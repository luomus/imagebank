package fi.laji.imagebank.endpoints;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class APIBaseServlet extends ImageBankBaseServlet {

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

}
