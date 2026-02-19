package com.ecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class EmailServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(EmailServiceApplication.class, args);
	}

}

@Configuration
class AdditionalConfig{	
	@Bean
	com.ecom.factory.util.DEBUG getDebugClient(){
		return new com.ecom.factory.util.DEBUG("EMAIL-SERVICE");
	}
}
