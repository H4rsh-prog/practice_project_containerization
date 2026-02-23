package com.ecom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecom.dto.AuthClient;
import com.ecom.factory.util.DEBUG;
import com.ecom.model.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserInfoService implements UserDetailsService {
	@Autowired
	private AuthClient authClient;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private DEBUG debugClient;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ResponseEntity<?> clientResponse = this.authClient.getAuth(username);
		try {
			if(clientResponse.getStatusCode().is2xxSuccessful()) {
				com.ecom.factory.model.response.Auth authBody = this.mapper.readValue(
							this.mapper.writeValueAsString(clientResponse.getBody())
							, com.ecom.factory.model.response.Auth.class
						);
				return new UserInfo(authBody);
			}
		} catch (JsonProcessingException e) {
			debugClient.print("EXCEPTION CAUGHT WHILE PROCESSESING JSON VIA OBJECTMAPPER");
			debugClient.print(e.getMessage());
		}
		return null;
	}

}
