package com.psi.business_service;

import java.security.InvalidParameterException;
import com.google.common.base.Strings;
import com.psi.models.Authentication;
import com.psi.models.contracts.user_management.User;

import org.springframework.stereotype.Service;

@Service
public class UserManagementValidation {
	
	public boolean isCreateUserValidParameters(User user) {
		if(!Strings.isNullOrEmpty(user.getUsername()) && !Strings.isNullOrEmpty(user.getEmail())) {
			return true;
		} 
		return false;
	}
	
	public boolean isAuthenticationValidParameters(Authentication auth) {		
		if(!Strings.isNullOrEmpty(auth.getUsername()) || !Strings.isNullOrEmpty(auth.getPassword())) {
			return true;
		}
		throw new InvalidParameterException("Invalid Parameter/s");
	}
}
