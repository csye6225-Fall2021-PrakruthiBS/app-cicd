package com.example.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.timgroup.statsd.StatsDClient;

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
	@Autowired
    private StatsDClient statsDClient;
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);

	@GetMapping(value = "self", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserResponse authenticate(HttpServletRequest request) {
		statsDClient.incrementCounter("endpoint.v1.user.self.api.get");
		String authorization = request.getHeader("authorization");
		String credentials = new String(Base64.decodeBase64(authorization.substring(6)));
		String userName = credentials.split(":")[0];
		String password = credentials.split(":")[1];
		System.out.println("Decoded username: " + userName);
		System.out.println("Decoded password: " + password);
		long startTime =  System.currentTimeMillis();
		UserEntity usr = userService.getUserInfo(userName, password);
		long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        statsDClient.recordExecutionTime("GetUserAPITime",duration);
        logger.info("Time taken to get user data: "+duration);
		return new UserResponse(usr);

	}

	@PostMapping("createUser")
	public UserResponse createUser(@RequestBody CreateUserRequest createUserRequest) {
		statsDClient.incrementCounter("endpoint.v1.user.createUser.api.post");
		long startTime =  System.currentTimeMillis();
		UserEntity usr = userService.createUser(createUserRequest);
		long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        statsDClient.recordExecutionTime("CreateUserAPITime",duration);
        logger.info("Time taken to create new user: "+duration);
		return new UserResponse(usr);

	}

	@PutMapping("self")
	public UserResponse updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest,	HttpServletRequest httpRequest) {
		statsDClient.incrementCounter("endpoint.v1.user.self.api.put");
		String authorization = httpRequest.getHeader("authorization");
		String credentials = new String(Base64.decodeBase64(authorization.substring(6)));
		String username = credentials.split(":")[0];
		String pwd = credentials.split(":")[1];
		long startTime =  System.currentTimeMillis();
		UserEntity user = userService.updateUser(updateUserRequest, username, pwd);
		long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        statsDClient.recordExecutionTime("UpdateUserAPITime",duration);
        logger.info("Time taken to update user data: "+duration);
		return new UserResponse(user);
	}

	///////////////// IMAGE PART/////////////////////////////////

	@GetMapping(value = "self/pic", produces = MediaType.APPLICATION_JSON_VALUE)
	public ImageResponse getUser(HttpServletRequest request) {
		statsDClient.incrementCounter("endpoint.v1.user.self.pic.api.get");
		String authorization = request.getHeader("authorization");
		String credentials = new String(Base64.decodeBase64(authorization.substring(6)));
		String userName = credentials.split(":")[0];
		String password = credentials.split(":")[1];
		long startTime =  System.currentTimeMillis();
		Image image = userService.getUserImage(userName, password);
		long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        statsDClient.recordExecutionTime("GetUserImageAPITime",duration);
        logger.info("Time taken to get user image details: "+duration);
		return new ImageResponse(image);
	}

	@PostMapping
	@RequestMapping(value = "self/pic", consumes = MediaType.IMAGE_JPEG_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ImageResponse uploadFile(@RequestBody byte[] file, HttpServletRequest request) throws Exception {
		statsDClient.incrementCounter("endpoint.v1.user.self.pic.api.post");
		String authorization = request.getHeader("authorization");
		String credentials = new String(Base64.decodeBase64(authorization.substring(6)));
		String userName = credentials.split(":")[0];
		String password = credentials.split(":")[1];
		long startTime =  System.currentTimeMillis();
		Image image = userService.uploadFile(userName, password, file);
		long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        statsDClient.recordExecutionTime("PostUserImageAPITime",duration);
        logger.info("Time taken to upload/update user image details: "+duration);
		return new ImageResponse(image);
	}

	@DeleteMapping(value = "self/pic")
	public String deleteFile(HttpServletRequest request) throws Exception {
		statsDClient.incrementCounter("endpoint.v1.user.self.pic.api.delete");
		String authorization = request.getHeader("authorization");
		String credentials = new String(Base64.decodeBase64(authorization.substring(6)));
		String userName = credentials.split(":")[0];
		String password = credentials.split(":")[1];
		long startTime =  System.currentTimeMillis();
		String result = this.userService.deleteFileFromS3Bucket(userName, password);
		long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        statsDClient.recordExecutionTime("DeleteUserImageAPITime",duration);
        logger.info("Time taken to delete user image details: "+duration);
		return result;
	}
}
