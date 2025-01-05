package fi.laji.imagebank.util;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.laji.imagebank.models.User;
import fi.luomus.commons.config.Config;
import fi.luomus.commons.reporting.ErrorReporter;
import fi.luomus.commons.services.ResponseData;
import fi.luomus.commons.session.SessionHandler;
import fi.luomus.commons.utils.Utils;
import fi.luomus.lajiauth.model.AuthenticationEvent;
import fi.luomus.lajiauth.model.UserDetails;
import fi.luomus.lajiauth.service.LajiAuthClient;

public class LoginUtil  {

	private static class AuthenticationResult {

		private final boolean success;
		private String errorMessage;
		private User user;
		private String next;

		public AuthenticationResult(boolean success) {
			this.success = success;
		}

		public boolean successful() {
			return success;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public AuthenticationResult setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
			return this;
		}

		public String getNext() {
			return next;
		}

		public void setNext(String next) {
			this.next = next;
		}
	}

	private final ErrorReporter errorReporter;
	private final Config config;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public LoginUtil(Config config, ErrorReporter errorReporter) {
		this.config = config;
		this.errorReporter = errorReporter;
	}

	public ResponseData processGet(HttpServletRequest req, SessionHandler session, ResponseData responseData) throws Exception {
		if (session.isAuthenticatedFor(config.systemId())) {
			return responseData.setRedirectLocation(config.baseURL());
		}
		return responseData.setRedirectLocation(getLajiAuthRedirect(req.getParameter("next"), responseData.getDefaultLocale()));
	}

	private String getLajiAuthRedirect(String next, String language) throws URISyntaxException {
		LajiAuthClient client = getLajiAuthClient();
		if (next == null) next = "";
		return client.createLoginUrl(next).language(language).build().toString();
	}

	public ResponseData processPost(HttpServletRequest req, SessionHandler session, ResponseData responseData) throws Exception {
		String lajiAuthToken = req.getParameter("token");
		try {
			AuthenticationResult authentication = authenticateViaLajiAuthentication(lajiAuthToken);
			if (authentication.successful()) {
				authenticateSession(session, authentication);
				if (nextGiven(authentication)) {
					return responseData.setRedirectLocation(config.baseURL() + authentication.getNext());
				}
				return responseData.setRedirectLocation(config.baseURL());
			}
			getLajiAuthClient().invalidateToken(lajiAuthToken);
			session.setFlashError(authentication.getErrorMessage());
			return responseData.setRedirectLocation(config.baseURL());
		} catch (Throwable e) {
			errorReporter.report("Login data " + lajiAuthToken, e);
			session.setFlashError("Login failed: " + e.getMessage());
			return responseData.setRedirectLocation(config.baseURL());
		}
	}

	private boolean nextGiven(AuthenticationResult authentication) {
		String next = authentication.getNext();
		if (!given(next)) return false;
		if (next.equals("/")) return false;
		return true;
	}

	private void authenticateSession(SessionHandler session, AuthenticationResult authentication) throws Exception {
		session.authenticateFor(config.systemId());
		session.setUserId(authentication.getUser().getId().toString());
		session.setUserName(authentication.getUser().getFullName());
		session.setUserType(authentication.getUser().getType().toString());
		session.setObject(Constant.USER, authentication.getUser());
		session.setTimeout(60 * 42);
	}

	private AuthenticationResult authenticateViaLajiAuthentication(String token) throws Exception {
		AuthenticationEvent authorizationInfo = null;
		try {
			LajiAuthClient client = getLajiAuthClient();
			authorizationInfo = client.getAndValidateAuthenticationInfo(token);
			// Validation throws exception if something is wrong; Authentication has been successful:

			return authenticationResultFromLajiAuth(authorizationInfo);
		} catch (Exception e) {
			if (authorizationInfo != null) {
				errorReporter.report("Erroreous LajiAuth login for " + Utils.debugS(token, objectMapper.writeValueAsString(authorizationInfo)), e);
			} else {
				errorReporter.report("Unsuccesful LajiAuth login for " + token, e);
			}
			AuthenticationResult authenticationResult = new AuthenticationResult(false);
			authenticationResult.setErrorMessage(e.getMessage());
			return authenticationResult;
		}
	}

	private AuthenticationResult authenticationResultFromLajiAuth(AuthenticationEvent authenticationEvent) throws IllegalAccessException {
		AuthenticationResult authenticationResponse = new AuthenticationResult(true);
		UserDetails userDetails = authenticationEvent.getUser();
		if (!userDetails.getQname().isPresent()) throw new IllegalAccessException("Unknown user");

		authenticationResponse.setUser(new User(userDetails.getQname().get(), userDetails.getRoles(), userDetails.getName()));
		authenticationResponse.setNext(authenticationEvent.getNext());
		return authenticationResponse;
	}

	private LajiAuthClient getLajiAuthClient() throws URISyntaxException {
		return new LajiAuthClient(config.get("SystemQname"), new URI(config.get("LajiAuthURL")));
	}

	private boolean given(String s) {
		return s != null && s.trim().length() > 0;
	}
}
