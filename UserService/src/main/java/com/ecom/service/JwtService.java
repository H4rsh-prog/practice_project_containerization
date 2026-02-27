package com.ecom.service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	@Autowired
	private ObjectMapper mapper;
	@Value("${auth.jwt.secret-key}")
	private String secretKey = "";
	
	public JwtService() {
		this.secretKey = Base64.getEncoder().encodeToString(this.secretKey.getBytes());
	}
	public SecretKey getKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secretKey));
	}
	
	public String generateToken(String subject, Object object) {
		Map<String, Object> claims = this.mapper.convertValue(object, HashMap.class);
		return Jwts.builder()
				.claims()
				.add(claims)
				.subject(subject)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis()+10800000))
				.and()
				.signWith(getKey())
				.compact();
	}
}

