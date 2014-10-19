package com.gamehitch.filters;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginFilter implements Filter  {
	
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//		System.out.println("LoginFilter called");
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		boolean sessionIdExists = false;
		boolean rmSchoolExists = false;
		boolean rmConferenceExists = false;
		
		
		try {
			Cookie[] cookies = request.getCookies();
			for (Cookie ck : cookies) {
				
				
				if (ck.getName().equalsIgnoreCase("rmSessionId")) {
					sessionIdExists = true;
				}
				
				if (ck.getName().equalsIgnoreCase("rmSchool")) {
					rmSchoolExists = true;
				}
				
				if (ck.getName().equalsIgnoreCase("rmConference")) {
					rmConferenceExists = true;
				}
	        }
			
			if (sessionIdExists && rmSchoolExists && rmConferenceExists) {
				chain.doFilter(req, res);
			}
			else {
				response.sendRedirect("/signin");
			}
		}
		catch (NullPointerException ne) {
			response.sendRedirect("/signin");
		}
		
		
		
		
		
		
		
	}

	@Override
	public void init(FilterConfig config) {
		
		
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}