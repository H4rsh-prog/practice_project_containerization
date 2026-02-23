package com.ecom.dto;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ecom.model.entity.AuthEntity;

@Repository
public interface AuthRepository extends CrudRepository<AuthEntity, String>{
	public Optional<AuthEntity> findByUsername(String username);
}
