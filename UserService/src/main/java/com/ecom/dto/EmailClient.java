package com.ecom.dto;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("email-client")
public interface EmailClient {

	@GetMapping("/{id}")
	public ResponseEntity<?> getEmailEntity(@PathVariable("id") String id);
	
	@GetMapping("/check/{address}")
	public boolean checkEmailAvailability(@PathVariable("address") String address);
	
	@PostMapping("/")
	public ResponseEntity<?> postEmailEntity(@RequestBody com.ecom.factory.model.request.Email EmailRequest);
	
	@PostMapping("/list-entity")
	public ResponseEntity<?> getEntitiesIterable(@RequestBody List<String> userids);
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteEmailEntity(@PathVariable("id") String user_id);
}