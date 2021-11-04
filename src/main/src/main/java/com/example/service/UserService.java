package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.entity.UserEntity;
import java.util.*;

import com.example.repository.UserRepository;

import antlr.collections.List;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	
	public @ResponseBody java.util.List<UserEntity> getUserInfo() {
		System.out.println("Response-Service");
		return  userRepository.findAll();
	}

}
