package com.example.Configuration;


import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.io.*;

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
		try {
		char[] keyStorePassword = "prakruthi".toCharArray();
		File keystorefile = new File("/home/ubuntu/keystore.jks");
		keystorefile.createNewFile();
		KeyStore keystore;
		try (FileInputStream storeInputStream = new FileInputStream("/home/ubuntu/keystore.jks");
				FileInputStream certInputStream = new FileInputStream("/home/ubuntu/rds-ca-2019-us-east-1.pem")) {
			keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(storeInputStream, keyStorePassword);

			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			Certificate certificate = certificateFactory.generateCertificate(certInputStream);

			keystore.setCertificateEntry("aws_rds_cert", certificate);
		} finally {
		}

		try (FileOutputStream storeOutputStream = new FileOutputStream("/home/ubuntu/keystore.jks")) {
			keystore.store(storeOutputStream, keyStorePassword);
		} finally {
		}
		System.setProperty("javax.net.ssl.trustStoreType", "JKS");
		System.setProperty("javax.net.ssl.trustStore", "/home/ubuntu/keystore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "prakruthi");
	} catch (CertificateException e) {
		e.printStackTrace();
	} catch (KeyStoreException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	}
	
	final DriverManagerDataSource defaultDataSource = new DriverManagerDataSource();
	Properties properties = new Properties();
    properties.setProperty("sslMode", "VERIFY_IDENTITY");
	String connection_url = main_url + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		defaultDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		defaultDataSource.setUrl(connection_url);
		defaultDataSource.setUsername(username);
		defaultDataSource.setPassword(password);
		defaultDataSource.setConnectionProperties(properties);
		return defaultDataSource;
	}

}
