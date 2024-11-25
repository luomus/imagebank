package fi.laji.imagebank.endpoints;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.luomus.commons.services.ResponseData;
import fi.luomus.commons.session.SessionHandler;

@WebServlet(urlPatterns = {"/logout"})
public class Logout extends ImageBankBaseServlet {

	private static final long serialVersionUID = -5412709583359956569L;

	@Override
	protected ResponseData processGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		ResponseData data = initResponseData(req);
		SessionHandler session = getSession(req, false);
		if (session.hasSession()) {
			session.invalidate();
		}
		return redirectTo(getConfig().baseURL()+"?locale="+data.getDefaultLocale());
	}

}
