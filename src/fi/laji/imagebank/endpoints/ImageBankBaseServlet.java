package fi.laji.imagebank.endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zaxxer.hikari.HikariDataSource;

import fi.laji.imagebank.dao.DataSourceDefinition;
import fi.laji.imagebank.dao.TaxonImageDAO;
import fi.laji.imagebank.dao.TaxonImageDAOImple;
import fi.laji.imagebank.dao.TaxonomyDAOImple;
import fi.laji.imagebank.models.User;
import fi.laji.imagebank.util.Constant;
import fi.luomus.commons.containers.rdf.Qname;
import fi.luomus.commons.db.connectivity.ConnectionDescription;
import fi.luomus.commons.services.BaseServlet;
import fi.luomus.commons.services.ResponseData;
import fi.luomus.commons.session.SessionHandler;
import fi.luomus.commons.taxonomy.TaxonSearchDataSourceDefinition;
import fi.luomus.commons.taxonomy.TaxonomyDAO;
import fi.luomus.kuvapalvelu.client.MediaApiClient;
import fi.luomus.kuvapalvelu.client.MediaApiClientImpl;

public abstract class ImageBankBaseServlet extends BaseServlet {

	private static final Qname MM_TYPE_ENUM = new Qname("MM.typeEnum");
	private static final Qname MM_SIDE_ENUM = new Qname("MM.sideEnum");
	private static final Qname MY_SEXES = new Qname("MY.sexes");
	private static final Qname MY_PLANT_LIFE_STAGES = new Qname("MY.plantLifeStageEnum");
	private static final Qname MY_LIFE_STAGES = new Qname("MY.lifeStages");
	private static final Qname MZ_INTELLECTUAL_RIGHTS_ENUM = new Qname("MZ.intellectualRightsEnum");
	private static final long serialVersionUID = -8211770238148729310L;
	public static final String CONFIG_FILE = "imagebank.properties";
	private static final Object LOCK = new Object();

	@Override
	protected String configFileName() {
		return CONFIG_FILE;
	}

	@Override
	protected void applicationInit() {
		try {
			getTaxonomyDAO().getTaxon(new Qname("MX.1"));
		} catch (Exception e) {
			getErrorReporter().report(e);
		}
	}

	@Override
	protected void applicationInitOnlyOnce() {}

	@Override
	protected void applicationDestroy() {
		try { if (dataSource != null) dataSource.close(); } catch (Exception e) {}
		try { if (taxonomyDAO != null) taxonomyDAO.close(); } catch (Exception e) {}
		try { if (taxonImageDAO != null) taxonImageDAO.close(); } catch (Exception e) {}
		try { if (triplestoreDataSource != null) triplestoreDataSource.close(); } catch (Exception e) {}
	}

	private static HikariDataSource dataSource = null;

	protected HikariDataSource getDataSource() {
		if (dataSource == null) {
			synchronized (LOCK) {
				if (dataSource == null) {
					dataSource = DataSourceDefinition.initDataSource(getConfig().connectionDescription());
				}
			}
		}
		return dataSource;
	}

	private static HikariDataSource triplestoreDataSource = null;

	protected HikariDataSource getTriplestoreDataSource() {
		if (triplestoreDataSource == null) {
			synchronized (LOCK) {
				if (triplestoreDataSource == null) {
					triplestoreDataSource = TaxonSearchDataSourceDefinition.initDataSource(getConfig().connectionDescription("Taxonomy"));
				}
			}
		}
		return triplestoreDataSource;
	}

	private static TaxonomyDAOImple taxonomyDAO;

	protected TaxonomyDAO getTaxonomyDAO() {
		if (taxonomyDAO == null) {
			synchronized (LOCK) {
				if (taxonomyDAO == null) {
					taxonomyDAO = new TaxonomyDAOImple(getConfig(), getErrorReporter(), getTriplestoreDataSource());
				}
			}
		}
		return taxonomyDAO;
	}


	private static TaxonImageDAOImple taxonImageDAO;

