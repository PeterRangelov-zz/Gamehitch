package com.gamehitch.controllers;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;

import com.balancedpayments.BankAccount;
import com.balancedpayments.errors.HTTPError;
import com.gamehitch.entities.Event;
import com.gamehitch.entities.Negotiation;
import com.gamehitch.entities.Sendgrid;
import com.gamehitch.entities.User;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.googlecode.objectify.cmd.Query;
import com.javadocmd.simplelatlng.LatLng;
import com.sun.jersey.api.container.MappableContainerException;

@Path("/event")
public class EventController {
	Gson gson = new Gson();
	
	@GET @Consumes(MediaType.APPLICATION_FORM_URLENCODED) @Produces(MediaType.APPLICATION_JSON)
	public String allEvents (@QueryParam("start") String start, @QueryParam("end") String end,
			@CookieParam("rmSessionId") String rmSessionId, @CookieParam("rmSchool") String rmSchool, @CookieParam("rmSportId") String rmSportId, @CookieParam("rmConference") String rmConference, @CookieParam("rmLat") double rmLat, @CookieParam("rmLon") double rmLon,
			@QueryParam("distance") double distance, @QueryParam("away") boolean away, @QueryParam("home") boolean home, @QueryParam("conferences") String selectedConferences) throws MappableContainerException {
		ArrayList<String> conferences = gson.fromJson(selectedConferences, new TypeToken<ArrayList<String>>(){}.getType());
		// projection query
		List<Event> myEvents = ofy().load().type(Event.class).filter("school", rmSchool).project("start").list();
		
		Set<String> myDates = new HashSet<String>();
		
		List<Event> excluded = new ArrayList<Event>();
		List<Event> returnEvents;
		
		for (Event event: myEvents) {
			myDates.add(event.getStart());
		}
		
		System.out.println("myDates: "+myDates);
		
		System.out.println("allEvents called");
		
		
		System.out.println("distance = "+distance);
		System.out.println("home? "+home);
		System.out.println("away? = "+away);
		System.out.println("conferences = "+conferences);
		
		LatLng myLocation = new LatLng(rmLat, rmLon);
		
		returnEvents = ofy().load().type(Event.class).filter("conference !=", rmConference).filter("start in", myDates).filter("conference in", conferences).list();
		
		
		if (home & away & distance >499){
			return gson.toJson(returnEvents);
		}
		
		
		
		
		for (Event event: returnEvents) {
			if ( !(myDates.contains(event.getStart())) || ( !event.getLocation().equalsIgnoreCase("away") && ! event.withinRange(myLocation, distance)) ||  ( !home && event.getLocation().equalsIgnoreCase("away") || !away && event.getLocation().equalsIgnoreCase(event.getSchool())) ) {
				excluded.add(event);
			}
			
		    
		}
		
		returnEvents.removeAll(excluded);
		
		System.out.println(returnEvents);
		
		return gson.toJson(returnEvents);
	
	}
}