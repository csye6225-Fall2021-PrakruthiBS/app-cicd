package com.example.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.CreateUserRequest.CreateUserRequest;
import com.example.CreateUserRequest.LoadUserRequest;
import com.example.UpdateUserRequest.ResetPasswordRequest;
import com.example.UpdateUserRequest.UpdateUserRequest;
import com.example.entity.Image;
import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import com.example.repository.ImageRepository;

@Service
public class UserService implements UserDetailsService{
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	ImageRepository imageRepository;
	//private UserService userService;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
	 LocalDateTime now = LocalDateTime.now();
	
	 private AmazonS3 s3client;
//	 @Value("${s3.endpointUrl}")
//	 private String endpointUrl;
	 @Value("${s3.bucketName}")
	 private String bucketName;
	 @Value("${s3.accessKeyId}")
	 private String accessKeyId;
	 @Value("${s3.secretKey}")
	 private String secretKey;
	 @Value("${s3.region}")
	 private String region;
	
	 @PostConstruct
	 private void initializeAmazon() {
		  AWSCredentials credentials = new BasicAWSCredentials(this.accessKeyId, this.secretKey);
		  System.out.println("credentials: " + credentials);
		  System.out.println("region: " + region);
		  this.s3client = AmazonS3ClientBuilder.standard().withRegion(region).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		}
	
	 @Autowired
	 public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
	        this.userRepository = userRepository;
	        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	    }
	
	public @ResponseBody UserEntity getUserInfo(String usernName, String password) {
		UserEntity user = userRepository.findByUserName(usernName);
		if(usernName.equalsIgnoreCase(user.getUserName()) && bCryptPasswordEncoder.matches(password, user.getPassword())) {
		return userRepository.findByUserName(usernName);
		}else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username does not exists/ username/password is wrong");
		}
		
	}
		
	public UserEntity createUser (CreateUserRequest createUserRequest) {
		UserEntity usr = new UserEntity(createUserRequest);
		UserEntity userExists = userRepository.findByUserName(usr.getUserName());
		System.out.println("username: " + usr.getUserName() + " usrpassword: " + usr.getPassword());
		//System.out.println("username: " + userExists.getUserName() + " usrpassword: " + userExists.getPassword());
			if(userExists!=null)
			{
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username already exists");}
			else {
		
		//System.out.println("Service Username: " + userExists.getUserName()+ "Password: " + userExists.getPassword());
			usr.setPassword(bCryptPasswordEncoder.encode(usr.getPassword()));
			usr = userRepository.save(usr);
			return usr;
			}
		
	}

	 public UserEntity findUserByUserName(String username) {
	        return userRepository.findByUserName(username);
	    }
	 
	 
	public UserEntity updateUser(UpdateUserRequest updateUserRequest, String username, String password) {

			UserEntity user = userRepository.findByUserName(username);
		if(userRepository.findByUserName(username) == null) {
		
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username does not exists");
		}
		else 
			if(username.equalsIgnoreCase(user.getUserName()) && bCryptPasswordEncoder.matches(password, user.getPassword())) {
			user.setFirstName(updateUserRequest.getFirst_name());
			user.setLastName(updateUserRequest.getLast_name());
			user.setPassword(updateUserRequest.getPassword());
			user.setAccount_updated(now);
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		return user;
			
		}
		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect username/password");
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user =  userRepository.findByUserName(username);
        if (user == null){
            throw new UsernameNotFoundException(username + " was not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(),user.getPassword(), AuthorityUtils.createAuthorityList(user.getuserRoles()));
                

	}
	
	//////////////////IMAGE PART//////////////////////////////////////
	
	private File convertMultiPartToFile(MultipartFile file) 
			throws IOException {
			  File convFile = new File(file.getOriginalFilename());
			  FileOutputStream fos = new FileOutputStream(convFile);
			  fos.write(file.getBytes());
			  fos.close();
			  return convFile;
			}
	
	private String generateFileName(MultipartFile multiPart) {
		 return new Date().getTime() + "-" +   multiPart.getOriginalFilename().replace(" ", "_");
		}
	

	public @ResponseBody Image getUserImage(String usernName, String password) {
		UserEntity user = userRepository.findByUserName(usernName);
		if(usernName.equalsIgnoreCase(user.getUserName()) && bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return imageRepository.getByUserId(user.getId());
		}else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username does not exists/ username/password is wrong");
		}
		
	}

	private void uploadFileTos3bucket(String fileName, File file) {
		  this.s3client.putObject(bucketName, fileName, file);
		}
	
		public Image uploadFile(String usernName, String password, byte[] file_input) throws Exception {
			UserEntity user = userRepository.findByUserName(usernName);
			if (usernName.equalsIgnoreCase(user.getUserName())&& bCryptPasswordEncoder.matches(password, user.getPassword())) {
				Image image = new Image();
				//Image image_exist = imageRepository.findById(user.getId());
				String fileUrl = "";
				String fileName = generateFileName();
				String today = LocalDate.now().toString();
				fileUrl = bucketName + "/" + fileName;
				File image_file = new File(fileName);
				FileOutputStream fos = new FileOutputStream(image_file);
				fos.write(file_input);
				fos.close();
				UserEntity user1 = userRepository.findByUserName(usernName);
				System.out.println("user1: " + user1.getId());
				
				
				//System.out.println("image_exist: " + image_exist.getId());

				if (imageRepository.getByUserId(user1.getId()) != null) {
					System.out.println(user.getId());
					Image image_exist = imageRepository.getByUserId(user1.getId());
					System.out.println("PUT");
					System.out.println("filename: " + image_exist.getFileName());
					s3client.deleteObject(bucketName, image_exist.getFileName());
					uploadFileTos3bucket(fileName, image_file);
					image_file.delete();
					image.setId(image_exist.getId());
					image.setUserEntity(user);
					image.setUrl(fileUrl);
					image.setFileName(fileName);
					image.setUploaded_date(today);
					imageRepository.save(image);
					
					
				} 
				else {
					System.out.println(user.getId());
					System.out.println("POST");
					image.setUrl(fileUrl);
					image.setFileName(fileName);
					image.setUploaded_date(today);
					image.setUserEntity(user);
					imageRepository.save(image);
					uploadFileTos3bucket(fileName, image_file);
					image_file.delete();
				}
				return image;
			} else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"User with given username does not exists/ username/password is wrong");
			}
		}

	
	private String generateFileName() {
        return new Date().getTime() + "image.jpeg";
    }
	
	public String deleteFileFromS3Bucket(String usernName, String password) {
		UserEntity user = userRepository.findByUserName(usernName);
		if(usernName.equalsIgnoreCase(user.getUserName()) && bCryptPasswordEncoder.matches(password, user.getPassword())) {
			Image image_exist = imageRepository.getByUserId(user.getId());
			System.out.println(image_exist);
		  String fileName = image_exist.getFileName();
		  System.out.println("filename: " + fileName);
		  s3client.deleteObject(bucketName, fileName);
		  imageRepository.deleteByUserId(user.getId());
		  return "successfully deleted";
		}else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username does not exists/ username/password is wrong");
		}
		}
	
	

}
