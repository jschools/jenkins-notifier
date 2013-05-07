package com.ubermind.internal.jenkinsnotifier;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ubermind.internal.jenkinsnotifier.util.AuthUtil;
import com.ubermind.internal.jenkinsnotifier.util.WebUtil;

@SuppressWarnings("serial")
public class SignOutServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		AuthUtil.logOut(req);

		resp.setContentType("text/html");

		PrintWriter out = resp.getWriter();
		out.println("You have been signed out.<br />");
		String loginUrl = WebUtil.buildUrl(req, "/");
		out.printf("<a href=\"%s\">Log in</a>", loginUrl);
	}
}
