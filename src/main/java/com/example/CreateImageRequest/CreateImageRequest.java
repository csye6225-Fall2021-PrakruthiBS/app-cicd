package com.example.CreateImageRequest;

import java.time.LocalDateTime;

import com.example.entity.UserEntity;

public class CreateImageRequest {


	private String fileName;
	private String url;
	private LocalDateTime uploaded_date;
	//private UserEntity userEntity;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public LocalDateTime getUploaded_date() {
		return uploaded_date;
	}
	public void setUploaded_date(LocalDateTime uploaded_date) {
		this.uploaded_date = uploaded_date;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
//	public UserEntity getUserEntity() {
//		return userEntity;
//	}
//	public void setUserEntity(UserEntity userEntity) {
//		this.userEntity = userEntity;
//	}
	

}
