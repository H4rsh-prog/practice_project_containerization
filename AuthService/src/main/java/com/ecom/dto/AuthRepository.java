package com.ecom.dto;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.model.entity.AuthEntity;

@Repository
public interface AuthRepository extends CrudRepository<AuthEntity, String>{
	@Query(nativeQuery = true, value = "SELECT * FROM auth_entity_authorities where auth_entity_user_id = :id")
	public List<String> getAuthorities(@Param("id") String id);
}
