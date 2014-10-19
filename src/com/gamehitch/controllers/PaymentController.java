package com.gamehitch.controllers;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.net.SocketException;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.balancedpayments.Balanced;
import com.balancedpayments.BankAccount;
import com.balancedpayments.BankAccountVerification;
import com.balancedpayments.errors.HTTPError;
import com.gamehitch.entities.User;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;
import com.sun.jersey.api.container.MappableContainerException;

@Path("/payment")
public class PaymentController {
	Gson gson = new Gson();
	
	// determine status of user's verification
	@Path ("/status") @GET @Produces(MediaType.APPLICATION_JSON)
	public Response getStatus (@CookieParam("rmSessionId") String rmSessionId, @CookieParam("rmSchool") String rmSchool, @CookieParam("rmSportId") String rmSportId) throws HTTPError, JSONException, MappableContainerException{
		User user = ofy().load().type(User.class).filter("sessionIds", rmSessionId).first().now();
		try {
			BankAccount ba = new BankAccount(user.getBalancedToken());
			System.out.println(ba.verification.verification_status);
			
			System.out.println("Started");
			
			if (ba.verification.verification_status.equals("succeeded")) {
				return Response.ok(new JSONObject().put("phase", 2).put("verification_status", ba.verification.verification_status).put("routing_number", ba.routing_number).put("account_number", ba.account_number).put("bank_name", ba.bank_name).put("account_type", ba.account_type).put("account_name", ba.name)
						.toString(), MediaType.APPLICATION_JSON).build();
			}
			
			return Response.ok(new JSONObject().put("phase", 1).put("verification_status", ba.verification.verification_status).put("routing_number", ba.routing_number).put("account_number", ba.account_number).put("bank_name", ba.bank_name).put("account_type", ba.account_type).put("account_name", ba.name)
					.toString(), MediaType.APPLICATION_JSON).build();
			
			
		}
		catch (RuntimeException e){
			System.out.println("Not started");
			return Response.ok(new JSONObject().put("phase", 0).toString(), MediaType.APPLICATION_JSON).build();
		}
	}
	
	@PUT @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
	public void newACH (@CookieParam("rmSessionId") String rmSessionId, @CookieParam("rmSchool") String rmSchool, @CookieParam("rmSportId") String rmSportId, @FormParam("uri") String uri) throws JSONException, MappableContainerException, SocketException {
		// find user
		System.out.println(uri);
		User user = ofy().load().type(User.class).filter("sessionIds", rmSessionId).first().now();
		
		user.setBalancedToken(uri);
		ofy().save().entity(user).now();
	
		BankAccount bankAccount;
		try {
			bankAccount = new BankAccount(uri);
			bankAccount.verify();
		} catch (HTTPError e) {}
		
		
		
		
		
	}
	
	@POST @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
	public void verifyACH (@CookieParam("rmSessionId") String rmSessionId, @FormParam("deposit1") int deposit1, @FormParam("deposit2") int deposit2) throws HTTPError, MappableContainerException{
		// find user
		User user = ofy().load().type(User.class).filter("sessionIds", rmSessionId).first().now();
		System.out.println(deposit1+" "+deposit2);
		
		BankAccount ba = new BankAccount(user.getBalancedToken());
		
	
		System.out.println(user.getBalancedToken());
		ba.verification.confirm(deposit1, deposit2);
		
		
		
		
		
	}
	
	
	
	
	
	
}