package com.psi.security;

import com.psi.models.contracts.user_management.User;

public interface TokenGenerator {
	
	public String generateToken(User user);
	
	public User parseToken(String token);

}
