package com.ecom.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = {@Index(name = "emailIndex", columnList = "address", unique = true)})
public class EmailEntity {
	@Id
	@JsonProperty("user_id")
	private String id;
	@Column(unique = true)
	@JsonProperty("email_address")
	private String address;
}
