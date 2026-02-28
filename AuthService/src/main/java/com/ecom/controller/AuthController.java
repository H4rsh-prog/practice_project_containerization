package com.ecom.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.dto.AuthRepository;
import com.ecom.model.entity.AuthEntity;

@RestController
public class AuthController {
	@Autowired
	private AuthRepository repo;
	@Autowired
	private com.ecom.factory.util.DEBUG debugClient;
	
	@GetMapping("/")
	public String baseGet() {
		return "AUTH SERVICE IS RUNNING";
	}
	@GetMapping("{id}")
	public Optional<AuthEntity> getAuth(@PathVariable("id") String id){
		debugClient.print("RECEIVED REQUEST TO FETCH AUTH ENTITY FOR "+id);
		Optional<AuthEntity> entity = this.repo.findById(id);
		if(entity.isEmpty()) {
			debugClient.print("COULD NOT FIND AUTH ENTITY");
		} else {
			debugClient.print("RETURNING FOUND AUTH ENTITY "+entity);
		}
		return entity;
	}
	@GetMapping("/authorities/{id}")
	public List<String> getAuthorities(@PathVariable("id") String id){
		debugClient.print("RECEIVED REQUEST TO FETCH AUTHORITY LIST FOR "+id);
		List<String> authorities = this.repo.getAuthorities(id);
		debugClient.print("RETURNING FETCHED LIST "+authorities);
		return authorities;
	}
	@PostMapping("/")
	public Optional<AuthEntity> postAuth(@RequestBody AuthEntity entity){
		debugClient.print("RECEIVED POST REQUEST FOR AUTH BODY "+entity);
		entity = this.repo.save(entity);
		debugClient.print("RETURNING SAVED ENTITY "+entity);
		return Optional.of(entity);
	}
	@DeleteMapping("/{id}")
	public boolean deleteEntityById(@PathVariable("id") String id) {
		debugClient.print("DELETE REQUEST FOR ID "+id);
		if(this.repo.existsById(id)) {
			debugClient.print("ENTITY EXISTS");
			this.repo.deleteById(id);
			debugClient.print("ENTITY DELETED");
			return true;
		}
		return false;
	}
 }
