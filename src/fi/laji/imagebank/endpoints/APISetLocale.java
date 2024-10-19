package fi.laji.imagebank.endpoints;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.luomus.commons.services.ResponseData;

@WebServlet(urlPatterns = {"/api/set-locale/*"})
public class APISetLocale extends ImageBankBaseServlet {

	private static final long serialVersionUID = 5707703585398451057L;

	@Override
	protected ResponseData processPost(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String locale = getLocale(req);
		getSession(req).setObject("locale", locale);
		return status(200, res);
	}

}
