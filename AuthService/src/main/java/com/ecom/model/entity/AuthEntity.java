package com.ecom.model.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {@Index(name = "authUsernameIndex", columnList = "username", unique = true)})
public class AuthEntity {
	@Id
	@JsonProperty("user_id")
	private String userId;
	@Column(unique = true, nullable = false)
	@JsonProperty("username")
	private String username;
	@Column(nullable = false)
	@JsonProperty("password")
	private String password;
	@JsonProperty("auth_authorities")
	private List<String> authorities;
}
