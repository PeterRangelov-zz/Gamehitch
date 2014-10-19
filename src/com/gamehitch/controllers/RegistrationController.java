package com.gamehitch.controllers;
import static com.googlecode.objectify.ObjectifyService.ofy;
import hirondelle.date4j.DateTime;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.gamehitch.entities.Sendgrid;
import com.gamehitch.entities.User;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.view.Viewable;

@Path("/registration")
public class RegistrationController {
	Sendgrid mail = new Sendgrid("peterrangelov","F231FR");
	
	@POST @Path("/user") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(@FormParam("baseURL") String baseURL, @FormParam("emailAddress") String emailAddress, @FormParam("password") String password, @FormParam("activationToken") String activationToken, @FormParam("activationTokenExpires") String activationTokenExpires) throws JSONException {
		User user = ofy().load().type(User.class).filter("emailAddress", emailAddress).first().now();
		System.out.println(user);
		// check if user has logged in before. If he has, he can't sign up
		if (user.isAccountActivated()){
			return  Response.ok(new JSONObject().put("success", false).put("message", emailAddress+" has already been registered with Rivalmaker.\nYour account is activated.\nTry signing in.").toString(), MediaType.APPLICATION_JSON).build();
		}
		
		else {
			user.setPasswordHash(DigestUtils.sha512Hex(password+"applepie"));
			user.setActivationToken(activationToken);
			user.setActivationTokenExpires(activationTokenExpires);
			 ofy().consistency(Consistency.STRONG).save().entity(user).now();
			// send activation email
	        mail.addTo(emailAddress)
	        .setFrom("peter.rangelov11@gmail.com")
	        .setSubject(user.getFirstName()+", welcome to Rivalmaker!")
	        .setText("")
	        .setHtml("Click <a href="+baseURL+"/rest/registration/activateaccount/"+activationToken+">here</a> to activate your account")
	        .send();
	       
	        return Response.ok(new JSONObject().put("success", true).toString(), MediaType.APPLICATION_JSON).build();
		}
	}
	
	@GET @Path("/activateaccount/{activationToken}") @Produces(MediaType.TEXT_HTML)
	public String activateAccount (@PathParam("activationToken") String activationToken, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletContext context, @Context UriInfo uri) throws JSONException, ServletException, IOException{
		
		String link = "http://"+uri.getBaseUri().getAuthority()+"/signin";
		User user = ofy().consistency(Consistency.STRONG).load().type(User.class).filter("activationToken", activationToken).first().now();
		
		if (new DateTime(user.getActivationTokenExpires().toString()).isInTheFuture(TimeZone.getTimeZone("EST")) && user.getActivationToken().equals(activationToken)){
			user.setAccountActivated(true);
			user.setActivationToken(null);
			user.setActivationTokenExpires(null);
			ofy().save().entity(user).now();
			mail.addTo(user.getEmailAddress())
			.setFrom("info@rivalmaker.com")
			.setBcc("info@rivalmaker.com")
			.setSubject("Rivalmaker account activated")
			.setText("Greetings from Rivalmaker! You've successfully activated your account.")
			.send();
	
			return "<html><p>"+user.getFirstName()+", Thanks for activating your account!</p>"
			+ "<p><a href="+link+">Click here to sign in</a></p></html>";
		}
		
		else if (new DateTime(user.getActivationTokenExpires().toString()).isInThePast(TimeZone.getTimeZone("EST"))){
			return "Your activation token has expired. You must reset your password.";
		}
		// else?
		else {
			return "Something went wrong. Shoot us an email at info@rivalmaker.com";
		}
	
	}
	
