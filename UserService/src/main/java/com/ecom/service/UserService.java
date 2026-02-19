package com.ecom.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ecom.dto.EmailClient;
import com.ecom.dto.UserRepository;
import com.ecom.factory.util.DEBUG;
import com.ecom.model.entity.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import jakarta.annotation.PostConstruct;

@Service
public class UserService {
	@Autowired
	private UserRepository repo;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private EmailClient emailClient;
	@Autowired
	private DEBUG debugClient;
	
	public ResponseEntity<?> getUserByUsername(String username) throws JsonProcessingException{
		com.ecom.factory.model.response.User userResponse;
		Optional<UserEntity> userEntity = this.repo.findByUsername(username);
		debugClient.print("FETCHED ENTITY WITH THE USERNAME "+username+" : "+userEntity.get());
		if(userEntity.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("USER ENTITY DOES NOT EXIST");
		}
		userResponse = this.mapper.readValue(this.mapper.writeValueAsString(userEntity), com.ecom.factory.model.response.User.class);
		debugClient.print("SENDING REQUEST TO FETCH EMAIL ENTITY WITH THE ID "+userEntity.get().getId());
		try {
			ResponseEntity<?> clientResponse = this.emailClient.getEmailEntity(userEntity.get().getId());
			debugClient.print("FEIGN CLIENT RETURNED WITH RESPONSE "+clientResponse);
			com.ecom.factory.model.response.Email emailResponse = this.mapper.readValue(
					this.mapper.writeValueAsString(clientResponse.getBody()), com.ecom.factory.model.response.Email.class
				);
			userResponse.setEmail(emailResponse);
			return ResponseEntity.status(HttpStatus.OK).body(userResponse);
		} catch (FeignException e) {
			debugClient.print("FEIGN CLIENT FAILED");
			debugClient.print("RETURNING INCOMPLETE OBJECT OF USER ENTITY");
			return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(userResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ResponseEntity<?> getEntitiesIterable(List<String> usernameList){
		debugClient.print("RECEIVED FETCH REQUEST FOR THE LIST "+usernameList);
		List<UserEntity> userEntityList = this.repo.findAllByUsername(usernameList);
		List<String> userIdList = new ArrayList<>();
		for(UserEntity entity : userEntityList) {
			userIdList.add(entity.getId());
		}
		debugClient.print("FETCHING EMAIL ENTITIES FOR THE LIST "+userIdList);
		try {
			ResponseEntity<?> clientResponse = this.emailClient.getEntitiesIterable(userIdList);
			debugClient.print("FEIGN CLIENT RETURNED WITH THE RESPONSE "+clientResponse);
			List<com.ecom.factory.model.request.Email> emailEntityList = this.mapper.readerForListOf( com.ecom.factory.model.request.Email.class).readValue(
						this.mapper.writeValueAsString(clientResponse.getBody()), ArrayList.class
					);
			Map<String, com.ecom.factory.model.response.Email> emailMapping = new HashMap<>();
			Iterator lt = emailEntityList.iterator();
			while(lt.hasNext()) {
				Object element = lt.next();
				emailMapping.put(this.mapper.readValue(this.mapper.writeValueAsString(element), com.ecom.factory.model.request.Email.class).getId(), this.mapper.readValue(this.mapper.writeValueAsString(element), com.ecom.factory.model.response.Email.class));
			}
			List<com.ecom.factory.model.response.User> userResponseList = new ArrayList<>();
			for(UserEntity entity : userEntityList) {
				com.ecom.factory.model.response.User userResponse = this.mapper.readValue(this.mapper.writeValueAsString(entity), com.ecom.factory.model.response.User.class);
				userResponse.setEmail(emailMapping.get(entity.getId()));
				userResponseList.add(userResponse);
			}
			return ResponseEntity.status(HttpStatus.OK).body(userResponseList);
		} catch (FeignException e) {
			debugClient.print("FEIGN CLIENT FAILED");
			return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body("INTERNAL SERVER ERROR COULD NOT FETCH SUFFICIENT DATA");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean checkUsernameAvailability(String username) {
		return !this.repo.existsByUsername(username);
	}
	
	public ResponseEntity<?> saveUser(com.ecom.factory.model.request.User userRequest) throws JsonProcessingException {
		debugClient.print("RECEIVED ENTITY SAVE REQUEST FOR "+userRequest);
		if(!checkUsernameAvailability(userRequest.getUsername())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("USERNAME NOT AVAILABLE");
		debugClient.print("USERNAME AVAILABLE");
		if(!emailClient.checkEmailAvailability(userRequest.getEmailAddress())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("EMAIL NOT AVAILABLE");
		debugClient.print("EMAIL AVAILABLE");
		String json = this.mapper.writeValueAsString(userRequest);
		debugClient.print("SAVING ENTITY TO DATABASE");
		Optional<UserEntity> entity = Optional.of(this.repo.save(
					this.mapper.readValue(json, UserEntity.class)
				));
		if(entity.isEmpty()) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("COULD NOT SAVE USER ENTITY");
		debugClient.print("ENTITY SAVED WITH ID "+entity.get().getId());
		com.ecom.factory.model.request.Email emailRequest = this.mapper.readValue(json, com.ecom.factory.model.request.Email.class);
		emailRequest.setId(entity.get().getId());
		debugClient.print("SENDING EMAIL MAPPING REQUEST "+emailRequest);
		try {
			ResponseEntity<?> clientResponse = emailClient.postEmailEntity(emailRequest);
			debugClient.print("FEIGN CLIENT RETURNED WITH THE RESPONSE "+clientResponse);
			debugClient.print("letting request pass : "+!(clientResponse.getStatusCode().is2xxSuccessful()));
			if(!(clientResponse.getStatusCode().is2xxSuccessful())) {
				debugClient.print("EMAIL MAPPING FAILED ATTEMPTING TO ROLL BACK CHANGES");
				this.repo.delete(entity.get());
				debugClient.print("ROLLING BACK CHANGES NO CHANGES MADE");
				return ResponseEntity.status(clientResponse.getStatusCode()).body("UNMAPPED EMAIL EXCEPTION");
			}
			com.ecom.factory.model.response.Email emailResponse = this.mapper.readValue(
						this.mapper.writeValueAsString(clientResponse.getBody()), com.ecom.factory.model.response.Email.class
					);
			debugClient.print("EMAIL MAPPED");
			com.ecom.factory.model.response.User userResponse = this.mapper.readValue(
						this.mapper.writeValueAsString(entity),
						com.ecom.factory.model.response.User.class
					);
			userResponse.setEmail(emailResponse);
			return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
		} catch (FeignException e) {
			debugClient.print("FEIGN CLIENT FAILED");
			this.repo.delete(entity.get());
			debugClient.print("ROLLING BACK CHANGES NO CHANGES MADE");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL SERVER ERROR NO CHANGES WERE MADE");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
