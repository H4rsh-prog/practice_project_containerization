package com.ecom.service;

import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	@Value("${auth.jwt.secret-key}")
	private String secretKey = "";
	
	public JwtService() {
		this.secretKey = Base64.getEncoder().encodeToString(this.secretKey.getBytes());
	}
	public SecretKey getKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secretKey));
	}
	
	public Claims extractAllClaims(String token) {
		return (Claims) Jwts.parser()
				.setSigningKey(getKey())
				.build()
				.parse(token)
				.getPayload();
	}
	public <R> R resolveClaim(String token, Function<Claims, R> claimResolver){
		return claimResolver.apply(extractAllClaims(token));
	}
	private Date getExpireAt(String token) {
		return resolveClaim(token, Claims::getExpiration);
	}
	public String getSubject(String token) {
		return resolveClaim(token, Claims::getSubject);
	}
	private boolean isNotExpired(String token) {
		return new Date(System.currentTimeMillis()).before(getExpireAt(token));
	}
	public boolean validate(String subject, String token) {
		if(subject.equals(getSubject(token)) && isNotExpired(token)) {
			return true;
		}
		return false;
	}
}

