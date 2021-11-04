package com.example.response;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.example.entity.Image;
import com.example.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude
public class ImageResponse {
	
	@JsonProperty("image_id")
	private long image_id;
	@JsonProperty("file_name")
	private String file_name;
	@JsonProperty("url")
	private String url;
	@JsonProperty("uploaded_date")
	private String uploaded_date;
	@JsonProperty("user_id")
	private long user_id;
	
	public ImageResponse(Image image) {
		this.image_id = image.getId();
		this.file_name = image.getFileName();
		this.url = image.getUrl();
		this.uploaded_date = image.getUploaded_date();
		this.user_id = image.getUserEntity().getId();
	}
	
	

}
