package fi.laji.imagebank.endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zaxxer.hikari.HikariDataSource;

import fi.laji.imagebank.dao.DataSourceDefinition;
import fi.luomus.commons.services.BaseServlet;
import fi.luomus.commons.services.ResponseData;

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
		ResponseData responseData = new ResponseData().setDefaultLocale("en");
		return responseData;
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
