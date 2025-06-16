package com.nexusmart.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF protection for stateless REST APIs
                .csrf(csrf -> csrf.disable())
                // 2. Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to the registration endpoint
                        .requestMatchers(HttpMethod.POST,"/api/users/register").permitAll() // Allow public access to the registration endpoint
                        // Allow public access to GET user information for now
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                        // Allow public access to PUT (update) user information for now
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll()
                        // Allow public access to DELETE user information
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").permitAll()
                        // Allow public access to CREATE a product
                        .requestMatchers(HttpMethod.POST, "/api/products").permitAll()
                        // Secure all other requests
                        .anyRequest().authenticated() // Secure all other requests
                );

        return http.build();
    }
}