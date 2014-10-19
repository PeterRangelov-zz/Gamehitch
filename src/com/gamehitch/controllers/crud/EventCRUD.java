package com.gamehitch.controllers.crud;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gamehitch.entities.Event;
import com.gamehitch.entities.User;

@Path("/crud/events")
public class EventCRUD {
	@GET @Produces(MediaType.APPLICATION_JSON)
	public List<Event> getEvents(){
		List <Event> events = ofy().load().type(Event.class).list();
		return events;
	}
	
	
}
