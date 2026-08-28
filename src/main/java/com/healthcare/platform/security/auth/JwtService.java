package com.healthcare.platform.security.auth;
import com.healthcare.platform.model.auth.*;
import com.healthcare.platform.repository.auth.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Named explicitly: com.healthcare.platform.security.JwtService has the same simple
// name, and two beans sharing the default name "jwtService" break context startup.
@Service("authJwtService")
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret:ChangeThisToASecureRandomSecretKeyThatIsAtLeast256BitsLong}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    public String extractEmail(String token) {
        Claims claims = parseClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    public boolean isTokenValid(String token) {
        return parseClaims(token) != null;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            // JwtException already covers ExpiredJwtException.
            return null;
        }
    }
}
