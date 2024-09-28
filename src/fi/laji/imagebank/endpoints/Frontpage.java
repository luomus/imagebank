package fi.laji.imagebank.endpoints;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.luomus.commons.services.ResponseData;

@WebServlet(urlPatterns = {"/"})
public class Frontpage extends ImageBankBaseServlet {

	private static final long serialVersionUID = 2460355892429955492L;

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return initResponseData(req).setViewName("frontpage");
	}

}
