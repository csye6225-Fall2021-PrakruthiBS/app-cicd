package com.example.response;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
	
	private long id;
	
	//@JsonProperty("first_name")
	private String firstName;
	private String lastName;
	private String userName;
	//private DateTimeFormat accountCreated;
	//private DateTimeFormat accountUpdated; 
	public UserResponse(UserEntity user) {
		this.id = user.getId();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.userName = user.getUserName();
	}
	
	

}
