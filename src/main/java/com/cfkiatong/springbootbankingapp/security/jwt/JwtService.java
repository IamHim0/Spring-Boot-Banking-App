package com.cfkiatong.springbootbankingapp.security.jwt;

import com.cfkiatong.springbootbankingapp.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String superSecretKey;

    public String generateToken(UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .subject(userPrincipal.getId().toString())
                .claim("roles", userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000 * 2))
                .signWith(getKey())
                .compact();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(superSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}