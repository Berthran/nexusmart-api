package com.nexusmart.api.controller;

import com.nexusmart.api.dto.LoginRequestDTO;
import com.nexusmart.api.dto.LoginResponseDTO;
import com.nexusmart.api.service.JwtService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO requestDTO) {
        // 1. Create an authentication object with the user's credentials
        Authentication authenticationRequest =
                new UsernamePasswordAuthenticationToken(requestDTO.getEmail(), requestDTO.getPassword());
        System.out.println("Authentication object:  " + authenticationRequest);
        // 2. Pass the object to the AuthenticationManager. It will handle the validation.
        // If credentials are bad, it throws an AuthenticationException.
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        System.out.println("Authentication manager response: " + authenticationResponse);
        // 3. If we get here, the user is successfully authenticated.
        // We should store this authentication in the security context.
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        System.out.println("SecurityContextHolder: " + SecurityContextHolder.getContext());
        // 4. If authentication is successful, generate the JWT
        String token = jwtService.generateToken(authenticationResponse);

        // 5. Create the response DTO and return it with a 200 OK status
        LoginResponseDTO response = new LoginResponseDTO(token);
        return ResponseEntity.ok(response);
    }

}
