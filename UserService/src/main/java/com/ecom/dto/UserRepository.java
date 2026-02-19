package com.ecom.dto;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ecom.model.entity.UserEntity;

import feign.Param;

import java.util.List;


@Repository
public interface UserRepository extends CrudRepository<UserEntity, String> {

	public boolean existsByUsername(String username);
	public Optional<UserEntity> findByUsername(String username);
	@Query(nativeQuery = true, value = "SELECT * FROM user_entity WHERE username IN :usernames")
	public List<UserEntity> findAllByUsername(@Param("usernames") List<String> usernames);
}
