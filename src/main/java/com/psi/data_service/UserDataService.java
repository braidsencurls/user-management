package com.psi.data_service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.stereotype.Service;

import com.psi.constants.CollectionConstants;
import com.psi.models.ADPrincipalDetails;
import com.psi.models.Authentication;
import com.psi.models.AuthenticationRequest;
import com.psi.models.LdapDetails;
import com.psi.models.contracts.user_management.User;
import com.psi.security.PsiActiveDirectoryLdapAuthenticationProvider;
import com.psi_incontrol.mongodb_helper.service.IDataManipulation;

@Service
public class UserDataService {
	
	private static Logger logger = LoggerFactory.getLogger(UserDataService.class);
	
	@Autowired
	@Qualifier("dataManipulation")
	IDataManipulation mongoCrud;
	
	@Autowired
	ActiveDirectoryService adService;
	
	@Value("${ldap.domain}")
	String ldapDomain;
	
	@Value("${ldap.url}")
	String ldapUrl;
	
	@Value("${ldap.rootDn}")
	String ldapRootDn;
	
	@Value("${ldap.sAMAccountPrefixLogin}")
	String ldapSAMAccountPrefixLogin;
	
	@Value("${ldap.useUserDatabase}")
	String ldapUseUserDatabase;
	
	@Value("${ldap.searchSecurityPrincipal}")
	String ldapearchSecurityPrincipal;
	
	@Value("${ldap.searchCredentials}")
	String ldapSearchCredentials;
	
	public User createUser(User user) throws PersistenceException, InvalidParameterException, Exception {
		logger.info("User: " + user);
		mongoCrud.insert(user, CollectionConstants.COL_USERS);
		return user;
	}
	
	public User searchUserByUsername(String username) throws Exception {
		return (User) mongoCrud.find("{ username : { $eq : '" + username + "' } }", CollectionConstants.COL_USERS, User.class);
	}
	
	public List<User> fetchAllUsers() {
		return (List<User>) mongoCrud.findAll(User.class, CollectionConstants.COL_USERS);
	}
	
	public User updateUser(User user) throws Exception {
		Map<String, Object> newValue = new HashMap<String, Object>();
		Field[] fields = user.getClass().getDeclaredFields();
		List<Field> fieldList = Arrays.asList(fields);
		for (Field f : fieldList) {
			try {
				if(!f.getName().equals("_id")) {
					PropertyDescriptor objPropertyDescriptor = new PropertyDescriptor(f.getName(), user.getClass());
					Object variableValue = objPropertyDescriptor.getReadMethod().invoke(user);
					newValue.put(f.getName(), variableValue);
				}
			} catch (Exception e) {
				throw new Exception();
			}
		}
		mongoCrud.update("{'username':'" + user.getUsername() + "'}", newValue, User.class);
		return user;
	}
	
	public long removeUser(User user) {
		logger.info("Deleting username: " + user.getUsername());
		return mongoCrud.remove("{'username':'" + user.getUsername() + "'}", CollectionConstants.COL_USERS);
	}
	
	public boolean authenticate(Authentication auth) {
		PsiActiveDirectoryLdapAuthenticationProvider ldapProvider = new PsiActiveDirectoryLdapAuthenticationProvider(
				ldapDomain, ldapUrl, ldapRootDn, ldapSAMAccountPrefixLogin,
				Boolean.valueOf(ldapUseUserDatabase));
		try {
			DirContextOperations e = ldapProvider.doAuthentication(auth.getUsername(), auth.getPassword());
			return true;
		} catch(AuthenticationException e) {
			throw new AuthenticationException();
		}
	}
	
	public ADPrincipalDetails searchFromAD(Authentication auth) {
		AuthenticationRequest request = new AuthenticationRequest();
		LdapDetails ldap = new LdapDetails();
		ldap.setDomain(ldapDomain);
		ldap.setUrl(ldapUrl);
		ldap.setRootDn(ldapRootDn);
		ldap.setsAMAccountPrefinLogin(ldapSAMAccountPrefixLogin);
		ldap.setUseUserDatabase(ldapUseUserDatabase);
		ldap.setSearchCredentials(ldapSearchCredentials);
		ldap.setSearchSecurityPrincipal(ldapearchSecurityPrincipal);
		
		request.setLdapDetails(ldap);
		request.setAuthentication(auth);
		return  adService.searchUser(request);
	}

}
