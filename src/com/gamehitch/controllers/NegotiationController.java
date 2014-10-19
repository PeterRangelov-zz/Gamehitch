package com.gamehitch.controllers;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;

import com.balancedpayments.BankAccount;
import com.balancedpayments.errors.HTTPError;
import com.gamehitch.entities.Negotiation;
import com.gamehitch.entities.Sendgrid;
import com.gamehitch.entities.User;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.gson.Gson;

@Path("/negotiation")
public class NegotiationController {
	Gson gson = new Gson();
	Sendgrid mail = new Sendgrid("peterrangelov","F231FR");
	
	@GET  @Produces(MediaType.APPLICATION_JSON)
	public List<Negotiation> all (@CookieParam("rmSessionId") String rmSessionId, @CookieParam("rmSchool") String rmSchool, @CookieParam("rmSportId") String rmSportId){
		
		return ofy().load().type(Negotiation.class).list();
	}
	
	@GET @Path("/{id}") @Produces(MediaType.APPLICATION_JSON)
	public Negotiation getNegotiation(@PathParam("id") Long id){
		return ofy().load().type(Negotiation.class).id(id).now();
	}
	
	@PUT @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
	public void newNegotiation(@CookieParam("rmSessionId") String rmSessionId, @CookieParam("rmSchool") String rmSchool, @CookieParam("rmSportId") String rmSportId, @FormParam("amount") String amount, @FormParam("date") String date, @FormParam("opponent") String opponent,  @FormParam("payer") String payer, @FormParam("location") String location, @FormParam("tickets") int tickets, @FormParam("cancellation") String cancellation, @FormParam("comments") String comments, @Context UriInfo uri) throws JSONException{
		System.out.println(cancellation);
		System.out.println(payer);
		System.out.println(amount);
		
		User coach1 = ofy().load().type(User.class).project("emailAddress").filter("school", rmSchool).filter("sportId", rmSportId).filter("role", "Coach").first().now();
		User coach2 = ofy().load().type(User.class).project("emailAddress").filter("school", opponent).filter("sportId", rmSportId).filter("role", "Coach").first().now();
		
		User ad1 = ofy().load().type(User.class).filter("school", rmSchool).filter("role", "Athletic Director").first().now();
		User ad2 = ofy().load().type(User.class).filter("school", opponent).filter("role", "Athletic Director").first().now();
		
		Negotiation n = new Negotiation();
		
		n.setCoach1Email(coach1.getEmailAddress());
		n.setCoach2Email(coach2.getEmailAddress());
		
		n.setAd1Email(ad1.getEmailAddress());
		n.setAd2Email(ad2.getEmailAddress());
		
		n.setSchool1BalancedToken(ad1.getBalancedToken());
		n.setSchool2BalancedToken(ad2.getBalancedToken());
		
		n.setSchool1(rmSchool);
		n.setSchool2(opponent);
		
		n.getGames().put(date, location);
		n.setTickets(tickets);
		n.setCancellation(cancellation);
		n.setTextarea(comments);
		n.setSportId(rmSportId);
		n.setSchool1Accepted(true);
		
		
		if (! payer.equalsIgnoreCase("nobody") && !amount.equalsIgnoreCase("0") ){
			n.setPayer(payer);
			n.setAmount(amount);
		}
		
		ofy().save().entity(n).now();
		
		mail.addTo("info@rivalmaker.com")
		 .setFromName("Rivalmaker")
		 .setBcc(coach1.getEmailAddress())
		 .setBcc(coach1.getEmailAddress())
		 .setFrom("info@rivalmaker.com")
	     .setSubject("New negotiation started")
	     .setText("Negotiation started between "+n.getSchool1()+" and "+n.getSchool2()+" "+uri.getBaseUri().getHost()+"/negotiation/"+n.getId())
	        .send();
		
		
	}
	
