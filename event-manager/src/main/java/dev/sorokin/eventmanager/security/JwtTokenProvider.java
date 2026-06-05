package dev.sorokin.eventmanager.security;


import dev.sorokin.eventmanager.domain.UserRole;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, String login, UserRole role) {

        return Jwts.builder()
                .subject(userId.toString())
                .claim("login",login)
                .claim("role",role.name())
                .signWith(secretKey)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .compact();

    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        var claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }

    public String getRoleFromToken(String token) {
        var claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("role").toString();
    }
}
