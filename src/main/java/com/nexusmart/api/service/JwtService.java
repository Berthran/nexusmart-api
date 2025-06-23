package com.nexusmart.api.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // 1. Inject the secret key and expiration time from application.properties
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    // 2. Method to generate the token
    public String generateToken(Authentication authentication) {
        // Get the username (email) from the authenticated principal
        String username = authentication.getName();
        System.out.println("Username: " + username);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // 3. Reconstitute the secret key from the Base64 encoded string
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        System.out.println("Secret key: " + key);

        // 4. Build the JWT
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
}
