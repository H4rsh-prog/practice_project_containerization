package com.ecom.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(indexes = {@Index(name = "usernameIndex", columnList = "username", unique = true)})
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	@Column(unique = true)
	private String username;
}