	@POST @Path("/login") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
	public Response login (@FormParam("emailAddress") String emailAddress, @FormParam("password") String password, @FormParam("date") String date) throws JSONException, URISyntaxException{
		Logger.getLogger("LoginAttempts").info("/login called");
		Response response;
		try {
			User user = ofy().consistency(Consistency.STRONG).load().type(User.class).filter("emailAddress", emailAddress).first().now();
			System.out.println(user);
			if (user.getPasswordHash().equals(DigestUtils.sha512Hex(password+"applepie")) && user.isAccountActivated()){
				user.setLastLogin(date);
				String sessionId=RandomStringUtils.randomAlphanumeric(50);
				user.getSessionIds().add(sessionId);
				response = Response.ok(new JSONObject().put("firstName", user.getFirstName()).put("role", user.getRole()).put("success", true).put("message", "Login successful").toString(), MediaType.APPLICATION_JSON)
						.cookie(new NewCookie("rmSessionId", sessionId, "/", null, null, 9999999, false))
						.cookie(new NewCookie("rmConference", user.getConference(), "/", null, null, 9999999, false))
						.cookie(new NewCookie("rmSchool", user.getSchool(), "/", null, null, 9999999, false))
						.cookie(new NewCookie("rmSportId", user.getSportId(), "/", null, null, 9999999, false))
						.cookie(new NewCookie("rmLat", Double.toString(user.getLat()), "/", null, null, 9999999, false))
						.cookie(new NewCookie("rmLon", Double.toString(user.getLon()), "/", null, null, 9999999, false))
						.cookie(new NewCookie("rmFirstName", user.getFirstName(), "/", null, null, 9999999, false))
						.build();
				Logger.getLogger("LoginAttempts").info("LoginAttempt: "+emailAddress+" logged in.");
				Logger.getLogger("LoginAttempts").info(user.toString());
				
				ofy().save().entity(user).now();
				return response;
			}
			
			if (!user.getPasswordHash().equals(DigestUtils.sha512Hex(password+"applepie"))){
				Logger.getLogger("LoginAttempts").info("LoginAttempt: "+emailAddress+" login failed. Reason: incorrect password");
				return Response.ok(new JSONObject().put("firstName", user.getFirstName()).put("success", false).put("message", "Incorrect password").toString(), MediaType.APPLICATION_JSON).build();
			}
			
			else {
				Logger.getLogger("LoginAttempts").info("LoginAttempt: "+emailAddress+" login failed. Reason: account isn't activated.");
				return Response.ok(new JSONObject().put("firstName", user.getFirstName()).put("success", false).put("message", "Account is not activated.\nIf you've never logged in before, you need to Sign up.").toString(), MediaType.APPLICATION_JSON).build();
			}
			
		}
		catch (NullPointerException e) {return Response.ok(new JSONObject().put("success", false).put("message", emailAddress+" doesn't exists in our system.\nDid you sign up under a different email address?").toString(), MediaType.APPLICATION_JSON).build();}
		
	}
	
	
	@GET @Path("/logout")
	public Response logout (@CookieParam("rmSessionId") String sessionId, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletContext context) throws JSONException {
		System.out.println("Logging off");
		User user = ofy().load().type(User.class).filter("sessionIds", sessionId).first().now();
		user.getSessionIds().remove(sessionId);
		ofy().save().entity(user).now();
		Response r = Response.ok(new JSONObject().put("loggedOff", true).toString())
				.cookie(new NewCookie("rmSessionId", null, "/", null, null, 0, false))
				.cookie(new NewCookie("rmConference", null, "/", null, null, 0, false))
				.cookie(new NewCookie("rmSchool", null, "/", null, null, 0, false))
				.cookie(new NewCookie("rmSportId", null, "/", null, null, 0, false))
				.cookie(new NewCookie("rmLat", null, "/", null, null, 0, false))
				.cookie(new NewCookie("rmLon", null, "/", null, null, 0, false))
				.cookie(new NewCookie("rmFirstName", null, "/", null, null, 0, false))
				.build();
		
		return r;
	}
	