	@GET @Path("/mine") @Produces(MediaType.APPLICATION_JSON)
	public List<Negotiation> myNegotiations (@CookieParam("rmSessionId") String rmSessionId, @CookieParam("rmSchool") String rmSchool, @CookieParam("rmSportId") String rmSportId){
		List<Negotiation> negotiations = ofy().load().type(Negotiation.class).filter("school1", rmSchool).filter("sportId", rmSportId).filter("school1Accepted", false).list();
		List<Negotiation> negotiations2 = ofy().load().type(Negotiation.class).filter("school2", rmSchool).filter("sportId", rmSportId).filter("school2Accepted", false).list();
		negotiations.addAll(negotiations2);
		
		return negotiations;
	}
	
	@DELETE @Path("/{id}") @Produces(MediaType.APPLICATION_JSON)
	public void deleteNegotiation (@PathParam("id") Long id, @CookieParam("rmSessionId") String rmSessionId, @CookieParam("rmSchool") String rmSchool, @CookieParam("rmSportId") String rmSportId) throws JSONException{
		Negotiation n = ofy().load().type(Negotiation.class).id(id).now();
		
		 mail.addTo("info@rivalmaker.com")
		 .setFromName("Rivalmaker")
		 .setBcc(n.getCoach1Email())
		 .setBcc(n.getCoach2Email())
		 .setFrom("info@rivalmaker.com")
	        .setSubject("Negotiation deleted")
	        .setText("Negotiation between "+n.getSchool1()+" and "+n.getSchool2()+" has been deleted.\n")
	        .send();
		 ofy().delete().entity(n).now();
	}
	
	@POST @Path("/{id}/accept") @Produces(MediaType.APPLICATION_JSON)
	public void acceptNegotiation (@PathParam("id") Long id, @CookieParam("rmSessionId") String rmSessionId, @CookieParam("rmSchool") String rmSchool, @CookieParam("rmSportId") String rmSportId, @Context UriInfo uri, @Context HttpServletRequest request) throws HTTPError, JSONException{
		
		Negotiation n = ofy().load().type(Negotiation.class).id(id).now();
		System.out.println(n);
		
		if (n.getSchool1().equalsIgnoreCase(rmSchool)){
			n.setSchool1Accepted(true);
			ofy().save().entity(n);
		}
		if (n.getSchool2().equalsIgnoreCase(rmSchool)){
			n.setSchool2Accepted(true);
			ofy().save().entity(n);
		}
		
		
		// SEND EMAIL TO COACHES
		mail.addTo("info@rivalmaker.com")
		.setFromName("Rivalmaker")
		.setBcc(n.getCoach1Email())
		.setBcc(n.getCoach2Email())
		.setFrom("info@rivalmaker.com")
		 .setSubject("Passed onto your Athletic Director")
		 .setText("")
		 .setHtml(n.getSchool1()+" and "+n.getSchool2()+",<br>"
		     + "Congrats on reaching a mutually acceptable agreement!<br>"
		     + "What's next?<br>"
		     + "We're sending your proposed game over to both your Athletic Directors. Once they sign it, we'll transfer any funds (if applicable) and generate your legally-binding contract.<br>"
		     + "Thanks for using Rivalmaker")
		.send();
			 
			 System.out.println(uri.getBaseUri().getAuthority());
	mail.addTo("info@rivalmaker.com")
	.setFromName("Rivalmaker")
	.setBcc(n.getAd1Email())
	.setBcc(n.getAd2Email())
	.setFrom("info@rivalmaker.com")
	.setSubject("Your signature is needed")
	.setText("")
	.setHtml(n.getSchool1()+" and "+n.getSchool2()+" have reached a mutually acceptable agreement.<br>"
//			+ "Please review details <a href=http://www.google.com>here</a><br>"
		  + "Please review details <a href=http://"+uri.getBaseUri().getAuthority()+"/adportal/sign/"+n.getId()+">here</a><br>"
		  + "Once you and your rival sign it, we'll transfer any funds (if applicable) and generate your legally-binding contract.<br>"
		  + "Thanks for using Rivalmaker")
	.send();
			 
		
		
	}
	
