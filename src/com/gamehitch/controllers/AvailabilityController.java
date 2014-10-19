package com.gamehitch.controllers;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.gamehitch.entities.Event;
import com.gamehitch.entities.User;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.googlecode.objectify.cmd.Query;

@Path("/availability") @SuppressWarnings("unused")
public class AvailabilityController {
	
	Gson gson = new Gson();
	
	@GET @Path("/getdates") @Produces(MediaType.APPLICATION_JSON)
	public List<Event> getDates(@CookieParam("rmSchool") String rmSchool, @CookieParam("rmSportId") String rmSportId) throws JSONException{

		try {
		List<Event> events = ofy().load().type(Event.class).filter("school", rmSchool).list();
		return events;
		}
		catch (NullPointerException e){
			return new ArrayList<Event>();
		}
	}
	
	@POST @Path ("/setdates") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
	public void setDates(@CookieParam("rmSessionId") String rmSessionId, @CookieParam("rmSchool") String rmSchool, @CookieParam("rmSportId") String rmSportId, @CookieParam("rmConference") String rmConference, @CookieParam("rmLat") double lat, @CookieParam("rmLon") double lon, @FormParam("datesAvailableHome") String datesAvailableHome, @FormParam("datesAvailableAway") String datesAvailableAway){
		ArrayList<String> homeDates = gson.fromJson(datesAvailableHome, new TypeToken<ArrayList<String>>(){}.getType());
		ArrayList<String> awayDates = gson.fromJson(datesAvailableAway, new TypeToken<ArrayList<String>>(){}.getType());
		
		List<Event> events = new ArrayList<Event>();
		
		// determine RPI
		int myRpi;
		if (rmSchool.equalsIgnoreCase("UMBC")){
			myRpi=341;
		}
		else if (rmSchool.equalsIgnoreCase("JMU")){
			myRpi=45;
		}
		else {
			myRpi=71;
		}
		
		
		try {
			// DELETE EVENTS
			List<Event> eventsToDelete = ofy().load().type(Event.class).filter("school", rmSchool).list();
			ofy().delete().entities(eventsToDelete).now();
			
			// SAVE EVENTS
			
			for (String homeDate: homeDates) {
				events.add(new Event(null, homeDate, lat, lon, 5, rmSchool, rmConference, rmSchool, "@ "+rmSchool+" #"+myRpi));
			}
			
			for (String awayDate: awayDates) {
				events.add(new Event(null, awayDate, lat, lon, 5, rmSchool, rmConference, "away", "V "+rmSchool+" #"+myRpi));
			}
			
			
			
			ofy().save().entities(events);
			
			
		}
		catch (NullPointerException e){
			
		}
		
	}
	
	
	
	
}