	@POST @Path("/sendpasswordresetlink") @Consumes(MediaType.APPLICATION_JSON)
	public Response sendPasswordResetLink (@FormParam("baseURL") String baseURL, @FormParam("emailAddress") String emailAddress, @FormParam("activationToken") String activationToken, @FormParam("activationTokenExpires") String activationTokenExpires) throws JSONException{
		try {
			User user = ofy().load().type(User.class).filter("emailAddress", emailAddress).first().now();
			if (!user.isAccountActivated()){
				return Response.ok(new JSONObject().put("success", false).put("message", "You haven't activated your account").toString(), MediaType.APPLICATION_JSON).build();
			}
			
			user.setActivationToken(activationToken);
			user.setActivationTokenExpires(activationTokenExpires);
			ofy().save().entity(user).now();
			
			// SEND EMAIL
			mail.addTo(emailAddress)
			.setFrom("info@rivalmaker.com")
			.setSubject("Rivalmaker password reset link")
			.setText("Greetings from Rivalmaker!")
			.setHtml("Click <a href="+baseURL+"/rest/registration/passwordreset/"+activationToken+">here</a> to reset your password")
			.send();
			return Response.ok(new JSONObject().put("success", true).toString(), MediaType.APPLICATION_JSON).build();
		}
		catch (NullPointerException e) {return Response.ok(new JSONObject().put("userExists", false).toString(), MediaType.APPLICATION_JSON).build();}
		
	}
		@GET @Path("/passwordreset/{activationToken}") @Consumes(MediaType.APPLICATION_JSON)
		public Viewable passwordReset (@PathParam("activationToken") String activationToken) throws JSONException{
			return new Viewable("/passwordreset.html", null);
		}
		
		@POST @Path("/newpassword") @Consumes(MediaType.APPLICATION_JSON)
		public Response newPassword (@FormParam("activationToken") String activationToken, @FormParam("password") String password) throws JSONException{
			User user = ofy().load().type(User.class).filter("activationToken", activationToken).first().now();
			System.out.println(user);
			DateTime expDate = new DateTime(user.getActivationTokenExpires());
			System.out.println(expDate.toString());
			
			
			// CHECK IF THE TOKEN EXPIRED?
			if (new DateTime(user.getActivationTokenExpires().toString()).isInTheFuture(TimeZone.getTimeZone("EST")) && user.getActivationToken().equals(activationToken)){
				user.setPasswordHash(DigestUtils.sha512Hex(password+"applepie"));
				user.setAccountActivated(true);
				user.setActivationToken(null);
				user.setActivationTokenExpires(null);
				String sessionId=RandomStringUtils.randomAlphanumeric(50);
				user.getSessionIds().add(sessionId);
				ofy().save().entity(user);
				// SEND EMAIL
				mail.addTo(user.getEmailAddress())
				.setFrom("peter.rangelov11@gmail.com")
				.setSubject("Rivalmaker password reset")
				.setText("Greetings from Rivalmaker! Your password has been reset.")
				.send();
				
				return Response.ok(new JSONObject().put("passwordReset", true).put("rmSessionId", sessionId).toString(), MediaType.APPLICATION_JSON).build();
			}
			return Response.ok(new JSONObject().put("passwordReset", false).toString(), MediaType.APPLICATION_JSON).build();
			
			
			
			
		}
		
	
		
		// FOR CSV TESTING
				@GET @Path("/csv")
				public void csv() throws JSONException, IOException {

//					ArrayList<User> users = new ArrayList<User>();
//					
//					
//					URL stockURL = new URL("http://pastebin.com/raw.php?i=5pPunrvj");
//					BufferedReader in = new BufferedReader(new InputStreamReader(stockURL.openStream()));
//					CSVReader reader = new CSVReader(in);
//					
//				    String [] nextLine;
//				    reader.readNext();
//				    while ((nextLine = reader.readNext()) != null) {
//				        // nextLine[] is an array of values from the line
//				        System.out.println(nextLine[0] + nextLine[1]);
//				        users.add(new User(null, nextLine[2], nextLine[3], nextLine[5].toLowerCase(), null, null, nextLine[1], "Men's Basketball", "mens-college-basketball", null, null, "Athletic Director", null, null, null, false, null, null));
//				        
//				    }
				    
					//System.out.println(users);
					
//					List<User> goners = ofy().load().type(User.class).filter("accountActivated", false).list();
//					ofy().delete().entities(goners);
				    
				    //ofy().save().entities(users);
				}
	
		
	
}
