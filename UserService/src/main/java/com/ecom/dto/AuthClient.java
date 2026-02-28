package com.ecom.dto;

import java.util.List;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient("auth-client")
public interface AuthClient {

	@PostMapping("/")
	public Optional<com.ecom.factory.model.response.Auth> postAuth(@RequestBody com.ecom.factory.model.response.Auth authBody);
	
	@DeleteMapping("/{id}")
	public boolean deleteEntityById(@PathVariable("id") String user_id);
	
	@GetMapping("/authorities/{id}")
	public List<String> getAuthorities(@PathVariable("id") String id);
}
