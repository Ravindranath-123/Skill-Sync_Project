package com.skillsync.mentor.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: JwtUtil
 * DESCRIPTION:
 * Utility class for Jwt token parsing, specifically for extracting 
 * user IDs and roles in the Mentor Service.
 * ================================================================
 */
@Component
public class JwtUtil {

    private final String SECRET = "skillsyncsecretkeyskillsyncsecretkey"; 
    // ⚠️ must be at least 32 chars for HS256

    /* ================================================================
     * METHOD: getSigningKey
     * DESCRIPTION: Returns the HMAC signing key derived from the secret.
     * ================================================================ */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /* ================================================================
     * METHOD: extractUserId
     * DESCRIPTION: Parses the JWT token to retrieve the embedded userId.
     * ================================================================ */
    public Long extractUserId(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        return ((Number) claims.get("userId")).longValue();
    }

    /* ================================================================
     * METHOD: extractRole
     * DESCRIPTION: Parses the JWT token to retrieve the embedded role.
     * ================================================================ */
    public String extractRole(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        return (String) claims.get("role");
    }
}