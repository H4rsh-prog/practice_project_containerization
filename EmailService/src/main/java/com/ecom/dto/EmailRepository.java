package com.ecom.dto;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ecom.model.entity.EmailEntity;

@Repository
public interface EmailRepository extends CrudRepository<EmailEntity, String> {
	
	public boolean existsByAddress(String address);
}
