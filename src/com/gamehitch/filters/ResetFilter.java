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

import com.google.appengine.api.utils.SystemProperty;

public class ResetFilter implements Filter  {
	private String appId = SystemProperty.applicationId.get();
	private String env = SystemProperty.environment.value().name();
	private String passwordHash = "d537329444c6f746e11d321133c020463ad83db8629a22cf0ed25c878f51cf69314b2b651d059ddc6db06ea4b832b62cc16e92b6ddc0c849b8e742593bdd48b2";
	
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//		System.out.println("LoginFilter called");
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		
		
		
		if (env.equalsIgnoreCase("Development") || !appId.equalsIgnoreCase("rival-maker")) {
			chain.doFilter(req, res);
		}
		else {
			response.getWriter().write("GTFO");
		}
		
		
		
		
		
		
		
	}

	@Override
	public void init(FilterConfig config) {
		
		
	}
	
	@Override
	public void destroy() {
	}
	
	
	
}