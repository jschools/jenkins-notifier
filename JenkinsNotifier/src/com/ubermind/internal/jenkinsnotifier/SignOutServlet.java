package com.ubermind.internal.jenkinsnotifier;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ubermind.internal.jenkinsnotifier.util.AuthUtil;

@SuppressWarnings("serial")
public class SignOutServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AuthUtil.logOut(req);
		resp.getWriter().write("You have been signed out.");

	}
}
