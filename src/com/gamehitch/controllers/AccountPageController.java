package com.gamehitch.controllers;
import static com.googlecode.objectify.ObjectifyService.ofy;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gamehitch.entities.User;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.gson.Gson;

@Path("/accountpage")
public class AccountPageController {
	Gson gson = new Gson();
	
	@GET @Path("/getinfo") @Produces(MediaType.APPLICATION_JSON)
	public User getBySessionId(@CookieParam("rmSessionId") String rmSessionId) throws JSONException{
		System.out.println(rmSessionId);
		User user = ofy().consistency(Consistency.STRONG).load().type(User.class).filter("sessionIds", rmSessionId).first().now();
		return user;
	}
	
	// IMPLEMENT A MAIN UPDATE METHOD
	@POST @Path ("/update") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
	public void update(@CookieParam("rmSessionId") String rmSessionId, @FormParam("firstName") String firstName, @FormParam("lastName") String lastName, @FormParam("emailAddress") String emailAddress, @FormParam("phoneNumber") String phoneNumber){
		
		User user = ofy().load().type(User.class).filter("sessionIds", rmSessionId).first().now();
		// SET PROPERTIES
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmailAddress(emailAddress);
		user.setPhoneNumber(phoneNumber);

		ofy().save().entity(user);
		
	}
	
	
}