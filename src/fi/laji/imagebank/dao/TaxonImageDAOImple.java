package fi.laji.imagebank.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.zaxxer.hikari.HikariDataSource;

import fi.luomus.commons.containers.Content;
import fi.luomus.commons.containers.Image;
import fi.luomus.commons.containers.LocalizedText;
import fi.luomus.commons.containers.rdf.Qname;
import fi.luomus.commons.db.connectivity.SimpleTransactionConnection;
import fi.luomus.commons.db.connectivity.TransactionConnection;
import fi.luomus.commons.reporting.ErrorReporter;
import fi.luomus.commons.taxonomy.Taxon;
import fi.luomus.commons.taxonomy.TaxonomyDAO;
import fi.luomus.commons.taxonomy.TripletToImageHandlers;
import fi.luomus.commons.utils.Utils;

public class TaxonImageDAOImple implements TaxonImageDAO {

	private final HikariDataSource dataSource;
	private final TaxonomyDAO taxonomyDAO;
	private final ErrorReporter errorReporter;

	public TaxonImageDAOImple(HikariDataSource dataSource, TaxonomyDAO taxonomyDAO, ErrorReporter errorReporter) {
		this.dataSource = dataSource;
		this.taxonomyDAO = taxonomyDAO;
		this.errorReporter = errorReporter;
	}

	@Override
	public void close() throws Exception {
		// TODO nothing for now...  remove if not needed at all
	}

	@Override
	public Taxon reloadImages(Taxon t) {
		try {
			List<Image> images = images(t.getId());
			t.clearMultimedia();
			for (Image i : images) {
				t.addMultimedia(i, t);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Loading images for " + t.getId(), e);
		}
		return t;
	}

	private static final String SINGLE_TAXON_SQL = "" +
			" SELECT   subjectname, predicatename, objectname, resourceliteral, langcodefk " +
			" FROM     rdf_statementview " +
			" WHERE    subjectname IN ( SELECT subjectname FROM rdf_statementview WHERE predicatename = 'MM.taxonURI' AND objectname = ?) " +
			" AND      subjectname IN ( SELECT subjectname FROM rdf_statementview WHERE predicatename = 'rdf:type'    AND objectname = 'MM.image') " +
			" ORDER BY subjectname, predicatename ";

	private List<Image> images(Qname taxonId) throws SQLException {
		List<Image> images = new ArrayList<>();
		TransactionConnection con = null;
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			con = new SimpleTransactionConnection(dataSource.getConnection());
			p = con.prepareStatement(SINGLE_TAXON_SQL);
			p.setString(1, taxonId.toString());
			rs = p.executeQuery();
			rs.setFetchSize(4001);
			Qname prevImageId = new Qname("");
			Image image = null;
			while (rs.next()) {
				Qname imageId = new Qname(rs.getString(1));
				Qname predicate = new Qname(rs.getString(2));
				Qname objectname = new Qname(rs.getString(3));
				String resourceliteral = rs.getString(4);
				String locale = rs.getString(5);
				if (imageChanges(prevImageId, imageId)) {
					add(image, images, taxonId);
					prevImageId = imageId;
					image = new Image(imageId, Content.DEFAULT_DESCRIPTION_CONTEXT);
				}
				addStatementToImage(image, predicate, objectname, resourceliteral, locale);
			}
			add(image, images, taxonId);
		} finally {
			Utils.close(p, rs, con);
		}
		return images;
	}

	private static final TripletToImageHandlers TRIPLET_TO_IMAGE_HANDLERS = new TripletToImageHandlers();

	private void addStatementToImage(Image image, Qname predicate, Qname objectname, String resourceliteral, String locale) {
		TRIPLET_TO_IMAGE_HANDLERS
		.getHandler(predicate)
		.setToImage(objectname, resourceliteral, locale, image, null);
	}

	private void add(Image image, List<Image> images, Qname taxonId) {
		if (image == null) return;
		finalize(image, taxonId);
		images.add(image);
	}

	private void finalize(Image image, Qname taxonId) {
		addLicenseName(image);
		if (image.getKeywords().contains("primary")) {
			image.addPrimaryForTaxon(taxonId);
		}
		image.doLegacyConversions();
	}

	private void addLicenseName(Image image) {
		if (image.getLicenseId() == null) return;
		try {
			LocalizedText localized = taxonomyDAO.getLicenseFullnames().get(image.getLicenseId().toString());
			if (localized != null) {
				image.setLicenseFullname(localized);
			}
		} catch (Exception e) {
			errorReporter.report("Loading license names", e);
		}
	}

	private boolean imageChanges(Qname prevImageId, Qname imageId) {
		return !prevImageId.equals(imageId);
	}

}
