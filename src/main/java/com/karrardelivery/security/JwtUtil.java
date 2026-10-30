package com.karrardelivery.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration.minutes}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration.minutes}")
    private long refreshTokenExpiration;

    public String generateAccessToken(String phone) {
        return generateAccessToken(phone, Collections.emptyList());
    }

    /**
     * Access token that also carries the user's roles, e.g. "roles": ["ROLE_ADMIN"].
     * The dashboard reads this claim to decide which menus and pages to show.
     */
    public String generateAccessToken(String phone, List<String> roles) {
        return Jwts.builder()
                .setSubject(phone)
                .claim("type", "access")
                .claim("roles", roles == null ? Collections.emptyList() : roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (accessTokenExpiration * 60 * 1000)))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String generateRefreshToken(String phone) {
        return generateToken(phone, refreshTokenExpiration, "refresh");
    }

    private String generateToken(String phone, long expirationMillis, String type) {
        return Jwts.builder()
                .setSubject(phone)
                .claim("type", type)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (expirationMillis * 60 * 1000) ))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String extractPhone(String token) {
        return getClaims(token).getSubject();
    }

    public String extractTokenType(String token) {
        return (String) getClaims(token).get("type");
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token); // throws if invalid
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isRefreshToken(String token) {
        try {
            var claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }
    public Date extractExpiration(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

}
