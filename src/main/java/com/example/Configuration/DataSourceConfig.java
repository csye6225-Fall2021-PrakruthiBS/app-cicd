package com.example.Configuration;


import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {
	@Value("${spring1.datasource.url}")
    private String main_url;
	@Value("${spring1.datasource.username}")
    private String username;
	@Value("${spring1.datasource.password}")
    private String password;
	
	@Bean
	public AppRoutingDataSource dataSource() {
		AppRoutingDataSource multiTenantDataSource = new AppRoutingDataSource();
		
		multiTenantDataSource.setTargetDataSources(new ConcurrentHashMap<>());
		multiTenantDataSource.setDefaultTargetDataSource(defaultDataSource());
		multiTenantDataSource.afterPropertiesSet();
		
		return multiTenantDataSource;
	}
	
	
	private DriverManagerDataSource defaultDataSource() {
		DriverManagerDataSource defaultDataSource = new DriverManagerDataSource();
		defaultDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		defaultDataSource.setUrl(main_url);
		defaultDataSource.setUsername(username);
		defaultDataSource.setPassword(password);
		return defaultDataSource;
	}

}
