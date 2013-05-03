package com.ubermind.internal.jenkinsnotifier;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class NotifySubscribersServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Key key = KeyFactory.stringToKey(req.getParameter("key"));

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Entity build = datastore.get(key);
		}
		catch (EntityNotFoundException e) {
			e.printStackTrace();
			resp.sendError(404);
		}

	}
}
