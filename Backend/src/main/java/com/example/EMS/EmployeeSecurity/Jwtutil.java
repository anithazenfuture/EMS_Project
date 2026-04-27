package com.example.EMS.EmployeeSecurity;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class Jwtutil {
	
	  @Value("${jwt.secret}")
	    private String secret;

	    @Value("${jwt.expiry}")
	    private long expiry;

	    public String generateToken(String email) {
	        Key key = Keys.hmacShaKeyFor(secret.getBytes());

	        return Jwts.builder()
	                .setSubject(email)
	                .setIssuedAt(new Date())
	                .setExpiration(new Date(System.currentTimeMillis() + expiry))
	                .signWith(key)
	                .compact();
	    }

}
