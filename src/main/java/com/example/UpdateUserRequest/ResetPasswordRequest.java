package com.example.UpdateUserRequest;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
	
	
	@NotNull(message = "Username is required")
	@NotBlank(message = "Username/email address is required")
	private String user_name;
	
	@NotNull(message = "password is required")
	@NotBlank(message = "Password field is required")
	private String password;
	
	//@JsonIgnore
	//@CreationTimestamp
	private LocalDateTime account_updated;

}
