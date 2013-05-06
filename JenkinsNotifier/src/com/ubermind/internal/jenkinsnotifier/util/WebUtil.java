package com.ubermind.internal.jenkinsnotifier.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.api.client.http.GenericUrl;

public class WebUtil {

	private static final String SESSION_ATTRIBUTE_FLASH = "flash";

	/**
	 * Builds a URL relative to this app's root.
	 */
	public static String buildUrl(HttpServletRequest req, String relativePath) {
		GenericUrl url = new GenericUrl(req.getRequestURL().toString());
		url.setRawPath(relativePath);
		return url.build();
	}

	/**
	 * A simple flash implementation for text messages across requests
	 *
	 * @param request
	 * @return
	 */
	public static String getClearFlash(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String flash = (String) session.getAttribute(SESSION_ATTRIBUTE_FLASH);
		session.removeAttribute(SESSION_ATTRIBUTE_FLASH);
		return flash;
	}

	public static void setFlash(HttpServletRequest request, String flashString) {
		HttpSession session = request.getSession();
		session.setAttribute(SESSION_ATTRIBUTE_FLASH, flashString);
	}
}
