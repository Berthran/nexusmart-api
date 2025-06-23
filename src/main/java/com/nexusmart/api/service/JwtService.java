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
//        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        SecretKey key = getSigningKey();
        System.out.println("Secret key: " + key);

        // 4. Build the JWT
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // Method to extract the username (email) from a token
    public String getUsernameFromToken(String token)  {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Verify with the same key used to sign it
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Method to check if a token is valid
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        // A token is valid if the username matches and the token has not expired
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Private helper method to check token expiration
    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        // Check if the expiration date is before the current date
        return expiration.before(new Date());
    }

    // Private helper method to get the signing key (we use this in multiple places)
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
