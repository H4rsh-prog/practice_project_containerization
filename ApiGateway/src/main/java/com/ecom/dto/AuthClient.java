package com.ecom.dto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("auth-client")
public interface AuthClient {

	@GetMapping("{username}")
	public ResponseEntity<?> getAuth(@PathVariable("username") String username);
}