	protected TaxonImageDAO getTaxonImageDAO() {
		if (taxonImageDAO == null) {
			synchronized (LOCK) {
				if (taxonImageDAO == null) {
					taxonImageDAO = new TaxonImageDAOImple(getTriplestoreDataSource(), getTaxonomyDAO(), getErrorReporter());
				}
			}
		}
		return taxonImageDAO;
	}

	private static MediaApiClient mediaApiClient;

	protected MediaApiClient getMediaApiClient() {
		if (mediaApiClient == null) {
			synchronized (LOCK) {
				if (mediaApiClient == null) {
					ConnectionDescription desc = getConfig().connectionDescription("MediaAPI");
					mediaApiClient = MediaApiClientImpl.builder()
							.uri(desc.url())
							.username(desc.username())
							.password(desc.password())
							.build();
				}
			}
		}
		return mediaApiClient;
	}

	protected ResponseData initResponseData(HttpServletRequest req) throws Exception {
		SessionHandler session = getSession(req, false);
		String locale = getSetLocale(req, session);
		ResponseData responseData = new ResponseData().setDefaultLocale(locale);
		if (session.hasSession()) {
			if (session.isAuthenticatedFor(getConfig().systemId())) {
				User user = (User) session.getObject(Constant.USER);
				if (user != null) {
					responseData.setData(Constant.USER, user);
				}
			}
			String flashError = session.getFlashError();
			if (given(flashError)) responseData.setData("errorMessage", flashError);
		}
		TaxonomyDAO dao = getTaxonomyDAO();
		responseData.setData("taxonRanks", dao.getTaxonRankLabels());
		responseData.setData("licenses", dao.getAlt(MZ_INTELLECTUAL_RIGHTS_ENUM));
		responseData.setData("lifeStages", dao.getAlt(MY_LIFE_STAGES));
		responseData.setData("plantLifeStages", dao.getAlt(MY_PLANT_LIFE_STAGES));
		responseData.setData("sexes", dao.getAlt(MY_SEXES));
		responseData.setData("sides", dao.getAlt(MM_SIDE_ENUM));
		responseData.setData("types", dao.getAlt(MM_TYPE_ENUM));
		return responseData;
	}

	protected User getUser(HttpServletRequest req) {
		SessionHandler session = getSession(req, false);
		if (!session.hasSession()) return null;
		if (!session.isAuthenticatedFor(getConfig().systemId())) return null;
		User user = (User) session.getObject(Constant.USER);
		if (user !=  null) return user;
		try {
			String id = session.userId();
			User.Type type = User.Type.valueOf(session.userType());
			String name = session.userName();
			user = new User(id, type, name);
			session.setObject(Constant.USER, user);
			return user;
		} catch (Exception e) {
			// user does not validate
			session.removeAuthentication(getConfig().systemId());
			return null;
		}
	}

	protected String getText(String text, HttpServletRequest req) {
		return getLocalizedTexts().getText(text, getSetLocale(req, getSession(req, false)));
	}

	private String getSetLocale(HttpServletRequest req, SessionHandler session) {
		if (!session.hasSession()) return getLocale(req);
		String sessionLocale = session.get("locale");
		if (sessionLocale == null) {
			sessionLocale = getLocale(req);
			session.setObject("locale", sessionLocale);
		}
		return sessionLocale;
	}

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return status404(res);
	}

	@Override
	protected ResponseData processPost(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return status404(res);
	}

	@Override
	protected ResponseData processPut(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return status404(res);
	}

	@Override
	protected ResponseData processDelete(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return status404(res);
	}

	protected String getNext(HttpServletRequest req) {
		String originalURL = getURL(req);
		return originalURL.replace("/imagebank", "").replace(getConfig().baseURL(), "");
	}

	protected static String getURL(HttpServletRequest req) {
		String servletPath = req.getServletPath();   // /servlet/MyServlet
		String pathInfo = req.getPathInfo();         // /a/b;c=123
		String queryString = req.getQueryString();   // d=789

		// Reconstruct original requesting URL
		StringBuilder url = new StringBuilder();
		url.append(servletPath);
		if (pathInfo != null) {
			url.append(pathInfo);
		}
		if (queryString != null) {
			url.append("?").append(queryString);
		}
		return url.toString();
	}

}
