package com.ubermind.internal.jenkinsnotifier;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.ubermind.internal.jenkinsnotifier.util.AuthUtil;
import com.ubermind.internal.jenkinsnotifier.util.WebUtil;

@SuppressWarnings("serial")
public class AuthServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(AuthServlet.class.getSimpleName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		// If something went wrong, log the error message.
		if (req.getParameter("error") != null) {
			LOG.severe("Something went wrong during auth: " + req.getParameter("error"));
			res.setContentType("text/plain");
			res.getWriter().write("Something went wrong during auth. Please check your log for details");
			return;
		}

		// If we have a code, finish the OAuth 2.0 dance
		if (req.getParameter("code") != null) {
			LOG.info("Got a code. Attempting to exchange for access token.");

			AuthorizationCodeFlow flow = AuthUtil.newAuthorizationCodeFlow();
			TokenResponse tokenResponse = flow.newTokenRequest(req.getParameter("code"))
					.setRedirectUri(WebUtil.buildUrl(req, "/oauth2callback")).execute();

			// Extract the Google User ID from the ID token in the auth response
			String userId = ((GoogleTokenResponse) tokenResponse).parseIdToken().getPayload().getUserId();

			LOG.info("Code exchange worked. User " + userId + " logged in.");

			// Set it into the session
			AuthUtil.setUserId(req, userId);
			flow.createAndStoreCredential(tokenResponse, userId);

			// The dance is done.
			// TODO new user stuff here
			System.out.println("Logged in successfully!");

			// Redirect back to index
			res.sendRedirect(WebUtil.buildUrl(req, "/"));
			return;
		}

		// Else, we have a new flow. Initiate a new flow.
		LOG.info("No auth context found. Kicking off a new auth flow.");

		AuthorizationCodeFlow flow = AuthUtil.newAuthorizationCodeFlow();
		GenericUrl url = flow.newAuthorizationUrl().setRedirectUri(WebUtil.buildUrl(req, "/oauth2callback"));
		url.set("approval_prompt", "auto");
		res.sendRedirect(url.build());
	}

}
