package com.example.response;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.example.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor
@JsonInclude
public class UserResponse {
	
	//private long id;
	
	@JsonProperty("first_name")
	private String firstName;
	@JsonProperty("last_name")
	private String lastName;
	@JsonProperty("user_name")
	private String userName;
	@JsonProperty("account_created")
	private LocalDateTime accountCreated;
	@JsonProperty("account_updated")
	private LocalDateTime accountUpdated; 
	@JsonProperty("isVerified")
	private Boolean isVerified;	
	@JsonProperty("verified_on")
	private String verified_on;
	
	public UserResponse(UserEntity user) {
		System.out.println("UserResponseConstructor");
		System.out.println(user.getUserName());
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.userName = user.getUserName();
		this.accountCreated = user.getAccount_created();
		this.accountUpdated = user.getAccount_updated();
		this.isVerified = user.getVerified();
		this.verified_on = user.getVerified_on();
	}

	
	

}
