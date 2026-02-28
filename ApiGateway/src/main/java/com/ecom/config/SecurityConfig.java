package com.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecom.filter.JwtFilter;
import com.ecom.service.UserInfoService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired JwtFilter jwtFilter;
	
	@Bean
	SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(CSRF->CSRF.disable())
				.httpBasic(BASIC->BASIC.disable())
				.formLogin(Customizer.withDefaults())
				.authorizeHttpRequests(request->request
					.requestMatchers("/user/generateToken", "/user/status", "/email/status", "/auth/status").permitAll()
					.requestMatchers(HttpMethod.POST, "/user/").permitAll()
					.requestMatchers("/user/**").hasAnyAuthority("USER","ADMIN")
					.requestMatchers("/email/**").hasAnyAuthority("ADMIN")
					.anyRequest().authenticated()
				)
//				.sessionManagement(SESSION->SESSION.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(this.jwtFilter , UsernamePasswordAuthenticationFilter.class)
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
