package com.ecom.factory.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email {
	@JsonProperty("user_id")
	private String id;
	@JsonProperty("email_address")
	private String address;
}
