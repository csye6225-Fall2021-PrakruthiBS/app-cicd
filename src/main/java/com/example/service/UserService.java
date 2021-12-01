package com.example.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.CreateUserRequest.CreateUserRequest;
import com.example.CreateUserRequest.LoadUserRequest;
import com.example.UpdateUserRequest.ResetPasswordRequest;
import com.example.UpdateUserRequest.UpdateUserRequest;
import com.example.entity.Image;
import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import com.timgroup.statsd.StatsDClient;
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
	 
	 private final static Logger logger = LoggerFactory.getLogger(UserService.class);
	 
	 @Autowired
	 private StatsDClient statsDClient;
	
	 private AmazonS3 s3client;
	 private static AmazonSQS sqs;
	 private static AmazonSNS sns;
	 static String myQUrl = "";
//	 @Value("${s3.endpointUrl}")
//	 private String endpointUrl;
	 @Value("${s3.bucketName}")
	 private String bucketName;
//	 @Value("${s3.accessKeyId}")
//	 private String accessKeyId;
//	 @Value("${s3.secretKey}")
//	 private String secretKey;
	 @Value("${s3.region}")
	 private String region;
	 @Value("${sns.arn}")
	 private String arn;
//	 @Value("${sqs.url}")
//	 private String sqsUrl;

	 @PostConstruct
	 private void initializeAmazon() {
		  //AWSCredentials credentials = new BasicAWSCredentials(this.accessKeyId, this.secretKey);
		  System.out.println("region: " + region);
		  this.s3client = AmazonS3ClientBuilder.standard().withRegion(region).withCredentials(new InstanceProfileCredentialsProvider(false)).build();
		}
	 
