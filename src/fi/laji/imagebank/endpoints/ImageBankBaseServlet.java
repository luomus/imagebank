package fi.laji.imagebank.endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zaxxer.hikari.HikariDataSource;

import fi.laji.imagebank.dao.DataSourceDefinition;
import fi.laji.imagebank.models.User;
import fi.laji.imagebank.util.Constant;
import fi.luomus.commons.services.BaseServlet;
import fi.luomus.commons.services.ResponseData;
import fi.luomus.commons.session.SessionHandler;

public abstract class ImageBankBaseServlet extends BaseServlet {

	private static final long serialVersionUID = -8211770238148729310L;
	private static final Object LOCK = new Object();

	@Override
	protected String configFileName() {
		return "imagebank.properties";
	}

	@Override
	protected void applicationInit() {}

	@Override
	protected void applicationInitOnlyOnce() {}

	@Override
	protected void applicationDestroy() {
		try {
			if (dataSource != null) dataSource.close();
		} catch (Exception e) {}
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

	protected ResponseData initResponseData(HttpServletRequest req) {
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
		return responseData;
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

}
