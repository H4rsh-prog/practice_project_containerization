package com.ecom.controller;

import org.springframework.web.bind.annotation.RestController;
import com.ecom.model.entity.EmailEntity;
import com.ecom.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class EmailController {

	@Autowired
	private EmailService service;
	
	@GetMapping("/status")
	public String baseGet() {
		return "EMAIL SERVICE ACTIVE";
	}
	@GetMapping("/{id}")
	public Optional<EmailEntity> getEmailEntity(@PathVariable("id") String id) throws JsonProcessingException{
		return this.service.getEmailById(id);
	}
	@GetMapping("/check/{address}")
	public boolean checkEmailAvailability(@PathVariable("address") String address) {
		return this.service.checkAddressAvailability(address);
	}
	@PostMapping("/list-entity")
	public List<EmailEntity> getEntitiesIterable(@RequestBody List<String> userids) throws JsonMappingException, JsonProcessingException{
		return this.service.getEntitiesIterable(userids);
	}
	@PostMapping("/")
	public Optional<EmailEntity> postEmailEntity(@RequestBody EmailEntity entity) throws Exception{
		return this.service.mapEmail(entity);
	}
	@DeleteMapping("/{id}")
	public boolean deleteEntityById(@PathVariable("id") String user_id){
		return this.service.deleteEntityById(user_id);
	}
}
