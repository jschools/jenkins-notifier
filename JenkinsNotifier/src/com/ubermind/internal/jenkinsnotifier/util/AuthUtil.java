package com.ubermind.internal.jenkinsnotifier.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.extensions.appengine.auth.oauth2.AppEngineCredentialStore;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.json.jackson.JacksonFactory;

public class AuthUtil {

	private static final String OAUTH_PLIST_FILE = "oauth.properties";

	@SuppressWarnings("unused")
	private static final String GLASS_SCOPE = "https://www.googleapis.com/auth/glass.timeline "
											+ "https://www.googleapis.com/auth/glass.location "
											+ "https://www.googleapis.com/auth/userinfo.profile";

	private static final String TASKS_SCOPE = "https://www.googleapis.com/auth/userinfo.profile "
											+ "https://www.googleapis.com/auth/tasks";

	/**
	 * Creates and returns a new {@link AuthorizationCodeFlow} for this app.
	 */
	public static AuthorizationCodeFlow newAuthorizationCodeFlow() throws IOException {
		FileInputStream authPropertiesStream = new FileInputStream(OAUTH_PLIST_FILE);
		Properties authProperties = new Properties();
		authProperties.load(authPropertiesStream);

		String clientId = authProperties.getProperty("client_id");
		String clientSecret = authProperties.getProperty("client_secret");

		return new GoogleAuthorizationCodeFlow.Builder(new UrlFetchTransport(), new JacksonFactory(), clientId, clientSecret,
				Collections.singleton(TASKS_SCOPE)).setAccessType("offline").setCredentialStore(getCredentialStore()).build();
	}

	/**
	 * Get the current user's ID from the session
	 *
	 * @return string user id or null if no one is logged in
	 */
	public static String getUserId(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (String) session.getAttribute(SessionVars.USER_ID);
	}

	public static void setUserId(HttpServletRequest request, String userId) {
		HttpSession session = request.getSession();
		session.setAttribute(SessionVars.USER_ID, userId);
	}

	public static void logOut(HttpServletRequest request) throws IOException {
		// Remove their ID from the local session
		request.getSession().removeAttribute(SessionVars.USER_ID);
	}

	public static void deAuth(HttpServletRequest request) throws IOException {
		// log the user out
		logOut(request);

		// Delete the credential in the credential store
		String userId = getUserId(request);
		getCredentialStore().delete(userId, getCredential(userId));
	}

	public static Credential getCredential(String userId) throws IOException {
		if (userId == null) {
			return null;
		}

		return AuthUtil.newAuthorizationCodeFlow().loadCredential(userId);
	}

	public static Credential getCredential(HttpServletRequest req) throws IOException {
		return AuthUtil.newAuthorizationCodeFlow().loadCredential(getUserId(req));
	}

	private static CredentialStore getCredentialStore() {
		return new AppEngineCredentialStore();
	}

	public interface SessionVars {
		public static final String USER_ID = "userId";
	}
}
