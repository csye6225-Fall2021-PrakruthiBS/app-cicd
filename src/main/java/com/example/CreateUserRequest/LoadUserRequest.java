package com.example.CreateUserRequest;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadUserRequest {
	
	//@NotBlank (message = "First name can't be blank")
		private String first_name;
		
		//@NotBlank (message = "Last name can't be blank")
		private String last_name;
		
		//@NotNull (message = "username can't be null")
		private String user_name;
		
		private String password;
		
		
		private LocalDateTime accountCreated;
		
		
		private LocalDateTime accountUpdated;


}
