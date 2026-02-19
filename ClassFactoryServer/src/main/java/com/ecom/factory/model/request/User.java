package com.ecom.factory.model.request;

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
	@JsonProperty("email_address")
	private String emailAddress;
}
