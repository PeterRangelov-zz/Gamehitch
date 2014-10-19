package com.gamehitch.entities;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;

@Entity @Index @XmlRootElement @XmlAccessorType(XmlAccessType.FIELD) @Data @AllArgsConstructor @NoArgsConstructor
public class User {
	@Id Long id;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String phoneNumber;
	private String school;
	private String sport;
	private String sportId;
	private String teamId;
	private String conference;
	
	private double lat;
	private double lon;
	
	
	private String role;
	@XmlTransient private String passwordHash;
	@XmlTransient private Set<String> sessionIds = new HashSet<String>();
	@XmlTransient private String lastLogin;
	@XmlTransient private boolean accountActivated;
	@XmlTransient private String activationToken;
	@XmlTransient private String activationTokenExpires;
	@XmlTransient private String balancedToken;
	

//	@OnLoad
//	public void onLoad(){

//		if (this.emailAddress.equalsIgnoreCase("peter.rangelov11@gmail.com")){
//			this.role="Coach";
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
//		this.setPasswordHash("d537329444c6f746e11d321133c020463ad83db8629a22cf0ed25c878f51cf69314b2b651d059ddc6db06ea4b832b62cc16e92b6ddc0c849b8e742593bdd48b2");
//		ofy().save().entity(this).now();
		
//	}
	
	
}