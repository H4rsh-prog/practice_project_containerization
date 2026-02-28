package com.ecom.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.dto.EmailRepository;
import com.ecom.factory.util.DEBUG;
import com.ecom.model.entity.EmailEntity;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class EmailService {
	@Autowired
	private EmailRepository repo;
	@Autowired
	private DEBUG debugClient;
	
	public Optional<EmailEntity> getEmailById(String id) throws JsonProcessingException{
		debugClient.print("FETCH REQUEST FOR ID "+id);
		Optional<EmailEntity> entity = this.repo.findById(id);
		debugClient.print("ENTITY = "+entity.get());
		return entity;
	}
	public boolean checkAddressAvailability(String address) {
		this.debugClient.print("CHECKING EMAIL AVAILABILITY FOR ADDRESS "+address);
		return !this.repo.existsByAddress(address);
	}
	public Optional<EmailEntity> mapEmail(EmailEntity entity) throws Exception {
		this.debugClient.print("RECEIVED MAPPING REQUEST FOR THE EMAIL ENTITY "+entity);
		entity = this.repo.save(entity);
		debugClient.print("RETURNING SAVED ENTITY "+entity);
		return Optional.of(entity);
	}
	public List<EmailEntity> getEntitiesIterable(List<String> userIdList) {
		debugClient.print("FETCH REQUEST FOR LIST OF IDs");
		List<EmailEntity> entity_list = (List<EmailEntity>) this.repo.findAllById(userIdList);
		debugClient.print("RETURNING FOUND ENTITIES "+entity_list);
		return entity_list;
	}
	public boolean deleteEntityById(String id) {
		debugClient.print("DELETE REQUEST FOR ID "+id);
		if(this.repo.existsById(id)) {
			debugClient.print("ENTITY EXISTS");
			this.repo.deleteById(id);
			debugClient.print("ENTITY DELETED");
			return true;
		}
		return false;
	}
	public Optional<EmailEntity> updateEntity(EmailEntity entity){
		debugClient.print("DELETE REQUEST FOR ID "+entity);
		if(this.repo.existsById(entity.getId())) {
			debugClient.print("ENTITY EXISTS");
			Optional<EmailEntity> curEntity = this.repo.findById(entity.getId());
			entity = this.repo.save(entity);
			debugClient.print("UPDATED ENTITY "+curEntity+" --> "+entity);
			return Optional.of(entity);
		} else {
			debugClient.print("ENTITY DOES NOT EXIST");
		}
		return Optional.of(null);
	}
}
