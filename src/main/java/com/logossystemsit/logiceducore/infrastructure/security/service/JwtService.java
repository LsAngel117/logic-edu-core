package com.logossystemsit.logiceducore.infrastructure.security.service;

import com.logossystemsit.logiceducore.infrastructure.security.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(JwtProperties properties) {
        this.secretKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.expirationMs = properties.expirationMs();
    }

    public String generate(String userId, List<MembershipClaim> memberships) {
        var membershipsList = memberships.stream()
                .map(m -> Map.of(
                        "role", (Object) m.role(),
                        "scopeType", m.scopeType(),
                        "scopeRefId", m.scopeRefId() != null ? m.scopeRefId() : ""
                ))
                .toList();

        return Jwts.builder()
                .subject(userId)
                .claim("memberships", membershipsList)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        var claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<MembershipClaim> extractMemberships(String token) {
        var claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        List<Map<String, Object>> list = claims.get("memberships", List.class);
        if (list == null) {
            return List.of();
        }

        return list.stream()
                .map(m -> new MembershipClaim(
                        String.valueOf(m.get("role")),
                        String.valueOf(m.get("scopeType")),
                        String.valueOf(m.get("scopeRefId"))
                ))
                .toList();
    }
}
