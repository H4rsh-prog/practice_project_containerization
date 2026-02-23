package com.ecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;

@SpringBootApplication
@EnableHystrix
@EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE, mode = AdviceMode.ASPECTJ)
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
	@Bean
	@Primary
	@Order(value = Ordered.HIGHEST_PRECEDENCE)
	HystrixCommandAspect getHystrixCommandAspect() {
		return new HystrixCommandAspect();
	}
}
