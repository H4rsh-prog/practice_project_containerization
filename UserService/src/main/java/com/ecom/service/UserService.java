package com.ecom.service;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Fallback;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.dto.AuthClient;
import com.ecom.dto.EmailClient;
import com.ecom.dto.UserRepository;
import com.ecom.factory.util.DEBUG;
import com.ecom.model.entity.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
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
	private AuthClient authClient;
	@Autowired
	private DEBUG debugClient;
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
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
	public ResponseEntity<?> availabilityCheck(com.ecom.factory.model.request.User userRequest) {
		if(!checkUsernameAvailability(userRequest.getUsername())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("USERNAME NOT AVAILABLE");
		debugClient.print("USERNAME AVAILABLE");
		if(!emailClient.checkEmailAvailability(userRequest.getEmailAddress())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("EMAIL NOT AVAILABLE");
		debugClient.print("EMAIL AVAILABLE");
		return ResponseEntity.ok("AVAILABLE");
	}
	public ResponseEntity<?> saveUserEntity(UserEntity entity) {
		debugClient.print("SAVING ENTITY TO DATABASE");
		Optional<UserEntity> savedEntity = Optional.of(this.repo.save(entity));
		if(savedEntity.isEmpty()) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("COULD NOT SAVE USER ENTITY");
		debugClient.print("ENTITY SAVED WITH ID "+savedEntity.get().getId());
		return ResponseEntity.ok(savedEntity.get());
	}
	public boolean saveEmailEntity(com.ecom.factory.model.request.Email emailRequest){
		debugClient.print("SENDING EMAIL MAPPING REQUEST "+emailRequest);
		Optional<com.ecom.factory.model.request.Email> emailResponse = emailClient.postEmailEntity(emailRequest);
		debugClient.print("FEIGN CLIENT RETURNED WITH THE RESPONSE "+emailResponse.get());
		return emailResponse.isPresent();
	}
	public boolean saveAuthEntity(com.ecom.factory.model.response.Auth authBody){
		debugClient.print("SENDING AUTH MAPPING REQUEST "+authBody);
		Optional<com.ecom.factory.model.response.Auth> authResponse = authClient.postAuth(authBody);
		debugClient.print("FEIGN CLIENT RETURNED WITH THE RESPONSE "+authResponse);
		return authResponse.isPresent();
	}
	@HystrixCommand(commandKey = "saveRevert", fallbackMethod = "revertSaveUser")
	public ResponseEntity<?> saveUser(com.ecom.factory.model.request.User userRequest) throws Exception {
		debugClient.print("RECEIVED ENTITY SAVE REQUEST FOR "+userRequest);
		ResponseEntity<?> responseEntity = this.availabilityCheck(userRequest);
		if(responseEntity.getStatusCode().is4xxClientError()) return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
		try {
			//PARSING USER ENTITY OBJECT
			String userRequestJSON = this.mapper.writeValueAsString(userRequest);
			responseEntity = saveUserEntity(this.mapper.readValue(userRequestJSON, UserEntity.class));
			if(responseEntity.getStatusCode().is5xxServerError()) return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
			UserEntity entity = (UserEntity) responseEntity.getBody();
			//PARSING EMAIL ENTITY OBJECT
			com.ecom.factory.model.request.Email emailRequest = this.mapper.readValue(userRequestJSON, com.ecom.factory.model.request.Email.class);
			emailRequest.setId(entity.getId());
			responseEntity = saveEmailEntity(emailRequest);
			//COMPILING USER RESPONSE
			debugClient.print("COMPILING USER RESPONSE");
			com.ecom.factory.model.response.Email emailResponse;
			emailResponse = this.mapper.readValue(
						this.mapper.writeValueAsString(responseEntity.getBody()), 
						com.ecom.factory.model.response.Email.class
					);
			debugClient.print("EMAIL RESPONSE COMPILED");
			com.ecom.factory.model.response.User userResponse = this.mapper.readValue(
						this.mapper.writeValueAsString(entity),
						com.ecom.factory.model.response.User.class
					);
			userResponse.setEmail(emailResponse);
			debugClient.print("USER RESPONSE COMPILED");
			//PARSING AUTH ENTITY OBJECT
			com.ecom.factory.model.response.Auth authBody = new com.ecom.factory.model.response.Auth(
						entity.getId(),
						this.encoder.encode(userRequest.getPassword()),
						List.of("USER")
					);
			saveAuthEntity(authBody);
			return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
		} catch (JsonProcessingException e) {
			debugClient.print("EXCEPTION CAUGHT WHILE PROCESSESING JSON VIA OBJECTMAPPER");
			debugClient.print(e.getMessage());
		}
		throw new Exception("[SAVE-USER]	UNEXPECTED BYPASS DID NOT ENTER FALLBACK");
	}
	public ResponseEntity<?> revertSaveUser(com.ecom.factory.model.request.User userRequest){
		debugClient.print("METHOD FAILED ENTERED FALLBACK-CLEANUP");
		Optional<UserEntity> entity = this.repo.findByUsername(userRequest.getUsername());
		if(entity.isPresent()) {
			debugClient.print("ENTITY WAS SAVED");
			this.repo.delete(entity.get());
			this.emailClient.deleteEmailEntity(entity.get().getId());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("[FALLBACK]	REVERTED ENTITY SAVE NO CHANGES WERE MADE");
		}
		debugClient.print("ENTITY WAS NOT SAVED");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("[FALLBACK]	ENTITY WAS NOT SAVED");
	}
}
