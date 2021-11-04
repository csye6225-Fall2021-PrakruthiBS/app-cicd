package com.example.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.response.UserResponse;
import com.example.service.UserService;

import antlr.collections.List;

import com.example.entity.UserEntity;

@RestController
@RequestMapping("/v1/user/self/")
public class UserController {

	@Autowired
	UserService userService;
	
	@GetMapping("/getUser")
	public java.util.List<UserResponse> getUser() {
		System.out.println("Response-Controller");
		java.util.List<UserEntity> userList = userService.getUserInfo();
		java.util.List<UserResponse> userResponseList = new ArrayList<UserResponse>();
		userList.stream().forEach(User -> {
			userResponseList.add(new UserResponse(User));
		});
		return userResponseList;
	}
	
	/*@GetMapping("/getUserInfo/{userName}")
	public List<UserResponse> getByUsername(@PathVariable String userName){
		
	}*/

	
	
}
