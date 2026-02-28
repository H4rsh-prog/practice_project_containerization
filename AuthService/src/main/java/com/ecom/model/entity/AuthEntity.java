package com.ecom.model.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthEntity {
	@Id
	@JsonProperty("user_id")
	private String userId;
	@Column(nullable = false)
	@JsonProperty("password")
	private String password;
	@JsonProperty("auth_authorities")
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> authorities;
}
