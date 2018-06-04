package com.psi.security;

import org.springframework.stereotype.Service;

import com.psi.models.contracts.user_management.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JWTTokenGenerator implements TokenGenerator {

	@Override
	public String generateToken(User user) {
		Claims claims = Jwts.claims()
				.setSubject(user.getUsername());
		claims.put("userName", user.getUsername());
		claims.put("firstName", user.getFirstname());
		
		return Jwts.builder().addClaims(claims).signWith(SignatureAlgorithm.HS256, "s3cr3t").compact();
	}

	@Override
	public User parseToken(String token) {
		User user = null;
		try {
			Claims body = Jwts.parser().setSigningKey("s3cr3t").parseClaimsJws(token).getBody();
			
			user = new User();
			user.setUsername(body.get("userName").toString());
			user.setFirstname(body.get("firstName").toString());
		} catch (JwtException e) {
			System.out.println(e.getMessage());
		}
		
		return user;
	}

}
