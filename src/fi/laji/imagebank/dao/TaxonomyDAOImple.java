package fi.laji.imagebank.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.HttpGet;

import com.zaxxer.hikari.HikariDataSource;

import fi.laji.imagebank.dao.TaxonomyCaches.TreeTerms;
import fi.luomus.commons.config.Config;
import fi.luomus.commons.containers.LocalizedText;
import fi.luomus.commons.containers.rdf.Qname;
import fi.luomus.commons.db.connectivity.SimpleTransactionConnection;
import fi.luomus.commons.db.connectivity.TransactionConnection;
import fi.luomus.commons.http.HttpClientService;
import fi.luomus.commons.json.JSONObject;
import fi.luomus.commons.reporting.ErrorReporter;
import fi.luomus.commons.taxonomy.AdministrativeStatusContainer;
import fi.luomus.commons.taxonomy.HabitatOccurrenceCounts.HabitatOccurrenceCount;
import fi.luomus.commons.taxonomy.InMemoryTaxonContainerImple;
import fi.luomus.commons.taxonomy.InMemoryTaxonContainerImple.InfiniteTaxonLoopException;
import fi.luomus.commons.taxonomy.InformalTaxonGroupContainer;
import fi.luomus.commons.taxonomy.NoSuchTaxonException;
import fi.luomus.commons.taxonomy.Occurrences;
import fi.luomus.commons.taxonomy.Occurrences.Occurrence;
import fi.luomus.commons.taxonomy.Taxon;
import fi.luomus.commons.taxonomy.TaxonContainer;
import fi.luomus.commons.taxonomy.TaxonSearch;
import fi.luomus.commons.taxonomy.TaxonSearchDAOSQLQueryImple;
import fi.luomus.commons.taxonomy.TaxonSearchResponse;
import fi.luomus.commons.taxonomy.TaxonomyDAOBaseImple;
import fi.luomus.commons.taxonomy.iucn.HabitatObject;
import fi.luomus.commons.utils.Cached;
import fi.luomus.commons.utils.DateUtils;
import fi.luomus.commons.utils.FileUtils;
import fi.luomus.commons.utils.Utils;

public class TaxonomyDAOImple extends TaxonomyDAOBaseImple implements AutoCloseable, TaxonomyDAO {

	private static final List<String> INCLUDED_PREDICATES = Utils.list(
			"MX.scientificName",
			"MX.taxonRank",
			"MX.nameAccordingTo",
			"MX.isPartOf",
			"MX.scientificNameAuthorship",
			"MX.vernacularName",
			"sortOrder",
			"MX.finnish",
			"MX.occurrenceInFinland",
			"MX.typeOfOccurrenceInFinland",
			"MX.hasAdminStatus",
			"MX.isPartOfInformalTaxonGroup",
			"MX.isPartOfSet",
			"MX.alternativeVernacularName",
			"MX.obsoleteVernacularName",
			"MX.hiddenTaxon",
			"MX.stopInformalTaxonGroupInheritance",
			"MX.colloquialVernacularName",
			"MX.redListStatus2000Finland",
			"MX.redListStatus2010Finland",
			"MX.redListStatus2015Finland",
			"MX.redListStatus2019Finland"
			);

	private static final String SEPARATOR = "\u001F";

	private static final Qname NOT_EVALUATED = new Qname("MX.typeOfOccurrenceNotEvaluated");
	private static final Qname BASED_ON_OCCURRENCES = new Qname("MX.typeOfOccurrenceOccursBasedOnOccurrences");

	private final Config config;
	private final ErrorReporter errorReporter;
	private final Cached<TaxonSearch, TaxonSearchResponse> cachedTaxonSearches;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final HikariDataSource dataSource;

	private TaxonContainer taxonContainer = null;
	private TaxonomyCaches caches = null;

	private static final Object LOCK = new Object();

