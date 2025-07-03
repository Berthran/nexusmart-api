package com.nexusmart.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusmart.api.service.CustomUserDetailsService;
import com.nexusmart.api.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Get the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");

        // 2. Check if the header exists and starts with "Bearer"
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            // If not, pass the request to the next filter and exit
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the token from the header (remove "Bearer ")
        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.getUsernameFromToken(jwt);

            // 4. Check if we have a user email and if the user is not already authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details from the database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 5. Check if the token is valid for this user
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // 6. If token is valid, update the Security Context
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Credentials are null as the user is already authenticated
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            // 7. Pass the request to the next filter in the chain
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            // Handle all JWT-related errors (expired, malformed, invalid signature, etc.)
            Map<String, String> errorDetails = new HashMap<>();
            errorDetails.put("error", "Invalid Token");
            errorDetails.put("message", "Your token is invalid or expired. Please log in again.");

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
        }
    }
}
