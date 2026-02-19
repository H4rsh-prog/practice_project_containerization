package com.ecom.controller;

import org.springframework.web.bind.annotation.RestController;
import com.ecom.dto.EmailRepository;
import com.ecom.factory.util.DEBUG;
import com.ecom.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<?> getEmailEntity(@PathVariable("id") String id) throws JsonProcessingException{
		return this.service.getEmailById(id);
	}
	@GetMapping("/check/{address}")
	public boolean checkEmailAvailability(@PathVariable("address") String address) {
		return this.service.checkAddressAvailability(address);
	}
	@PostMapping("/list-entity")
	public ResponseEntity<?> getEntitiesIterable(@RequestBody List<String> userids) throws JsonMappingException, JsonProcessingException{
		return this.service.getEntitiesIterable(userids);
	}
	@PostMapping("/")
	public ResponseEntity<?> postEmailEntity(@RequestBody com.ecom.factory.model.request.Email emailRequest) throws JsonProcessingException{
		return this.service.mapEmail(emailRequest);
	}
}
