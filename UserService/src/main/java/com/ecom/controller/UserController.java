package com.ecom.controller;

import org.springframework.web.bind.annotation.RestController;
import com.ecom.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class UserController {
	@Autowired
	private UserService service;
	
	@GetMapping("/status")
	public String baseGet() {
		return "USER SERVICE ACTIVE";
	}
	@GetMapping("/{username}")
	public ResponseEntity<?> getUserEntity(@PathVariable("username") String username) throws JsonProcessingException{
		return this.service.getUserByUsername(username);
	}
	@PostMapping("/list-entity")
	public ResponseEntity<?> getEntitiesIterable(@RequestBody List<String> usernames){
		return this.service.getEntitiesIterable(usernames);
	}
	@PostMapping("/")
	public ResponseEntity<?> postUserEntity(@RequestBody com.ecom.factory.model.request.User userRequest) throws Exception{
		return this.service.saveUser(userRequest);
	}
}