//	  
//	    private void initializeSQS() {
//	        this.sqs =  AmazonSQSClientBuilder.standard().withRegion(region).withCredentials(new InstanceProfileCredentialsProvider(false)).build();
//	    }
	    
	    private void initializeSNS() {
	        this.sns = AmazonSNSClientBuilder.standard()
	        		.withRegion(region)
	                .withCredentials(new InstanceProfileCredentialsProvider(false))
	                .build();
	    }
	
	 @Autowired
	 public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
	        this.userRepository = userRepository;
	        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	    }
	
	public @ResponseBody UserEntity getUserInfo(String usernName, String password) {
		UserEntity user = userRepository.findByUserName(usernName);
		if(user.getVerified() == true) {
		if(usernName.equalsIgnoreCase(user.getUserName()) && bCryptPasswordEncoder.matches(password, user.getPassword())) {
		long startTime =  System.currentTimeMillis();
		UserEntity usr = userRepository.findByUserName(usernName);
		long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        statsDClient.recordExecutionTime("dbQueryTimeGetUser",duration);
        logger.info("Get user data from the DB");
		return usr;
		}else {
			logger.error("User with given username does not exists/ username/password is wrong");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username does not exists/ username/password is wrong");
		}
		}
		else {
			logger.error("User is not verified");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not verified");
		}
		
	}
		
	public UserEntity createUser (CreateUserRequest createUserRequest) {
		UserEntity usr = new UserEntity(createUserRequest);
		UserEntity userExists = userRepository.findByUserName(usr.getUserName());
		System.out.println("username: " + usr.getUserName() + " usrpassword: " + usr.getPassword());
			if(userExists!=null)
			{
				logger.error("User with given username already exists");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username already exists");}
			else {
			usr.setVerified(false);
	        usr.setVerified_on(null);
			usr.setPassword(bCryptPasswordEncoder.encode(usr.getPassword()));
			long startTime = System.currentTimeMillis();
			usr = userRepository.save(usr);
			long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);
            statsDClient.recordExecutionTime("dbQueryTimeCreateUser", duration);
            logger.info("New user created in DB successfully");
            String verification_token = UUID.randomUUID().toString();
            long ttl = (System.currentTimeMillis()/1000)+120;
            String message = usr.getUserName()+":"+verification_token+":"+"initial_token";
            publishSNSMessage(message);
            AmazonDynamoDB dynamoclient = AmazonDynamoDBClientBuilder.standard().build();
            Map<String, AttributeValue> DynamoDBMap = new HashMap();
            DynamoDBMap.put("msg", new AttributeValue(message));
            DynamoDBMap.put("ttl", new AttributeValue(String.valueOf(ttl)));
            PutItemRequest request = new PutItemRequest();
            request.setTableName("csye6225");
            request.setItem(DynamoDBMap);
            PutItemResult result1 = dynamoclient.putItem(request);
			return usr;
			}
		
	}

	 public UserEntity findUserByUserName(String username) {
	        return userRepository.findByUserName(username);
	    }
	 
	 
	public UserEntity updateUser(UpdateUserRequest updateUserRequest, String username, String password) {

			UserEntity user = userRepository.findByUserName(username);
		if(userRepository.findByUserName(username) == null) {
			logger.error("User with given username does not exists");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username does not exists");
		}
		else 
			if(user.getVerified() == true) {
			if(username.equalsIgnoreCase(user.getUserName()) && bCryptPasswordEncoder.matches(password, user.getPassword())) {
			user.setFirstName(updateUserRequest.getFirst_name());
			user.setLastName(updateUserRequest.getLast_name());
			user.setPassword(updateUserRequest.getPassword());
			user.setAccount_updated(now);
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			long startTime =  System.currentTimeMillis();
			userRepository.save(user);
			long endTime = System.currentTimeMillis();
	        long duration = (endTime - startTime);
	        statsDClient.recordExecutionTime("dbQueryTimeUpdateUser",duration);
	        logger.info("User data updated in DB successfully");
			return user;
			
		}
		else {
			logger.error("Incorrect username/password");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect username/password");
		}
			}
			else {
				logger.error("User is not verified");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not verified");
			}
	}
	
	public UserEntity updateUserVerification(String username) {

		UserEntity user = userRepository.findByUserName(username);

		user.setVerified(true);
		user.setVerified_on(LocalDateTime.now().toString());
		long startTime =  System.currentTimeMillis();
		userRepository.save(user);
		long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        statsDClient.recordExecutionTime("dbQueryTimeUpdateUser",duration);
        logger.info("User verification updated in DB successfully");
		return user;
}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user =  userRepository.findByUserName(username);
        if (user == null){
            throw new UsernameNotFoundException(username + " was not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(),user.getPassword(), AuthorityUtils.createAuthorityList(user.getuserRoles()));
                

	}
	
	//////////////////Amazon SNS Lambda///////////////////////////////
	
//    public void sendMessage(String email,String token, String msgtype, String ttl) {
//        initializeSQS();
//        String message = email+":"+token+":"+msgtype+":"+ttl;
//        for (final String queueUrl : sqs.listQueues().getQueueUrls()) {
//            if(queueUrl.equals(sqsUrl)) {
//            	myQUrl = queueUrl;
//            }
//        }
//        sqs.sendMessage(new SendMessageRequest(myQUrl,message));
//     }
	
    public void publishSNSMessage(String message) {
        initializeSNS();

        System.out.println("Publishing SNS message: " + message);

        PublishResult result = this.sns.publish(arn, message);

        System.out.println("SNS Message ID: " + result.getMessageId());
    }
    
    public ResponseEntity<Object> verifyUser (String username, String token, String ttl){

        logger.info("Username from link: "+username);
        logger.info("token from link: "+token);
        Map<String, Object> map = new HashMap<String, Object>();
        UserEntity u1 = userRepository.findByUserName(username);
        AmazonDynamoDB dynamoclient = AmazonDynamoDBClientBuilder.standard().build();
        GetItemRequest req = new GetItemRequest();
        req.setTableName("csye6225");
        req.setConsistentRead(true);
        String msg = username+"::"+token+"::"+ttl+"::initial_token";
        Map<String, AttributeValue> DynamoMap = new HashMap();
        DynamoMap.put("msg", new AttributeValue(msg));
        req.setKey(DynamoMap);
        logger.info("msg: "+msg);
        GetItemResult result = dynamoclient.getItem(req);
        logger.info("from DynamoDB "+result.toString());
        logger.info("Item from DynamoDB "+result.getItem());
        logger.info("msg from DynamoDB "+result.getItem().get("msg"));
        logger.info("ttl from DynamoDB "+result.getItem().get("ttl"));
        String t[] = result.toString().split("::");
        logger.info("t[0] "+t[0]);
        logger.info("t[1] "+t[1]);
        logger.info("t[2] "+t[2]);
      //  long dynamottl = Long.parseLong(result.getItem().get("ttl").toString());
        long now = System.currentTimeMillis()/1000;
        logger.info("now "+ now);
        if ((result.getItem() != null) && (Long.parseLong(t[2]) > now)){
            if (t[1].equals(token)){
                logger.info("token from DB "+t[1]);
                updateUserVerification(username);
                return new ResponseEntity<Object>(HttpStatus.OK);
            }
            else {map.put("Message", "Invalid Link.");
                logger.info("token mismatch");
                return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);}
        }
        else {map.put("Message", "Invalid Link.");
            logger.info("Not found in DynamoDB");
            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);}
    }
    }
    
    
	//////////////////IMAGE PART//////////////////////////////////////
	
	private File convertMultiPartToFile(MultipartFile file)throws IOException {
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
		if (user.getVerified() == true) {
		if(usernName.equalsIgnoreCase(user.getUserName()) && bCryptPasswordEncoder.matches(password, user.getPassword())) {
			long startTime =  System.currentTimeMillis();
			Image image = imageRepository.getByUserId(user.getId());
			long endTime = System.currentTimeMillis();
	        long duration = (endTime - startTime);
	        statsDClient.recordExecutionTime("dbQueryTimeGetImageData",duration);
	        logger.info("Getting user image metadat from DB");
	        return image;
		}else {
			logger.error("User with given username does not exists/ username/password is wrong");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username does not exists/ username/password is wrong");
		}
		}
		else {
			logger.error("User is not verified");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not verified");
		}
		
	}

	private void uploadFileTos3bucket(String fileName, File file) {
		  this.s3client.putObject(bucketName, fileName, file);
		}
	
		public Image uploadFile(String usernName, String password, byte[] file_input) throws Exception {
			UserEntity user = userRepository.findByUserName(usernName);
			if (user.getVerified() == true) {
			if (usernName.equalsIgnoreCase(user.getUserName())&& bCryptPasswordEncoder.matches(password, user.getPassword())) {
				Image image = new Image();
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

				if (imageRepository.getByUserId(user1.getId()) != null) {
					Image imageExists = imageRepository.getByUserId(user1.getId());
					s3client.deleteObject(bucketName, imageExists.getFileName());
					//uploadFileTos3bucket(fileName, image_file);
					long startTime =  System.currentTimeMillis();
					s3client.putObject(bucketName, fileName, image_file);
			        long endTime = System.currentTimeMillis();
			        long duration = (endTime - startTime);
			        statsDClient.recordExecutionTime("PutProfileimageToS3",duration);
			        logger.info("User image updated in S3 bucket successfully");
					image_file.delete();
					image.setId(imageExists.getId());
					image.setUserEntity(user);
					image.setUrl(fileUrl);
					image.setFileName(fileName);
					image.setUploaded_date(today);
					imageRepository.save(image);
				} 
				else {
					image.setUrl(fileUrl);
					image.setFileName(fileName);
					image.setUploaded_date(today);
					image.setUserEntity(user);
					imageRepository.save(image);
					//uploadFileTos3bucket(fileName, image_file);
					long startTime =  System.currentTimeMillis();
					s3client.putObject(bucketName, fileName, image_file);
					long endTime = System.currentTimeMillis();
			        long duration = (endTime - startTime);
			        statsDClient.recordExecutionTime("PostProfileimageToS3",duration);
			        logger.info("User image uploaded to S3 bucket successfully");
					image_file.delete();
				}
				return image;
			} else {
				logger.error("User with given username does not exists/ username/password is wrong");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username does not exists/ username/password is wrong");
			}
			}
			else {
				logger.error("User is not verified");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not verified");
			}
		}

	
	private String generateFileName() {
        return new Date().getTime() + "image.jpeg";
    }
	
	public String deleteFileFromS3Bucket(String usernName, String password) {
		UserEntity user = userRepository.findByUserName(usernName);
		if (user.getVerified() == true) {
		if(usernName.equalsIgnoreCase(user.getUserName()) && bCryptPasswordEncoder.matches(password, user.getPassword())) {
		  Image imageExists = imageRepository.getByUserId(user.getId());
		  String fileName = imageExists.getFileName();
		  System.out.println("filename: " + fileName);
		  long startTime =  System.currentTimeMillis();
		  s3client.deleteObject(bucketName, fileName);
		  imageRepository.deleteByUserId(user.getId());
		  long endTime = System.currentTimeMillis();
	      long duration = (endTime - startTime);
	      statsDClient.recordExecutionTime("DeleteProfileimageFromS3",duration);
	      logger.info("User image deleted from S3 bucket successfully");
		  return "successfully deleted";
		}else {
			logger.error("User with given username does not exists/ username/password is wrong");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with given username does not exists/ username/password is wrong");
		}
		}
	
		else {
			logger.error("User is not verified");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not verified");
		}
	}

}