	@POST @Path("/{id}/propose") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
	public void proposeNegotiation (@PathParam("id") Long id, @CookieParam("rmSessionId") String rmSessionId, @CookieParam("rmSchool") String rmSchool, @CookieParam("rmSportId") String rmSportId, @FormParam("date") String date, @FormParam("amount") String amount, @FormParam("payer") String payer, @FormParam("comments") String comments, @FormParam("location") String location, @FormParam("tickets") int tickets, @FormParam("cancellation") String cancellation, @Context UriInfo uri) throws JSONException{
		Negotiation n = ofy().load().type(Negotiation.class).id(id).now();
		System.out.println("payer: "+payer);
		System.out.println("amount: "+amount);
		
		System.out.println(n);
		String sendTo;
		
		if (payer.equalsIgnoreCase("nobody") || amount.equalsIgnoreCase("0") ){
			n.setPayer(null);
			n.setAmount(null);
		}
		else {
			n.setPayer(payer);
			n.setAmount(amount);
		}
		
		
		Map<String, String> dl = new HashMap<String, String>();
		dl.put(date, location);
		n.setGames(dl);
		n.setTextarea(comments);
		n.setTickets(tickets);
		n.setCancellation(cancellation);
		
		if (n.getSchool1().equalsIgnoreCase(rmSchool)){
			sendTo=n.getSchool1();
			n.setSchool1Accepted(true);
			n.setSchool2Accepted(false);
		}
		else {
			sendTo=n.getSchool2();
			n.setSchool2Accepted(true);
			n.setSchool1Accepted(false);
		}
		
		ofy().save().entity(n);
		
		// SEND EMAIL
		mail.addTo("info@rivalmaker.com")
		.setFromName("Rivalmaker")
		.setBcc(sendTo)
		.setFrom("info@rivalmaker.com")
		.setSubject(rmSchool+" has changed the conditions")
		.setText("")
		.setHtml("Click <a href=http://"+uri.getBaseUri().getAuthority()+"/negotiations>here</a> to take action.<br>"
				+ "If you accept, we'll shoot the game conditions over to both your Athletic Directors for their e-signatures.<br>"
				+ "If you propose new conditions, we'll shoot the new conditions over to "+rmSchool+"'s coach who can either accept, or propose new conditions to you.<br>"
				+ "Thanks for using Rivalmaker!")
		.send();
	}
	
	@POST @Path("/{id}/sign") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
	public void adSign (@PathParam("id") Long id, @CookieParam("rmSessionId") String rmSessionId, @CookieParam("rmSchool") String rmSchool, @CookieParam("rmSportId") String rmSportId) throws JSONException, HTTPError{
		Negotiation n = ofy().load().type(Negotiation.class).id(id).now();
		System.out.println(n);
		
		if (n.getSchool1().equalsIgnoreCase(rmSchool)){
			n.ad1Signs();
		}
		if (n.getSchool2().equalsIgnoreCase(rmSchool)){
			n.ad2Signs();
		}
	}
	
	@GET @Path("/ad/unsigned") @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
	public List<Negotiation> getADunsigned (@CookieParam("rmSessionId") String rmSessionId, @CookieParam("rmSchool") String rmSchool, @CookieParam("rmSportId") String rmSportId) throws JSONException{
		List<Negotiation> negotiations = ofy().load().type(Negotiation.class).filter("school1", rmSchool).filter("ad1Signed", false).filter("school1Accepted", true).filter("school2Accepted", true).list();
		List<Negotiation> negotiations2 = ofy().load().type(Negotiation.class).filter("school2", rmSchool).filter("ad2Signed", false).filter("school1Accepted", true).filter("school2Accepted", true).list();
		negotiations.addAll(negotiations2);
		
		return negotiations;
		
	}
}