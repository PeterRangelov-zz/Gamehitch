package com.gamehitch.controllers.crud;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gamehitch.entities.User;

@Path("/crud/users")
public class UserCRUD {
	@GET @Produces(MediaType.APPLICATION_JSON)
	public List<User> getUsers(){
		List <User> users = ofy().load().type(User.class).list();
		return users;
	}
	
	@GET @Path("/{userId}") @Produces(MediaType.APPLICATION_JSON)
	public User getUser(@PathParam("userId") Long userId){
		User user = ofy().load().type(User.class).id(userId).now();
		return user;
	}

	
	
	@DELETE @Path("/{userId}")
	public void deleteDentist(@PathParam("userId") Long userId){
		ofy().delete().type(User.class).id(userId);
	}
	
	// SECURE IN WEB.XML
	@DELETE
	public void deleteUseres(){
		List <User> users = ofy().load().type(User.class).list();
		ofy().delete().entities(users);
	}
}
