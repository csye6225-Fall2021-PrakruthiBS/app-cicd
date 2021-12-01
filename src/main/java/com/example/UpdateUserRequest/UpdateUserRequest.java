package com.example.UpdateUserRequest;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

//@Getter
//@Setter
public class UpdateUserRequest {
	
	private Long id;
	
	@NotBlank(message = "Firstname is required")
	@NotNull(message = "Firstname is required")
	private String first_name;
	
	@NotNull(message = "Lastname is required")
	@NotBlank(message = "Lastname is required")
	private String last_name;
	
	@NotNull(message = "password is required")
	@NotBlank(message = "Password field is required")
	private String password;
	
	@NotNull(message = "Username is required")
	@NotBlank(message = "Username/email address is required")
	private String user_name;
	
	//@JsonIgnore
	//@CreationTimestamp
	private LocalDateTime account_updated;
	
    private Boolean isVerified;

	
	private String verified_on;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public LocalDateTime getAccount_updated() {
		return account_updated;
	}

	public void setAccount_updated(LocalDateTime account_updated) {
		this.account_updated = account_updated;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getVerified_on() {
		return verified_on;
	}

	public void setVerified_on(String verified_on) {
		this.verified_on = verified_on;
	}
	
	

}
