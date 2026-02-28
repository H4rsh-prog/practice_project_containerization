package com.ecom.factory.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@JsonProperty("username")
	private String username;
	@JsonProperty("email")
	private com.ecom.factory.model.response.Email email;
	@JsonProperty("authorities")
	private List<String> authorities;
}
