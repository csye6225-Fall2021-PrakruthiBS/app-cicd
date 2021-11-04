package com.example.CreateUserRequest;

import java.time.Instant;
import java.time.LocalDateTime;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.lang.NonNull;

import lombok.Getter;
import lombok.Setter;

//@Getter
//@Setter
public class CreateUserRequest {
	
	@NotBlank (message = "First name can't be blank")
	//@NotNull(message = "Firstname is required")
	private String first_name;
	
	@NotBlank (message = "Last name can't be blank")
	//@NotNull(message = "Lastname is required")
	private String last_name;
	
	@NotBlank (message = "username is required")
	@NotNull(message = "username is required")
	@Email
	private String user_name;
	
	@NotBlank (message = "password is required")
	@NotNull(message = "password is required")
	private String password;
	
	
	private LocalDateTime accountCreated;
	
	
	private LocalDateTime accountUpdated;


	public String getFirst_name() {
		return first_name;
	}


	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}


	public String getLast_name() {
		return last_name;
	}


	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}


	public String getUser_name() {
		return user_name;
	}


	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public LocalDateTime getAccountCreated() {
		return accountCreated;
	}


	public void setAccountCreated(LocalDateTime accountCreated) {
		this.accountCreated = accountCreated;
	}


	public LocalDateTime getAccountUpdated() {
		return accountUpdated;
	}


	public void setAccountUpdated(LocalDateTime accountUpdated) {
		this.accountUpdated = accountUpdated;
	}
	
	

}
