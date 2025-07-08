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

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;


    // 2. Inject it in the constructor
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomUserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Tell it how to find users
        authProvider.setPasswordEncoder(passwordEncoder()); // Tell it how to check passwords
        return authProvider;
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
                        // PUBLIC ENDPOINTS: Anyone can access these
                        .requestMatchers(HttpMethod.POST,"/api/auth/login", "/api/users/register").permitAll() // Allow public access to the registration endpoint
                        .requestMatchers(HttpMethod.GET,  "/api/products", "/api/products/**").permitAll()
                        // ADD THIS LINE to allow access to the documentation
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // AUTHENTICATED USER ENDPOINTS: Any logged-in user can access these
                        .requestMatchers(HttpMethod.GET, "/api/cart", "/api/orders", "/api/orders/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/cart/items", "/api/orders").authenticated()
                        .requestMatchers(HttpMethod.PUT,  "/api/cart/items/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/cart/items/**").authenticated()

                        // We will add logic for a user to view/update their own profile later
                        .requestMatchers("/api/users/**").hasRole("ADMIN") // For now, only ADMINs can see/modify user lists

                        // ADMIN ONLY ENDPOINTS: Only users with ROLE_ADMIN can access these
                        .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,  "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,  "/api/products/**").hasRole("ADMIN")

                        // Secure all other requests
                        .anyRequest().authenticated() // Secure all other requests
                )
                .authenticationProvider(authenticationProvider())
                // Tell Spring to use our custom filter before the standard username/password filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

}