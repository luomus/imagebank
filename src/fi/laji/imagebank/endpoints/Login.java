package fi.laji.imagebank.endpoints;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.laji.imagebank.util.LoginUtil;
import fi.luomus.commons.services.ResponseData;

@WebServlet(urlPatterns = {"/login"})
public class Login extends ImageBankBaseServlet {

	private static final long serialVersionUID = -6026570927088040555L;
	private static LoginUtil util;

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return util()
				.processGet(req, getSession(req, false), initResponseData(req));
	}

	@Override
	protected ResponseData processPost(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return util()
				.processPost(req, getSession(req), initResponseData(req));
	}

	private LoginUtil util() {
		if (util != null) return util;
		synchronized (this) {
			if (util != null) return util;
			util = new LoginUtil(getConfig(), getErrorReporter());
			return util;
		}
	}

}
