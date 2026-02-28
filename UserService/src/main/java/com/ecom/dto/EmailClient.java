package com.ecom.dto;

import java.util.List;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("email-client")
public interface EmailClient {

	@GetMapping("/check/{address}")
	public boolean checkEmailAvailability(@PathVariable("address") String address);
	
	@PostMapping("/")
	public Optional<com.ecom.factory.model.request.Email> postEmailEntity(@RequestBody com.ecom.factory.model.request.Email EmailRequest);
	
	@GetMapping("/{id}")
	public Optional<com.ecom.factory.model.request.Email> getEmailEntity(@PathVariable("id") String id);
	
	@PostMapping("/list-entity")
	public List<com.ecom.factory.model.request.Email> getEntitiesIterable(@RequestBody List<String> userids);
	
	@DeleteMapping("/{id}")
	public boolean deleteEntityById(@PathVariable("id") String user_id);
}