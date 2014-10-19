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
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

@Entity @Index @XmlRootElement @XmlAccessorType(XmlAccessType.FIELD) @Data @AllArgsConstructor @NoArgsConstructor
public class Event {
	@Id Long id;
	
	private String start;
	private double lat;
	private double lon;
	private int rpi;
	
	private String school;
	private String conference;
	
	private String location;
	private String title;
	
	public boolean withinRange(LatLng requestorLocation, double range){
		LatLng eventLocation = new LatLng(this.lat, this.lon);
		double distance = LatLngTool.distance(requestorLocation, eventLocation, LengthUnit.MILE);
		
		if (distance <= range){
			return true;
		}
		else {
			return false;
		}
		
	}
	
//	@OnLoad
//	public void onLoad(){
//
//		if (this.school.equals("UMBC")){
//			this.setRpi(341);
//		}
//		
//		if (this.school.equals("American")){
//			this.setRpi(71);
//		}
//		
//		if (this.school.equals("JMU")){
//			this.setRpi(45);
//		}
		
//	
//		if (this.emailAddress.equalsIgnoreCase("david.centofante@gmail.com")){
//			this.role="Coach";
//		}
//	
//		if (this.emailAddress.equalsIgnoreCase("il5kil7@gmail.com")) {
//			this.conference="Patriot";
//			this.role="Athletic Director";
//			this.school="American";
//		}
		
//		if (this.emailAddress.equalsIgnoreCase("horvatca@gmail.com")) {
//			this.role="Coach";
//			this.school="American";
//		}
		
//		if (this.emailAddress.equalsIgnoreCase("dssfeed@gmail.com")) {
//			this.conference="CAA";
//		}
		
//		ofy().save().entity(this).now();
		
//	}
	
}