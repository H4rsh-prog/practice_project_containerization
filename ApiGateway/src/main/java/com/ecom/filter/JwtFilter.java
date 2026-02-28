package com.ecom.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecom.factory.util.DEBUG;
import com.ecom.model.UserInfo;
import com.ecom.service.JwtService;
import com.ecom.service.UserInfoService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
	@Autowired private DEBUG debugClient;
	@Autowired private JwtService jwtService;
	@Autowired private UserInfoService userInfoService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		debugClient.print("ENTERED JWT FILTER");
		String authHeader = request.getHeader("Authorization");
		String token = null, subject = null;
		debugClient.print("AUTH HEADER = "+authHeader);
		if(authHeader!=null && authHeader.startsWith("Bearer ")) {
			debugClient.print("AUTHORIZATION INCLUDES BEARER TOKEN");
			token = authHeader.substring(7);
			subject = this.jwtService.getSubject(token);
			debugClient.print("AUTHORIZATION [TOKEN = "+token+" ; SUBJECT = "+subject+"]");
		}
		if(subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			debugClient.print("CONTEXT UNAUTHENTICATED ATTEMPTING TO VALIDATE");
			if(jwtService.validate(subject, token)) {
				debugClient.print("VALID TOKEN");
				UserDetails userDetails = (UserInfo) this.userInfoService.loadUserByUsername(subject);
				debugClient.print("FETCHED USER DETAILS "+((UserInfo)userDetails).getAuthUser());
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
				debugClient.print("CONTEXT UPDATED [AUTHENTICATION_STATUS = "+SecurityContextHolder.getContext().getAuthentication().isAuthenticated()+"]");
			}			
		}
		filterChain.doFilter(request, response);
	}

}
