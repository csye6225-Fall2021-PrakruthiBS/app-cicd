package com.example.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import com.example.entity.UserEntity;
import com.example.repository.UserRepository;

//@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private UserRepository userRepository;

	@Bean
    public BCryptPasswordEncoder bCryptUserPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username = authentication.getName();
		String password = authentication.getCredentials().toString();
		UserEntity user = userRepository.findByUserName(username);

		if(null != username && null != password && user != null)
		{
			if (username.equalsIgnoreCase(user.getUserName()) && bCryptUserPasswordEncoder().matches(password, user.getPassword())) 
			{
				System.out.println("In Authenticate method");
				return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
			} else {
				return null;
			}
		}
		
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
