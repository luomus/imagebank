package fi.laji.imagebank.endpoints;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.luomus.commons.services.ResponseData;

@WebServlet(urlPatterns = {"/ping/*"})
public class Ping extends ImageBankBaseServlet {

	private static final long serialVersionUID = 7198887954614306466L;

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if ("true".equals(req.getParameter("test-exception"))) throw new ServletException("Testing exception");
		return new ResponseData("ok", "text/plain");
	}

}
