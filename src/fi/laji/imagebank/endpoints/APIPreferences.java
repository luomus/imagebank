package fi.laji.imagebank.endpoints;

import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.laji.imagebank.models.User;
import fi.laji.imagebank.util.Constant;
import fi.luomus.commons.services.ResponseData;

@WebServlet(urlPatterns = {"/api/preferences/*"})
public class APIPreferences extends APIBaseServlet {

	private static final long serialVersionUID = 4529091225338013409L;

	private static final Set<Character> ALLOWED =
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.-_,".chars().mapToObj(c -> (char) c).collect(Collectors.toSet());

	@Override
	protected ResponseData processPost(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String userId = getUserId(req);
		if (userId == null) return status403(res);

		String preference = clean(req.getParameter("preference"));
		String value = clean(req.getParameter("value"));
		if (preference == null) return status(400, res);
		if (value == null) return status(400, res);

		getDAO().savePreference(userId, preference, value);

		getSession(req, true).setObject(Constant.PREFERENCES, getDAO().getPreferences(userId));

		return new ResponseData("ok", "text/plain");
	}

	private String clean(String s) {
		if (s == null) return null;
		s = s.chars()
				.filter(c -> ALLOWED.contains((char) c))
				.mapToObj(c -> String.valueOf((char) c))
				.collect(Collectors.joining());
		if (s.isEmpty()) return null;
		return s;
	}

	private String getUserId(HttpServletRequest req) {
		String userId = null;
		User user = getUser(req);
		if (user != null) {
			if (user.getId() != null) {
				userId = user.getId().toString();
			}
		}
		return userId;
	}


}
