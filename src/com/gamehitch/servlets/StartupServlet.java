package com.gamehitch.servlets;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import com.balancedpayments.Balanced;
import com.gamehitch.entities.Event;
import com.gamehitch.entities.Negotiation;
import com.gamehitch.entities.Sendgrid;
import com.gamehitch.entities.User;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class StartupServlet extends HttpServlet {
	
	public static Logger loginAttempts = Logger.getLogger("LoginAttempts");
	public static Sendgrid mail = new Sendgrid("peterrangelov","F231FR");
	
	static {
		
		Balanced.configure("F231FR");
        
		
        ObjectifyService.register(User.class);
        ObjectifyService.register(Negotiation.class);
        ObjectifyService.register(Event.class);
      
        
    }
}