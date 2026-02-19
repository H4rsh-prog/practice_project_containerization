package com.ecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;


@SpringBootApplication
@EnableFeignClients
public class UserServiceApplication {

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}

@Configuration
class AdditionalConfig{	
	@Bean
	com.ecom.factory.util.DEBUG getDebugClient(){
		return new com.ecom.factory.util.DEBUG("USER-SERVICE");
	}
}
