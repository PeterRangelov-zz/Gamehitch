package com.gamehitch.entities;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.balancedpayments.BankAccount;
import com.balancedpayments.errors.HTTPError;
import com.gamehitch.servlets.StartupServlet;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;

@Entity @Index @XmlRootElement @XmlAccessorType(XmlAccessType.FIELD) @Data @AllArgsConstructor @NoArgsConstructor
public class Negotiation {
	@Id Long id;
	
	private String payer;
	private String amount;
	private String sportId;
	private String textarea;
	private String cancellation;
	private int tickets;
	
	private String school1;
	private String school2;
	
	private String coach1Email;
	private String coach2Email;
	
	private String ad1Email;
	private String ad2Email;
	
	@XmlTransient private String school1BalancedToken;
	@XmlTransient private String school2BalancedToken;
	
	boolean school1Accepted;
	boolean school2Accepted;
	
	boolean ad1Signed;
	boolean ad2Signed;
	
	Map <String, String> games = new HashMap<>();
	
//	@OnLoad
//	public void onLoad(){
//		this.ad1Signed=false;
//		this.ad2Signed=false;
//		//this.games.remove("01/01/2015");
//		ofy().save().entity(this);
//	}
	
	
	public void school1Accepts(){
		this.school1Accepted=true;
		ofy().save().entity(this);
		if (this.school2Accepted){
			// SEND EMAIL TO ATHLETIC DIRECTORS
		}
	}
	
	public void school2Accepts(){
		this.school2Accepted=true;
		ofy().save().entity(this);
		if (this.school1Accepted){
			// SEND EMAIL TO ATHLETIC DIRECTORS
		}
	}
	
	
	public void ad1Signs() throws HTTPError, JSONException{
		this.ad1Signed=true;
		ofy().save().entity(this);
		if (this.ad2Signed){
			transferMoney();
		}
	}
	
	public void ad2Signs() throws HTTPError, JSONException{
		this.ad2Signed=true;
		ofy().save().entity(this);
		if (this.ad1Signed){
			transferMoney();
		}
	}
	
	public void transferMoney() throws HTTPError, JSONException{
		System.out.println("Transferring money...");
		// logic to transfer funds
		
		if (this.getPayer()!=null){
			// WHO PAYS WHO
			String fromToken;
			String toToken;
			
			// IF SCHOOL1 PAYS
			if (this.school1.equalsIgnoreCase(this.payer)){
				fromToken=this.school1BalancedToken;
				toToken=this.school2BalancedToken;
			}
			else {
				fromToken=this.school2BalancedToken;
				toToken=this.school1BalancedToken;
			}
			
			
			int moneyToThem = Integer.parseInt(this.amount)*100;
			int moneyToUs = moneyToThem + (moneyToThem *3) /100;
			System.out.println("Money to them: "+moneyToThem);
			System.out.println("Money to us: "+moneyToUs);
		
			
			BankAccount baPayer = new BankAccount(fromToken);
			BankAccount baReceiver = new BankAccount(toToken);
		
//			// WE GET moneyToUs
			HashMap<String, Object> payloadIn = new HashMap<String, Object>();
			payloadIn.put("amount", moneyToUs);
			payloadIn.put("description", this.school1+" vs "+this.school2);
			payloadIn.put("appears_on_statement_as", this.school1+" vs "+this.school2);
			
			baPayer.debit(payloadIn);
			
//			// WE TRANSFER moneyToThem
			HashMap<String, Object> payloadOut = new HashMap<String, Object>();
			payloadOut.put("amount", moneyToThem);
			payloadOut.put("description", this.school1+" vs "+this.school2);
			payloadOut.put("appears_on_statement_as", this.school1+" vs "+this.school2);
			
			baReceiver.credit(payloadOut);
			
			
		}
		
		
//		 SEND EMAIL
		StartupServlet.mail.addTo("info@rivalmaker.com")
		.setFromName("Rivalmaker")
		.setBcc(this.ad1Email)
		.setBcc(this.ad2Email)
		.setBcc(this.coach1Email)
		.setBcc(this.coach2Email)
		.setFrom("info@rivalmaker.com")
        .setSubject("Negotiation finalized")
        .setText("Negotiation is finalized between "+this.school1+" and "+this.school2+".\n"
        		+ "If a sum of money was agreed upon, Rivalmaker will transfer it within 5 business days.\n"
        		+ "Please allow 5 business days for your contract to be generated by out system.\nThank you for using Rivalmaker!")
        .send();
	}
	
}