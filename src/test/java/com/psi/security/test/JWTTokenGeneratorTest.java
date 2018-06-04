package com.psi.security.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.psi.models.contracts.user_management.User;
import com.psi.security.JWTTokenGenerator;
import com.psi.security.TokenGenerator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JWTTokenGeneratorTest {
	
	@Configuration
	static class TestConfig {
		
		@Bean
		TokenGenerator TestConfigContextConfiguration() {
			return new JWTTokenGenerator();
		}
	}
	
	@Autowired
	TokenGenerator tokenGenerator;
	
	@Test
	public void generateTokenTest() {
		User user = new User();
		user.setUsername("graceidepaz0330");
		user.setFirstname("grace");
		String token = tokenGenerator.generateToken(user);
		System.out.println(token);
		assertNotNull(token);
	}
	
	@Test
	public void parseCorrectToken() {
		User user = tokenGenerator.parseToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJncmFjZWlkZXBhejAzMzAiLCJ1c2VyTmFtZSI6ImdyYWNlaWRlcGF6MDMzMCIsImZpcnN0TmFtZSI6ImdyYWNlIn0.GFPi6NRGkw8RHErGrhkjcjCbPfMlZ2BuNeAKwhrWg9c");
		assertNotNull(user);
		assert(user.getUsername().equals("graceidepaz0330"));
		assert(user.getFirstname().equals("grace"));
	}
	
	@Test
	public void parseIncorrectToken() {
		User user = tokenGenerator.parseToken("sdsdssd.83383.dsdsds");
		assertNull(user);
	}

}
