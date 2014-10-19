package com.gamehitch.controllers.crud;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gamehitch.entities.Negotiation;
import com.gamehitch.entities.User;

@Path("/crud/negotiations")
public class NegotiationCRUD {
	@GET @Produces(MediaType.APPLICATION_JSON)
	public List<Negotiation> getA(){
		List <Negotiation> a = ofy().load().type(Negotiation.class).list();
		return a;
	}
	
	
}
