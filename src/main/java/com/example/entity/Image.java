package com.example.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.example.CreateImageRequest.CreateImageRequest;
import com.example.CreateUserRequest.CreateUserRequest;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "image")
public class Image {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private long id;
	
	@Column(name = "file_name")
	private String fileName;
	
	@Column(name = "url")
	private String url;
	
	@Column
	//@CreationTimestamp
	private String uploaded_date;
	
	@OneToOne
	@JoinColumn(name="user_id")
	private UserEntity userEntity;
	
	

	public long getId() {
		return id;
	}

	public void setId(long l) {
		this.id = l;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	

	public UserEntity getUserEntity() {
		return userEntity;
	}

	public void setUserEntity(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	public String getUploaded_date() {
		return uploaded_date;
	}

//	public long getUser_id() {
//		return user_id;
//	}
//
//	public void setUser_id(long user_id) {
//		this.user_id = user_id;
//	}

	
	public void setUploaded_date(String today) {
		this.uploaded_date = today;
	}


//	public Image(CreateImageRequest createImageRequest) {
//		//System.out.println("UserName: " + createUserRequest.getUser_name());
//		this.fileName = createImageRequest.getFileName();
//		this.url = createImageRequest.getUrl();
//		//this.userEntity = createImageRequest.getUserEntity().;
//		this.uploaded_date = createImageRequest.getUploaded_date();
//	}
	
}
