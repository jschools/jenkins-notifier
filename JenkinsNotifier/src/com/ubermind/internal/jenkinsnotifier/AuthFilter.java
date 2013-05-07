package com.ubermind.internal.jenkinsnotifier;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ubermind.internal.jenkinsnotifier.util.AuthUtil;
import com.ubermind.internal.jenkinsnotifier.util.WebUtil;

/**
 * A filter which ensures that prevents unauthenticated users from accessing the web app.
 *
 * (Modified from Mirror Java Starter Demo)
 */
public class AuthFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(AuthFilter.class.getSimpleName());

	private static final Set<String> nonAuthenticatedPaths;
	static {
		Set<String> paths = new HashSet<String>();
		paths.add("/completedBuilds");
		paths.add("/oauth2callback");
		paths.add("/notifySubscribers");
		nonAuthenticatedPaths = Collections.unmodifiableSet(paths);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		// filterChain.doFilter(request, response);
		// return;

		if (response instanceof HttpServletResponse && request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;

			// Redirect to https when on App Engine since subscriptions only work over https
			if (httpRequest.getServerName().contains("appspot.com") && httpRequest.getScheme().equals("http")) {

				httpResponse.sendRedirect(httpRequest.getRequestURL().toString().replaceFirst("http", "https"));
				return;
			}

			// Are we in the middle of an auth flow? IF so skip check.
			if (nonAuthenticatedPaths.contains(httpRequest.getRequestURI())) {
				LOG.info("Skipping auth check during auth flow");
				filterChain.doFilter(request, response);
				return;
			}

			// Is this a robot visit to the notify servlet? If so skip check
			if (httpRequest.getMethod().equals("POST") && nonAuthenticatedPaths.contains(httpRequest.getRequestURI())) {
				LOG.info("Skipping auth check for notify servlet");
				filterChain.doFilter(request, response);
				return;
			}

			LOG.fine("Checking to see if anyone is logged in");
			if (AuthUtil.getUserId(httpRequest) == null || AuthUtil.getCredential(AuthUtil.getUserId(httpRequest)) == null
					|| AuthUtil.getCredential(AuthUtil.getUserId(httpRequest)).getAccessToken() == null) {
				// redirect to auth flow
				httpResponse.sendRedirect(WebUtil.buildUrl(httpRequest, "/oauth2callback"));
				return;
			}

			// Things checked out OK :)
			filterChain.doFilter(request, response);
		}
		else {
			LOG.warning("Unexpected non HTTP servlet response. Proceeding anyway.");
			filterChain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
