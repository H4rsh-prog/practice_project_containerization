package com.ecom.dto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("auth-client")
public interface AuthClient {

	@PostMapping("/")
	public ResponseEntity<?> postAuth(@RequestBody com.ecom.factory.model.response.Auth authBody);
}
