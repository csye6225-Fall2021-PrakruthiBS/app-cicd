package com.example.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.example.CreateUserRequest.CreateUserRequest;
import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Getter
//@Setter
//@NoArgsConstructor
@Entity
@Table(name = "user_entity")
public class UserEntity {
	
	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	private long Id;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "user_name", unique = true)
	@NotBlank (message = "username is required")
	@NotNull(message = "username is required")
	private String userName;
	
	@Column(name = "password")
	private String password;

	
	@Column
	@CreationTimestamp
	private LocalDateTime account_created;
	
	@Column
	@CreationTimestamp
	private LocalDateTime account_updated;
	
	private String[] userRoles;
	
	public UserEntity() {};
	
	public UserEntity(CreateUserRequest createUserRequest) {
		//System.out.println("UserName: " + createUserRequest.getUser_name());
		this.firstName = createUserRequest.getFirst_name();
		this.lastName = createUserRequest.getLast_name();
		this.userName = createUserRequest.getUser_name();
		this.password = createUserRequest.getPassword();
		this.account_created = createUserRequest.getAccountCreated();
		this.account_updated = createUserRequest.getAccountUpdated();
	}
	
	public String[] getuserRoles() {
        return userRoles;
    }

    public void setuserRoles(String[] userRoles) {
        this.userRoles = userRoles;
    }

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDateTime getAccount_created() {
		return account_created;
	}

	public void setAccount_created(LocalDateTime account_created) {
		this.account_created = account_created;
	}

	public LocalDateTime getAccount_updated() {
		return account_updated;
	}

	public void setAccount_updated(LocalDateTime account_updated) {
		this.account_updated = account_updated;
	}

	public String[] getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(String[] userRoles) {
		this.userRoles = userRoles;
	}
    


}
