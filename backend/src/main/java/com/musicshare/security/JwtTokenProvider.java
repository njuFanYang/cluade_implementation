package com.musicshare.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT Token Provider
 *
 * <p>Handles JWT token generation, validation, and parsing. Uses HMAC-SHA256
 * algorithm for token signing.
 *
 * <h3>Token Structure:</h3>
 * <pre>
 * Header: Algorithm and token type
 * Payload: User information and claims
 * Signature: HMAC-SHA256(header + payload, secret)
 * </pre>
 *
 * @author MusicShare Team
 * @version 1.0.0
 * @since 2026-02-20
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Generate JWT token from authentication
     *
     * @param authentication Spring Security authentication object
     * @return JWT token string
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Generate JWT token with custom expiration
     *
     * @param username Username
     * @param roles User roles
     * @param expirationMs Token expiration in milliseconds
     * @return JWT token string
     */
    public String generateTokenWithExpiration(String username, String roles, Long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Get username from JWT token
     *
     * @param token JWT token
     * @return Username
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * Get roles from JWT token
     *
     * @param token JWT token
     * @return Roles string
     */
    public String getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("roles", String.class);
    }

    /**
     * Validate JWT token
     *
     * @param token JWT token
     * @return true if token is valid
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    /**
     * Get claims from JWT token
     *
     * @param token JWT token
     * @return Claims object
     */
    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Get token expiration time
     *
     * @return Expiration time in milliseconds
     */
    public Long getExpirationTime() {
        return jwtExpiration;
    }

}
