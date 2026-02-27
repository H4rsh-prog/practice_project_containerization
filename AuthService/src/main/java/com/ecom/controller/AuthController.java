package com.ecom.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.dto.AuthRepository;
import com.ecom.model.entity.AuthEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class AuthController {
	@Autowired
	private AuthRepository repo;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private com.ecom.factory.util.DEBUG debugClient;
	
	@GetMapping("/")
	public String baseGet() {
		return "AUTH SERVICE IS RUNNING";
	}
	@GetMapping("{username}")
	public ResponseEntity<?> getAuth(@PathVariable("username") String username){
		debugClient.print("RECEIVED REQUEST TO FETCH AUTH ENTITY FOR "+username);
		Optional<AuthEntity> entity = this.repo.findByUsername(username);
		if(entity.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ENTITY DOES NOT EXIST");
		debugClient.print("AUTH ENTITY FOUND "+entity.get());
		try {
			return ResponseEntity.status(HttpStatus.OK).body(this.mapper.readValue(
						this.mapper.writeValueAsString(entity.get()),
						com.ecom.factory.model.response.Auth.class)
					);
		} catch (JsonProcessingException e) {
			debugClient.print("EXCEPTION CAUGHT WHILE PROCESSESING JSON VIA OBJECTMAPPER");
			debugClient.print(e.getMessage());
		}
		return null;
	}
	@PostMapping("/")
	public ResponseEntity<?> postAuth(@RequestBody com.ecom.factory.model.response.Auth authBody){
		try {
			debugClient.print("RECEIVED POST REQUEST FOR AUTH BODY "+authBody);
			AuthEntity savedEntity = this.repo.save(this.mapper.readValue(
						this.mapper.writeValueAsString(authBody)
						, AuthEntity.class)
					);
			debugClient.print("SAVED ENTITY "+savedEntity);
			return ResponseEntity.ok(savedEntity);
		} catch (JsonProcessingException e) {
			debugClient.print("EXCEPTION CAUGHT WHILE PROCESSESING JSON VIA OBJECTMAPPER");
			debugClient.print(e.getMessage());
		}
		return null;
	}
 }
