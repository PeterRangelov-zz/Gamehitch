package com.gamehitch.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.ServletContext;

import static com.googlecode.objectify.ObjectifyService.ofy;
import hirondelle.date4j.DateTime;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.gamehitch.entities.Negotiation;
import com.gamehitch.entities.Sendgrid;
import com.gamehitch.entities.User;
import com.google.appengine.labs.repackaged.org.json.JSONException;

import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.googlecode.objectify.cmd.Query;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.view.Viewable;


@Path("/earlyaccess")
public class EarlyAccessController {
	Sendgrid mail = new Sendgrid("peterrangelov","F231FR");
	Sendgrid mail2 = new Sendgrid("peterrangelov","F231FR");
	
	@GET @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
	public List<Negotiation> test()  {

		Query<Negotiation> q = ofy().load().type(Negotiation.class);
		
		String school1 = "American";
		
		if (school1.equalsIgnoreCase("UMBC")){
			q = q.filter("school1", "UMBC");
		}
		else if (school1.equalsIgnoreCase("American")){
			q = q.filter("school1", "American");
		}
		
		return q.list();
		
		//CompositeFilter f = new CompositeFilter();
		

		
	}
	
	@POST @Consumes(MediaType.APPLICATION_JSON)
	public void x(@FormParam("email") String email, @FormParam("sport") String sport, @FormParam("college") String college, @FormParam("ip") String ip, @FormParam("canSchedule") boolean canSchedule, @Context ServletContext context) throws JSONException, IOException {

		StringWriter writer = new StringWriter();
		InputStream inputStream = context.getResourceAsStream("/emails/email.html");
		IOUtils.copy(inputStream, writer, "UTF-8");
		String theString = writer.toString();
		
		 mail.addTo("info@rivalmaker.com")
		 .setFrom("peter.rangelov11@gmail.com")
	        .setSubject("Get early access: "+email)
	        .setText("")
	        .setHtml("<table>"
	        		+ "<tr>"
        				+ "<td>Email</td><td>"+email+"</td>"
        				+ "</tr>"
	        		+ "<tr>"
	        			+ "<td>Sport</td><td>"+sport+"</td>"
	        		+ "</tr>"
	        		+ "<tr>"
        				+ "<td>Can schedule?</td><td>"+canSchedule+"</td>"
        			+ "</tr>"
        			+ "<tr>"
    					+ "<td>School</td><td>"+college+"</td>"
    				+ "</tr>"
    				+ "<tr>"
						+ "<td>IP</td><td>"+ip+"</td>"
					+ "</tr>"
	        ).send();

		 mail2.addTo(email)
		 .setFrom("info@rivalmaker.com")
		 .setFromName("Rivalmaker")
		 .setBcc("info@rivalmaker.com")
	     .setSubject("Thank you for preregistering for Rivalmaker!")
	     .setHtml(theString)
	     .setText("")		
	     .send();
	}
	
	
}
