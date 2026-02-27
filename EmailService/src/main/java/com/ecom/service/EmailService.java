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
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

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
	@HystrixCommand(commandKey = "mapRevert", fallbackMethod = "revertMapEmail")
	public ResponseEntity<?> mapEmail(com.ecom.factory.model.request.Email emailRequest) {
		try {
			String json = this.mapper.writeValueAsString(emailRequest);
			Optional<EmailEntity> entity = Optional.of(this.mapper.readValue(json, EmailEntity.class));
			this.debugClient.print("RECEIVED MAPPING REQUEST FOR THE EMAIL ENTITY "+entity.get());
			entity = Optional.of(this.repo.save(entity.get()));
			this.debugClient.print("MAPPING SUCCEEDED");
			return ResponseEntity.status(HttpStatus.SC_ACCEPTED).body(
				this.mapper.readValue(
						this.mapper.writeValueAsString(entity),
						com.ecom.factory.model.response.Email.class
					)
				);
		} catch(JsonProcessingException e) {
			debugClient.print("EXCEPTION CAUGHT WHILE PROCESSESING JSON VIA OBJECTMAPPER");
			debugClient.print(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.SC_EXPECTATION_FAILED).body("[MAP-EMAIL]	UNEXPECTED BYPASS DID NOT ENTER FALLBACK");
	}
	public ResponseEntity<?> revertMapEmail(com.ecom.factory.model.request.Email emailRequest) {
		debugClient.print("METHOD FAILED ENTERED FALLBACK-CLEANUP");
		Optional<EmailEntity> entity = this.repo.findById(emailRequest.getId());
		if(entity.isPresent()) {
			debugClient.print("ENTITY WAS SAVED");
			this.repo.delete(entity.get());
			return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("[FALLBACK]	REVERTED ENTITY SAVE NO CHANGES WERE MADE");
		}
		debugClient.print("ENTITY WAS NOT SAVED");
		return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("[FALLBACK]	ENTITY WAS NOT SAVED");
	}
	public ResponseEntity<?> getEntitiesIterable(List<String> userIdList) {
		return ResponseEntity.ok(this.repo.findAllById(userIdList));
	}
	public ResponseEntity<?> deleteEmailEntity(String user_id) {
		Optional<EmailEntity> entity = this.repo.findById(user_id);
		if(entity.isEmpty()) return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("ENTITY DOES NOT EXIST");
		this.repo.delete(entity.get());
		return ResponseEntity.status(HttpStatus.SC_ACCEPTED).body("SUCCESSFULLY DELETED ENTITY");
	}
}
