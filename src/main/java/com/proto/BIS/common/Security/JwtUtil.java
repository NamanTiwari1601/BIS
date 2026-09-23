package com.proto.BIS.common.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret ;
    private static final long EXPRIATION=1000 * 60 * 60 * 24;

    private Key getKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());  // ← use jwtSecret
    }

    public String generateToken(String userName, boolean isAdmin){
        return Jwts.builder()
                .setSubject(userName)
                .claim("isAdmin",isAdmin)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPRIATION))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public  String extractUserName(String token){
        return getClaims(token).getSubject();
    }

    public boolean extractIsAdmin(String token){
        return (boolean) getClaims(token).get("isAdmin");
    }
    public boolean isTokenValid(String token){
        try{
            getClaims(token);
            return true;
        }
        catch (JwtException| IllegalArgumentException e){
            return false;
        }
    }
    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
