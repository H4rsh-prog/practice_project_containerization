package com.ecom.service;

import java.util.List;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ecom.dto.EmailRepository;
import com.ecom.factory.util.DEBUG;
import com.ecom.model.entity.EmailEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailService {
	@Autowired
	private EmailRepository repo;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private DEBUG debugClient;
	
	public ResponseEntity<?> getEmailById(String id) throws JsonProcessingException{
		Optional<EmailEntity> entity = this.repo.findById(id);
		if(entity.isEmpty()) return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("ENTITY NOT FOUND IN TABLE");
		String json = this.mapper.writeValueAsString(entity.get());
		this.debugClient.print("EMAIL ENTITY FETCHED "+entity.get());
		return ResponseEntity.status(HttpStatus.SC_ACCEPTED).body(
				this.mapper.readValue(json, com.ecom.factory.model.response.Email.class)
			);
	}
	public boolean checkAddressAvailability(String address) {
		this.debugClient.print("CHECKING EMAIL AVAILABILITY FOR ADDRESS "+address);
		return !this.repo.existsByAddress(address);
	}
	public ResponseEntity<?> mapEmail(com.ecom.factory.model.request.Email emailRequest) throws JsonProcessingException{
		String json = this.mapper.writeValueAsString(emailRequest);
		this.debugClient.print("RECEIVED MAPPING REQUEST FOR THE EMAIL ENTITY "+this.mapper.readValue(json, EmailEntity.class));
		Optional<EmailEntity> entity = Optional.of(
			this.repo.save(
				this.mapper.readValue(json, EmailEntity.class)
			));
		if(entity.isEmpty()) {
			this.debugClient.print("MAPPING FAILED");
			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("COULD NOT MAP EMAIL TO USER");
		}
		this.debugClient.print("MAPPING SUCCEEDED");
		return ResponseEntity.status(HttpStatus.SC_ACCEPTED).body(
			this.mapper.readValue(
					this.mapper.writeValueAsString(entity),
					com.ecom.factory.model.response.Email.class
				)
			);
	}
	public ResponseEntity<?> getEntitiesIterable(List<String> userIdList) {
		return ResponseEntity.ok(this.repo.findAllById(userIdList));
	}
}