	public TaxonomyDAOImple(Config config, ErrorReporter errorReporter, HikariDataSource dataSource) {
		super(config);
		this.config = config;
		this.errorReporter = errorReporter;
		this.dataSource = dataSource;
		this.taxonContainer = null;
		this.cachedTaxonSearches = new Cached<>(
				new TaxonSearchLoader(), 12, TimeUnit.HOURS, taxonSearchCacheSize());
		startNightlyTasks();
		this.caches = new TaxonomyCaches(this);
		System.out.println(this.getClass().getSimpleName() + " created!");
	}

	@Override
	public void close() {
		try {
			if (scheduler != null) {
				scheduler.shutdownNow();
			}
		} catch (Exception e) {}
	}

	private void startNightlyTasks() {
		long repeatPeriod = TimeUnit.DAYS.toSeconds(1);
		long initialDelay = calculateInitialDelayTill(5);
		scheduler.scheduleAtFixedRate(
				nightyTasks(),
				initialDelay, repeatPeriod, TimeUnit.SECONDS);
	}

	private Runnable nightyTasks() {
		return new Runnable() {
			@Override
			public void run() {
				try {
					reloadTriplets();
					reloadHabitats();
					reloadObsCounts();
					caches.clearCaches();
					cachedTaxonSearches.invalidateAll();
					taxonContainer = null;
					getTaxonContainer();
				} catch (Exception e) {
					errorReporter.report("Nightly scheduler", e);
				}
			}
		};
	}

	private long calculateInitialDelayTill(int hour) {
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime nextRun = now.withHour(hour).withMinute(0).withSecond(0);
		if (now.compareTo(nextRun) > 0)
			nextRun = nextRun.plusDays(1);

		Duration duration = Duration.between(now, nextRun);
		return duration.getSeconds();
	}

	private int taxonSearchCacheSize() {
		if (config.productionMode()) return 150000;
		return 1000;
	}

	@Override
	public Taxon getTaxon(Qname qname) throws NoSuchTaxonException {
		return getTaxonContainer().getTaxon(qname);
	}

	@Override
	public void clearCaches() {
		synchronized (LOCK) {
			taxonContainer = null;
			super.clearCaches();
		}
	}

	@Override
	public TaxonContainer getTaxonContainer() {
		if (taxonContainer == null) {
			synchronized (LOCK) {
				if (taxonContainer == null) {
					try {
						File tripletFile = getTripletFile();
						File habitatFile = getHabitatFile();
						File obsCountFile = getObsCountFile();
						taxonContainer = new TaxonContainerLoader(this, errorReporter, tripletFile, habitatFile, obsCountFile).load();
					} catch (Exception e) {
						throw new RuntimeException("Loading taxa failed", e);
					}
				}
			}
		}
		return taxonContainer;
	}

	public File reloadTriplets() throws Exception {
		synchronized (LOCK) {
			File finalFile = tripletFile();
			File backupFile = new File(storageFolder(), "taxon_triplets_BACKUP.txt");
			File tempFile = new TaxonTripletLoader(dataSource).load();
			if (finalFile.exists()) {
				if (backupFile.exists()) {
					backupFile.delete();
				}
				finalFile.renameTo(backupFile);
			}
			tempFile.renameTo(finalFile);
			System.out.println("Taxon triplets saved to " + finalFile.getAbsolutePath());
			return finalFile;
		}
	}

	public File reloadHabitats() throws Exception {
		synchronized (LOCK) {
			File finalFile = habitatFile();
			File backupFile = new File(storageFolder(), "taxon_habitats_BACKUP.txt");
			File tempFile = new HabitatLoader(dataSource).load();
			if (finalFile.exists()) {
				if (backupFile.exists()) {
					backupFile.delete();
				}
				finalFile.renameTo(backupFile);
			}
			tempFile.renameTo(finalFile);
			System.out.println("Taxon habitats saved to " + finalFile.getAbsolutePath());
			return finalFile;
		}
	}

	public File reloadObsCounts() throws Exception {
		synchronized (LOCK) {
			File finalFile = obsCountFile();
			File backupFile = new File(storageFolder(), "obs_count_BACKUP.txt");
			File tempFile = loadObsCounts();
			if (finalFile.exists()) {
				if (backupFile.exists()) {
					backupFile.delete();
				}
				finalFile.renameTo(backupFile);
			}
			tempFile.renameTo(finalFile);
			System.out.println("Observation counts saved to " + finalFile.getAbsolutePath());
			return finalFile;
		}
	}

