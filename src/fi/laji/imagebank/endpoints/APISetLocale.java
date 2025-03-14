package fi.laji.imagebank.endpoints;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.laji.imagebank.models.User;
import fi.laji.imagebank.util.Constant;
import fi.luomus.commons.services.ResponseData;

@WebServlet(urlPatterns = {"/api/set-locale/*"})
public class APISetLocale extends APIBaseServlet {

	private static final long serialVersionUID = 5707703585398451057L;

	@Override
	protected ResponseData processPost(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String locale = getLocaleParameter(req);
		getSession(req).setObject(Constant.LOCALE, locale);
		User user = getUser(req);
		if (user != null) {
			getDAO().savePreference(user.getId().toString(), Constant.LOCALE, locale);
		}
		return new ResponseData("ok", "text/plain");
	}

}
