package fi.laji.imagebank.endpoints;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.luomus.commons.services.ResponseData;

public class APIAdminBaseServlet extends Admin {

	private static final long serialVersionUID = -3442868408125642254L;

	@Override
	protected void handleException(Exception e, HttpServletRequest req, HttpServletResponse res) throws ServletException {
		try {
			status500(res);
			getSession(req).setFlashError(e.getMessage());
		} catch (Exception e1) {
			throw new ServletException(e);
		}
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
