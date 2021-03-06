package com.example.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
//@ComponentScan("com.example.*") 
@ComponentScan({"com.example.controller", "com.example.service", "com.example.Configuration"})
@EntityScan("com.example.entity")
@EnableJpaRepositories("com.example.repository")

public class WebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebappApplication.class, args);
	
	}
	
	 @Bean
		@Primary
		public BCryptPasswordEncoder getpce()
		{
			return new BCryptPasswordEncoder();
		}

}
