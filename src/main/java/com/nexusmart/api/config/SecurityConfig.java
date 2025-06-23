package com.nexusmart.api.config;

import com.nexusmart.api.security.JwtAuthenticationFilter;
import com.nexusmart.api.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF protection for stateless REST APIs
                .csrf(csrf -> csrf.disable())
                // Make our session management stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 2. Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to the registration endpoint
                        .requestMatchers(HttpMethod.POST,"/api/auth/login", "/api/users/register", "/api/products").permitAll() // Allow public access to the registration endpoint
                        // Allow public access to GET User & Product information for now
                        .requestMatchers(HttpMethod.GET, "/api/users/**", "/api/products/**").permitAll()
                        // Allow public access to PUT (update) User & Product information for now
                        .requestMatchers(HttpMethod.PUT, "/api/users/**", "/api/products/**").permitAll()
                        // Allow public access to DELETE User & Product information
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**", "/api/products/**").permitAll()
                        // Secure all other requests
                        .anyRequest().authenticated() // Secure all other requests
                );

        return http.build();
    }

}