	private class TaxonSearchLoader implements Cached.CacheLoader<TaxonSearch, TaxonSearchResponse> {
		@Override
		public TaxonSearchResponse load(TaxonSearch key) {
			try {
				return uncachedTaxonSearch(key);
			} catch (Exception e) {
				throw exceptionAndReport("Taxon search with terms " + key.toString(), e);
			}
		}
	}

	private TaxonSearchResponse uncachedTaxonSearch(TaxonSearch taxonSearch) throws Exception {
		return new TaxonSearchDAOSQLQueryImple(this, dataSource, "ltkm_luonto").search(taxonSearch);
	}

	public RuntimeException exceptionAndReport(String message, Exception e) {
		errorReporter.report(message, e);
		return new RuntimeException(message, e);
	}

	@Override
	public TaxonSearchResponse search(TaxonSearch taxonSearch) throws Exception {
		return cachedTaxonSearches.get(taxonSearch);
	}

	private File getTripletFile() throws Exception {
		File tripletFile = tripletFile();
		if (tripletFile.exists()) return tripletFile;
		synchronized (LOCK) {
			if (tripletFile.exists()) return tripletFile;
			tripletFile = reloadTriplets();
			return tripletFile;
		}
	}

	private File getHabitatFile() throws Exception {
		File habitatFile = habitatFile();
		if (habitatFile.exists()) return habitatFile;
		synchronized (LOCK) {
			if (habitatFile.exists()) return habitatFile;
			habitatFile = reloadHabitats();
			return habitatFile;
		}
	}

	private File getObsCountFile() throws Exception {
		File obsCountFile = obsCountFile();
		if (obsCountFile.exists()) return obsCountFile;
		synchronized (LOCK) {
			if (obsCountFile.exists()) return obsCountFile;
			obsCountFile = reloadObsCounts();
			return obsCountFile;
		}
	}

	private File tripletFile() {
		return new File(storageFolder(), "taxon_triplets.txt");
	}

	private File habitatFile() {
		return new File(storageFolder(), "taxon_habitats.txt");
	}

	private File obsCountFile() {
		return new File(storageFolder(), "obs_counts.json");
	}

	private File storageFolder() {
		return new File(config.baseFolder() + config.get("StorageFolder"));
	}

	private class HabitatLoader {

		private final HikariDataSource dataSource;

		public HabitatLoader(HikariDataSource dataSource) {
			this.dataSource = dataSource;
			System.out.println(this.getClass().getSimpleName() + " created!");
		}

		public File load() throws Exception {
			TransactionConnection con = null;
			try {
				System.out.println("Starting to load taxon habitats...");
				con = new SimpleTransactionConnection(dataSource.getConnection());
				File f = loadUsing(con);
				System.out.println("Taxon habitat loading done");
				return f;
			} finally {
				if (con != null) con.release();
			}
		}

		private File loadUsing(TransactionConnection con) throws Exception {
			PreparedStatement p = null;
			ResultSet rs = null;
			File tempFile = new File(storageFolder(), "taxon-habitats-"+DateUtils.getFilenameDatetime()+".txt");
			tempFile.getParentFile().mkdirs();
			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8"), 1024*1024)) {
				String sql = getHabitatLoadSQL();
				p = con.prepareStatement(sql);
				System.out.println("Executing habitat query...");
				rs = p.executeQuery();
				rs.setFetchSize(4001);
				System.out.println("Received first habitat query response, going through result set...");
				int i = 0;
				while (rs.next()) {
					write(rs, writer);
					i++;
					if (i % 100000 == 0 || i == 1) {
						System.out.println(" ... habitat row " + i);
					}
				}
				return tempFile;
			} catch (Exception e) {
				if (tempFile.exists()) {
					try { tempFile.delete(); } catch (Exception e2) {}
				}
				throw e;
			} finally {
				Utils.close(p, rs);
			}
		}

