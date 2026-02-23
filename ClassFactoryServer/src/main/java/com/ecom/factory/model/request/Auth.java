package com.ecom.factory.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Auth {
	@JsonProperty("username")
	private String username;
	@JsonProperty("password")
	private String password;
}
