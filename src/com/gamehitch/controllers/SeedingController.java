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
import com.gamehitch.entities.Negotiation;
import com.gamehitch.entities.User;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.googlecode.objectify.cmd.Query;

@Path("/reset")
public class SeedingController {
	private String appId = SystemProperty.applicationId.get();
	private String env = SystemProperty.environment.value().name();
	private String passwordHash = "d537329444c6f746e11d321133c020463ad83db8629a22cf0ed25c878f51cf69314b2b651d059ddc6db06ea4b832b62cc16e92b6ddc0c849b8e742593bdd48b2";
	
	Gson gson = new Gson();
	
	@GET @Path("/users")
	public String resetUsers() {
			List<User> users = ofy().load().type(User.class).list();
			List<User> newUsers = new ArrayList<User>();
			ofy().delete().entities(users).now();
			
			User chase = new User();
			chase.setFirstName("Chase");
			chase.setLastName("Horvath");
			chase.setRole("Coach");
			chase.setSport("Men's Basketball");
			chase.setSportId("mens-college-basketball");
			chase.setSchool("American");
			chase.setConference("Patriot");
			chase.setEmailAddress("horvatca@gmail.com");
			chase.setPasswordHash(passwordHash);
			chase.setLat(38.944754);
			chase.setLon(-77.095114);
			chase.setAccountActivated(true);
			
			User david = new User();
			david.setFirstName("David");
			david.setLastName("Centofante");
			david.setRole("Coach");
			david.setSport("Men's Basketball");
			david.setSportId("mens-college-basketball");
			david.setSchool("JMU");
			david.setConference("CAA");
			david.setEmailAddress("david.centofante@gmail.com");
			david.setPasswordHash(passwordHash);
			david.setLat(38.440191);
			david.setLon(-78.87508);
			david.setAccountActivated(true);
			
			User peter = new User();
			peter.setFirstName("Peter");
			peter.setLastName("Rangelov");
			peter.setRole("Coach");
			peter.setSport("Men's Basketball");
			peter.setSportId("mens-college-basketball");
			peter.setSchool("UMBC");
			peter.setConference("AEC");
			peter.setEmailAddress("peter.rangelov11@gmail.com");
			peter.setPasswordHash(passwordHash);
			peter.setLat(39.252078);
			peter.setLon(-76.709509);
			peter.setAccountActivated(true);
	
			User c = new User();
			c.setFirstName("Chase");
			c.setLastName("H");
			c.setRole("Athletic Director");
			c.setConference("Patriot");
			c.setSchool("American");
			c.setEmailAddress("il5kil7@gmail.com");
			c.setPasswordHash(passwordHash);
			c.setAccountActivated(true);
			c.setLat(38.944754);
			c.setLon(-77.095114);
			
	
			User d = new User();
			d.setFirstName("David");
			d.setLastName("C");
			d.setRole("Athletic Director");
			d.setSchool("JMU");
			d.setEmailAddress("dssfeed@gmail.com");
			d.setPasswordHash(passwordHash);
			d.setAccountActivated(true);
			d.setConference("CAA");
			d.setLat(38.440191);
			d.setLon(-78.87508);	
			
			User p = new User();
			p.setFirstName("P");
			p.setLastName("R");
			p.setRole("Athletic Director");
			p.setSchool("UMBC");
			p.setConference("AEC");
			p.setEmailAddress("pr6@umbc.edu");
			p.setPasswordHash(passwordHash);
			p.setAccountActivated(true);
			p.setLat(39.252078);
			p.setLon(-76.709509);
		
			newUsers.add(peter);
			newUsers.add(chase);
			newUsers.add(david);
			newUsers.add(p);
			newUsers.add(c);
			newUsers.add(d);
			
			
			ofy().save().entities(newUsers).now();
			
			
			return newUsers.toString();
		
	}
	
	@GET @Path("/ach")
	public String removeACH() {
			List<User> users = ofy().load().type(User.class).list();
			
			for (User u : users){
				u.setBalancedToken(null);
			}
			ofy().save().entities(users).now();
			
			return users.toString();
		
	}
	
	@GET @Path("/negotiations")
	public String resetNegotiations() {
		if (env.equalsIgnoreCase("Development") || !appId.equalsIgnoreCase("rival-maker")) {
			List<Negotiation> negotiations = ofy().load().type(Negotiation.class).list();
			
			ofy().delete().entities(negotiations);
			
			return "Negotiations are reset";
		}
		return "GTFO";
		
	}
	
	@GET @Path("/sids")
	public String sessionIDs() {
		if (env.equalsIgnoreCase("Development") || !appId.equalsIgnoreCase("rival-maker")) {
			List<User> users = ofy().load().type(User.class).list();
			
			for (User user : users){
				user.setSessionIds(null);
			}
			
			ofy().save().entities(users);
			
			return "Session IDs are reset";
		}
		return "GTFO";
		
	}

	
	
	
	
	
	
}