		private String getHabitatLoadSQL() {
			String sql = "" +
					" SELECT 	taxon.subjectname AS taxonId, " +
					" 			habitatId.objectname AS habitatId,  " +
					" 			habitatId.predicatename AS type,  " +
					" 			habitat.objectname AS habitat,  " +
					" 			sortOrder.resourceliteral AS sortOrder, " +
					" 			habitatSpecificType.objectname AS habitatSpecificType " +
					" FROM 		rdf_statementview taxon " +
					" JOIN 		rdf_statementview habitatId ON taxon.subjectname = habitatId.subjectname AND habitatId.predicatename IN ('MKV.primaryHabitat', 'MKV.secondaryHabitat') " +
					" JOIN 		rdf_statementview habitat ON habitatId.objectname = habitat.subjectname AND habitat.predicatename = 'MKV.habitat' " +
					" LEFT JOIN rdf_statementview sortOrder ON habitatId.objectname = sortOrder.subjectname AND sortOrder.predicatename = 'sortOrder' " +
					" LEFT JOIN	rdf_statementview habitatSpecificType ON habitatId.objectname = habitatSpecificType.subjectname AND habitatSpecificType.predicatename = 'MKV.habitatSpecificType' " +
					" WHERE 	taxon.predicatename = 'rdf:type' " +
					" AND		taxon.objectname = 'MX.taxon' " +
					" ORDER BY	taxonId, habitatId, type ";
			return sql;
		}

