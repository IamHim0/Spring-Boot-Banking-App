package com.cfkiatong.springbootbankingapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    private String superSecretKey = "mysuperlongrandomsecretkeyatleast256bitslong";
    Key key = Keys.hmacShaKeyFor(superSecretKey.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String id) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .subject(id)
                .claim("roles", List.of("admin") )
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000 * 2))
                .signWith(key)
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