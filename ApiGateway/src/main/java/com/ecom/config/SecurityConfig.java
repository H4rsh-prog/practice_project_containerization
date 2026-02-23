package com.ecom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ecom.service.UserInfoService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(CSRF->CSRF.disable())
				.httpBasic(Customizer.withDefaults())
				.formLogin(FORM->FORM.disable())
				.authorizeHttpRequests(request->request
					.requestMatchers("/generateToken").permitAll()
					.requestMatchers("/UserService/**").hasAnyAuthority("USER","ADMIN")
					.requestMatchers("/EmailService/**").hasAnyAuthority("ADMIN")
					.anyRequest().authenticated()
				)
				.sessionManagement(SESSION->SESSION.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
	}
	@Bean
	AuthenticationProvider getAuthProvider() {
		DaoAuthenticationProvider DaoAuth = new DaoAuthenticationProvider();
		DaoAuth.setPasswordEncoder(getPasswordEncoder());
		DaoAuth.setUserDetailsService(getUserDetailsService());
		return DaoAuth;
	}
	@Bean
	AuthenticationManager getAuthManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	UserDetailsService getUserDetailsService() {
		return new UserInfoService();
	}
}
