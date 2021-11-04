package com.example.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.example.response.ImageResponse;
import com.example.response.UserResponse;
import com.example.service.UserService;

import antlr.collections.List;

import com.example.CreateUserRequest.CreateUserRequest;
import com.example.CreateUserRequest.LoadUserRequest;
import com.example.UpdateUserRequest.ResetPasswordRequest;
import com.example.UpdateUserRequest.UpdateUserRequest;
import com.example.entity.Image;
import com.example.entity.UserEntity;

@RestController
//@EnableWebMvc
@RequestMapping("/v1/user/")
public class UserController {

	@Autowired
	UserService userService;
	public static String registrationSuccess = "User registered successfully";
	
	
	@GetMapping(value = "self",produces = MediaType.APPLICATION_JSON_VALUE)
	 public  UserResponse authenticate(HttpServletRequest request){
		String authorization=request.getHeader("authorization");
		    String credentials=new String(Base64.decodeBase64(authorization.substring(6)));
		    String userName=credentials.split(":")[0];
		    String password=credentials.split(":")[1]; 
		    System.out.println("Decoded username: " + userName);
		    System.out.println("Decoded password: " + password);
		    UserEntity usr = userService.getUserInfo(userName,password);
		    return new UserResponse(usr);

		
	}
	
//	@GetMapping(value = "self", produces = "MediaType.APPLICATION_JSON_VALUE")
//	 public ResponseEntity<Object>  authenticate(HttpServletRequest request){
//		String authorization=request.getHeader("authorization");
//		    String credentials=new String(Base64.decodeBase64(authorization.substring(6)));
//		    String userName=credentials.split(":")[0];
//		    String password=credentials.split(":")[1]; 
//		    System.out.println("Decoded username: " + userName);
//		    System.out.println("Decoded password: " + password);
//		    UserEntity usr = userService.getUserInfo(userName,password);
//		    //return new UserResponse(usr);
//		    return usr;
//		    //return null;
//		
//	}
	



	@PostMapping("createUser")
	public UserResponse createUser(@RequestBody CreateUserRequest createUserRequest) {
		//System.out.println("Create User Request: " + createUserRequest.getUser_name());
		//UserEntity userEntity = userService.findUserByUserName(user.getUserName());
     
				UserEntity usr = userService.createUser(createUserRequest);
				return new UserResponse(usr);
				//return null;
			
	}
	
	
	@PutMapping("self")
		public UserResponse updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest, HttpServletRequest httpRequest) {
		String authorization=httpRequest.getHeader("authorization");
	    String credentials=new String(Base64.decodeBase64(authorization.substring(6)));
	    String username=credentials.split(":")[0];
	    String pwd=credentials.split(":")[1]; 
		System.out.println("Checking");
		UserEntity user = userService.updateUser(updateUserRequest, username, pwd);
		return new UserResponse(user);
	}
	
	/////////////////IMAGE PART/////////////////////////////////
	
	

	@GetMapping(value = "self/pic",produces = MediaType.APPLICATION_JSON_VALUE)
	 public ImageResponse getUser(HttpServletRequest request){
		String authorization=request.getHeader("authorization");
		    String credentials=new String(Base64.decodeBase64(authorization.substring(6)));
		    String userName=credentials.split(":")[0];
		    String password=credentials.split(":")[1]; 
		    System.out.println("Decoded username: " + userName);
		    System.out.println("Decoded password: " + password);
		    Image image = userService.getUserImage(userName,password);	
		    return new ImageResponse(image);
	}
		
	
	@PostMapping @RequestMapping(value = "self/pic", consumes = MediaType.IMAGE_JPEG_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
	public ImageResponse uploadFile(@RequestBody byte[] file, HttpServletRequest request) throws Exception {
		String authorization=request.getHeader("authorization");
	    String credentials=new String(Base64.decodeBase64(authorization.substring(6)));
	    String userName=credentials.split(":")[0];
	    String password=credentials.split(":")[1]; 
	    System.out.println("Decoded username: " + userName);
	    System.out.println("Decoded password: " + password);
	    Image image = userService.uploadFile(userName, password,file);
	    return new ImageResponse(image);
	}
	
	@DeleteMapping(value = "self/pic")
	public String  deleteFile(HttpServletRequest request) throws Exception {
		 System.out.println("In delete mapping");
		String authorization=request.getHeader("authorization");
	    String credentials=new String(Base64.decodeBase64(authorization.substring(6)));
	    String userName=credentials.split(":")[0];
	    String password=credentials.split(":")[1]; 
	    System.out.println("Decoded username: " + userName);
	    System.out.println("Decoded password: " + password);
	return this.userService.deleteFileFromS3Bucket(userName,password);
	}
}
	 

