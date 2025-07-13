package com.karrardelivery.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration.minutes}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration.minutes}")
    private long refreshTokenExpiration;

    public String generateAccessToken(String phone) {
        return generateToken(phone, accessTokenExpiration, "access");
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