		private void write(ResultSet rs, BufferedWriter writer) throws SQLException, IOException {
			//					// MX.59808	MKV.383407	MKV.primaryHabitat		MKV.habitatMkk	0	MKV.habitatSpecificTypeP
			//					// MX.59808	MKV.383407	MKV.primaryHabitat		MKV.habitatMkk	0	MKV.habitatSpecificTypeH
			//					// MX.59808	MKV.383407	MKV.primaryHabitat		MKV.habitatMkk	0	MKV.habitatSpecificTypePAK
			//					// MX.59808	MKV.383408	MKV.secondaryHabitat	MKV.habitatIu	0
			String taxonId = s(rs, 1);
			String habitatId = s(rs, 2);
			String type = s(rs, 3);
			String habitat = s(rs, 4);
			String order = s(rs, 5);
			String habitatSpecificType = s(rs, 6);
			w(writer, taxonId);
			w(writer, habitatId);
			w(writer, type);
			w(writer, habitat);
			w(writer, order);
			w(writer, habitatSpecificType);
			writer.write('\n');
		}
	}

	private static void w(BufferedWriter writer, String s) throws IOException {
		writer.write(s);
		writer.write(SEPARATOR);
	}

	private static String s(ResultSet rs, int i) throws SQLException {
		String s = rs.getString(i);
		if (s == null) return "";
		return s.replace(SEPARATOR, "").replace("\n", "").replace("\r", "");
	}

	private class TaxonTripletLoader {

		private final HikariDataSource dataSource;

		public TaxonTripletLoader(HikariDataSource dataSource) {
			this.dataSource = dataSource;
			System.out.println(this.getClass().getSimpleName() + " created!");
		}

		public File load() throws Exception {
			TransactionConnection con = null;
			try {
				System.out.println("Starting to load taxons triplets...");
				con = new SimpleTransactionConnection(dataSource.getConnection());
				File f = loadUsing(con);
				System.out.println("Taxon triplets loading done");
				return f;
			} finally {
				if (con != null) con.release();
			}
		}

		private File loadUsing(TransactionConnection con) throws Exception {
			PreparedStatement p = null;
			ResultSet rs = null;
			File tempFile = new File(storageFolder(), "taxon-triplets-"+DateUtils.getFilenameDatetime()+".txt");
			tempFile.getParentFile().mkdirs();
			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8"), 1024*1024)) {
				String sql = getTaxonLoadSQL();
				p = con.prepareStatement(sql);
				System.out.println("Executing taxon triplet query...");
				rs = p.executeQuery();
				rs.setFetchSize(4001);
				System.out.println("Received first taxon triplet query response, going through result set...");
				int i = 0;
				while (rs.next()) {
					write(rs, writer);
					i++;
					if (i % 100000 == 0 || i == 1) {
						System.out.println(" ... taxon triplet row " + i);
					}
				}
				return tempFile;
			} catch (Exception e) {
				if (tempFile.exists()) {
					try { tempFile.delete(); } catch (Exception e2) {}
				}
				throw e;
			} finally {
				Utils.close(p, rs);
			}
		}

		private String getTaxonLoadSQL() {
			StringBuilder sql = new StringBuilder()
					.append(" SELECT     s.subjectname, s.predicatename, s.objectname, s.resourceliteral, s.langcodefk, s.contextname ")
					.append(" FROM       rdf_statementview s ")
					.append(" JOIN       rdf_statementview c ON (c.subjectname = s.subjectname AND c.predicatename = 'MX.nameAccordingTo' AND c.objectname = 'MR.1') ")
					.append(" WHERE      s.subjectname IN (     ")
					.append(" 				SELECT distinct subjectname     ")
					.append(" 				FROM   rdf_statementview     ")
					.append(" 				WHERE  predicatename = 'rdf:type' ")
					.append(" 				AND    objectname = 'MX.taxon' ")
					.append(" ) ");
			sql.append(" AND s.predicatename IN ( ");
			Utils.toCommaSeperatedStatement(sql, INCLUDED_PREDICATES, true);
			sql.append(" ) ORDER BY s.subjectname ");
			return sql.toString();
		}

		private void write(ResultSet rs, BufferedWriter writer) throws SQLException, IOException {
			String taxonId = s(rs, 1);
			String predicate = s(rs, 2);
			String object = s(rs, 3);
			String resourceliteral = s(rs, 4);
			String locale = s(rs, 5);
			String context = s(rs, 6);
			w(writer, taxonId);
			w(writer, predicate);
			w(writer, object);
			w(writer, resourceliteral);
			w(writer, locale);
			w(writer, context);
			writer.write('\n');
		}

	}

	private class TaxonContainerLoader {

		private final File tripletFile;
		private final File habitatFile;
		private final File obsCountFile;
		private final TaxonomyDAO dao;
		private final ErrorReporter errorReporter;

		public TaxonContainerLoader(TaxonomyDAO dao, ErrorReporter errorReporter, File tripletFile, File habitatFile, File obsCountFile) {
			this.dao = dao;
			this.errorReporter = errorReporter;
			this.tripletFile = tripletFile;
			this.habitatFile = habitatFile;
			this.obsCountFile = obsCountFile;
			System.out.println(this.getClass().getSimpleName() + " created!");
		}

		public TaxonContainer load() throws Exception {
			System.out.println("Starting to create taxon container...");
			InMemoryTaxonContainerImple taxonContainer = load(tripletFile);
			addHabitats(taxonContainer, habitatFile);
			// TODO addBiogeographicalProvinceOccurrences(taxonContainer, con, possiblyLimitedIds);
			addObservationCounts(taxonContainer, obsCountFile);
			taxonContainer.generateInheritedOccurrencesAndHabitats();
			limitHabitatCountsToTop(10, taxonContainer);
			taxonContainer.setHasMediaFilter(Collections.emptySet());
			List<InfiniteTaxonLoopException> ex = taxonContainer.generateTaxonomicOrders();
			for (InfiniteTaxonLoopException e : ex) {
				errorReporter.report(e);
				e.printStackTrace();
			}
			System.out.println("Taxon container creation done");
			return taxonContainer;
		}


		private void limitHabitatCountsToTop(int top, InMemoryTaxonContainerImple taxonContainer) {
			for (Taxon t : taxonContainer.getAll()) {
				if (t.hasHabitatOccurrenceCounts()) {
					t.getHabitatOccurrenceCounts().retainTop(top);
				}
			}
		}

		private void addHabitats(InMemoryTaxonContainerImple taxonContainer, File habitatFile) throws NoSuchTaxonException, IOException {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(habitatFile), "UTF-8"), 1024*1024)) {
				System.out.println("Reading habitats from " + habitatFile.getAbsolutePath() + " ... ");
				String line = null;
				HabitatObject habitatObject = new HabitatObject(new Qname(""), null, -1);
				while ((line = reader.readLine()) != null) {
					// MX.59808	MKV.383407	MKV.primaryHabitat		MKV.habitatMkk	0	MKV.habitatSpecificTypeP
					// MX.59808	MKV.383407	MKV.primaryHabitat		MKV.habitatMkk	0	MKV.habitatSpecificTypeH
					// MX.59808	MKV.383407	MKV.primaryHabitat		MKV.habitatMkk	0	MKV.habitatSpecificTypePAK
					// MX.59808	MKV.383408	MKV.secondaryHabitat	MKV.habitatIu	0
					String[] parts = line.split(SEPARATOR, -1);
					Qname taxonId = new Qname(parts[0]);
					Qname habitatId = new Qname(parts[1]);
					String type = parts[2];
					Qname habitat = new Qname(parts[3]);
					int order = intV(parts[4]);
					Qname habitatSpecificType = new Qname(parts[5]);

					if (habitatId.equals(habitatObject.getId())) {
						habitatObject.addHabitatSpecificType(habitatSpecificType);
					} else {
						habitatObject = new HabitatObject(habitatId, habitat, order);
						if (habitatSpecificType.isSet()) {
							habitatObject.addHabitatSpecificType(habitatSpecificType);
						}
						if (!taxonContainer.hasTaxon(taxonId)) continue;
						Taxon taxon = taxonContainer.getTaxon(taxonId);
						if (type.equals("MKV.primaryHabitat")) {
							taxon.setPrimaryHabitat(habitatObject);
						} else {
							taxon.addSecondaryHabitat(habitatObject);
						}
					}
				}
			}
		}

		private int intV(String string) {
			if (string == null || string.isEmpty()) return 0;
			try {
				return Integer.valueOf(string);
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		private void addObservationCounts(InMemoryTaxonContainerImple taxonContainer, File obsCountFile) throws FileNotFoundException, IOException {
			System.out.println("Reading observation counts from " + obsCountFile.getAbsolutePath());
			JSONObject data = getObservationCountData(obsCountFile);
			addObservationCounts(taxonContainer, data);
		}

		private JSONObject getObservationCountData(File obsCountFile) throws FileNotFoundException, IOException {
			return new JSONObject(FileUtils.readContents(obsCountFile));
		}

		private void addObservationCounts(InMemoryTaxonContainerImple taxonContainer, JSONObject response) {
			response.getArray("results").iterateAsObject().forEach(counts -> {
				Qname taxonId = Qname.fromURI(counts.getString("taxonId"));
				if (!taxonContainer.hasTaxon(taxonId)) return;
				Taxon taxon = taxonContainer.getTaxon(taxonId);
				taxon.setExplicitObservationCount(counts.getInteger("count"));
				taxon.setExplicitObservationCountFinland(counts.getInteger("countFinland"));
				if (counts.hasKey("biogeographicalProvinceCounts")) {
					JSONObject provinceCounts = counts.getObject("biogeographicalProvinceCounts");
					for (String areaQname : provinceCounts.getKeys()) {
						int count = provinceCounts.getInteger(areaQname);
						addBiogeographicalProvinceCount(areaQname, count, taxon);
					}
				}
				if (counts.hasKey("habitatOccurrenceCounts")) {
					JSONObject habitatCounts = counts.getObject("habitatOccurrenceCounts");
					for (String habitat : habitatCounts.getKeys()) {
						int count = habitatCounts.getInteger(habitat);
						addHabitatCount(habitat, count, taxon);
					}
				}
			});
		}

		private void addHabitatCount(String habitat, int count, Taxon taxon) {
			if (count < 1) return;
			if (!validHabitat(habitat)) return;
			taxon.getHabitatOccurrenceCounts().setCount(
					new HabitatOccurrenceCount(habitat, localized(habitat))
					.setOccurrenceCount(count));
		}

		private boolean validHabitat(String habitat) {
			if (habitat == null) return false;
			habitat = habitat.trim();
			if (habitat.length() < 5) return false;
			return true;
		}

		private LocalizedText localized(String habitat) {
			if (habitat.startsWith("http://tun.fi")) {
				return localize(Qname.fromURI(habitat));
			}
			return localizedAsPlain(habitat);
		}

		private LocalizedText localizedAsPlain(String habitat) {
			return new LocalizedText().set("fi", habitat).set("en", habitat).set("sv", habitat);
		}

		private LocalizedText localize(Qname habitat) {
			try {
				LocalizedText localizedText = getLabels(habitat);
				if (localizedText.isEmpty()) return localizedAsPlain(habitat.toString());
				return localizedText;
			} catch (Exception e) {
				return localizedAsPlain(habitat.toString());
			}
		}

		private void addBiogeographicalProvinceCount(String areaQname, int count, Taxon taxon) {
			if (count < 1) return;
			Qname areaId = new Qname(areaQname);
			Occurrences occurrences = taxon.getOccurrences();
			Occurrence occurrence = occurrences.getOccurrence(areaId);
			if (occurrence == null) {
				occurrence = new Occurrence(null, areaId, BASED_ON_OCCURRENCES);
				occurrences.setOccurrence(occurrence);
			} else {
				if (occurrence.getStatus().equals(NOT_EVALUATED)) {
					occurrence.setStatus(BASED_ON_OCCURRENCES);
				}
			}
			occurrence.setOccurrenceCount(count);
		}

		private InMemoryTaxonContainerImple load(File tripletFile) throws Exception {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tripletFile), "UTF-8"), 1024*1024)) {
				System.out.println("Reading triplets from " + tripletFile.getAbsolutePath() + " ... ");
				InMemoryTaxonContainerImple taxonContainer = new InMemoryTaxonContainerImple(
						new InformalTaxonGroupContainer(dao.getInformalTaxonGroups()),
						new InformalTaxonGroupContainer(dao.getRedListEvaluationGroups()),
						new AdministrativeStatusContainer(dao.getAdministrativeStatuses()));
				int i = 0;
				String line = null;
				while ((line = reader.readLine()) != null) {
					addTaxonInformation(line, taxonContainer);
					i++;
					if (i % 100000 == 0 || i == 1) {
						System.out.println(" ... taxon triplet row " + i);
					}
				}
				List<InfiniteTaxonLoopException> ex = taxonContainer.generateTaxonomicOrders();
				for (InfiniteTaxonLoopException e : ex) {
					errorReporter.report(e);
					e.printStackTrace();
				}
				return taxonContainer;
			}
		}

		private void addTaxonInformation(String line, InMemoryTaxonContainerImple taxonContainer) {
			String[] parts = line.split(SEPARATOR, -1);
			Qname taxonId = new Qname(parts[0]);
			Qname predicate = new Qname(parts[1]);
			Qname object = new Qname(parts[2]);

			String resourceliteral = parts[3];
			if (resourceliteral.isEmpty()) resourceliteral = null;

			String locale = parts[4];
			if (locale.isEmpty()) locale = null;

			Qname context = new Qname(parts[5]);
			if (!context.isSet()) context = null;

			taxonContainer.handle(taxonId, predicate, object, resourceliteral, locale, context);
		}
	}

	private File loadObsCounts() throws Exception {
		File tempFile = new File(storageFolder(), "obs_counts-"+DateUtils.getFilenameDatetime()+".txt");
		tempFile.getParentFile().mkdirs();
		String url = config.get("DwURL");
		System.out.println("Calling " + url);
		try (HttpClientService client = new HttpClientService(); OutputStream out = new FileOutputStream(tempFile)) {
			client.contentToStream(new HttpGet(url), out);
		}
		return tempFile;
	}

	@Override
	public List<Taxon> getTree(TreeTerms terms) {
		return caches.getTree(terms);
	}

	@Override
	public Taxon next(Taxon self) {
		return caches.next(self);
	}

	@Override
	public Taxon prev(Taxon self) {
		return caches.prev(self);
	}

}
