package com.skillsync.auth.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: JwtUtil
 * DESCRIPTION:
 * Utility for JWT token generation, parsing, and validation 
 * specialized for the Authentication Service.
 * ================================================================
 */
@Component
public class JwtUtil {

    private final String SECRET = "skillsyncsecretkeyskillsyncsecretkey";

    private final long EXPIRATION = 1000 * 60 * 60; // 1 hour

    /* ================================================================
     * METHOD: getSignKey
     * DESCRIPTION: Returns the HMAC signing key for JWT signatures.
     * ================================================================ */
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /* ================================================================
     * METHOD: generateToken
     * DESCRIPTION: Creates a signed JWT token containing user identity and role.
     * ================================================================ */
    public String generateToken(String email, String role,Long Id) {

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", Id)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* ================================================================
     * METHOD: extractEmail
     * DESCRIPTION: Retrieves the subject (email) from the provided JWT.
     * ================================================================ */
    public String extractEmail(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /* ================================================================
     * METHOD: extractRole
     * DESCRIPTION: Retrieves the specific role claim from the JWT.
     * ================================================================ */
    public String extractRole(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    /* ================================================================
     * METHOD: isTokenValid
     * DESCRIPTION: Validates token integrity and expiration.
     * ================================================================ */
    public boolean isTokenValid(String token) {

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}