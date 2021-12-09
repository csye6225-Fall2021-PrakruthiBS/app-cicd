package com.example.Configuration;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.io.*;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.entity.Image;
import com.example.entity.UserEntity;

@Component
public class SecurityConfig {

	@Value("${spring1.datasource.url}")
	private static String main_url;
	@Value("${spring1.datasource.username}")
	private static String username;
	@Value("${spring1.datasource.password}")
	private static String password;

	@Value("${spring2.datasource.url}")
	private static String replica_url;

	static {
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

				keystore.setCertificateEntry("aws_rds_certificate", certificate);
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
		final Configuration cfg = new Configuration();
		String connection_url = main_url
				+ "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
		cfg.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		cfg.setProperty("sslMode", "VERIFY_IDENTITY");
		cfg.setProperty("hibernate.connection.url", connection_url);
		cfg.setProperty("hibernate.connection.username", username);
		cfg.setProperty("hibernate.connection.password", password);
		cfg.setProperty("hibernate.hbm2ddl.auto", "update");
		cfg.setProperty("hibernate.show_sql", "true");
		cfg.addAnnotatedClass(Image.class);
		cfg.addAnnotatedClass(UserEntity.class);
		// sessionFactory = cfg.buildSessionFactory();
	}

	static {
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

				keystore.setCertificateEntry("aws_rds_certificate", certificate);
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

		final Configuration cfgReplica = new Configuration();
		String connection_url = replica_url
				+ "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		cfgReplica.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
		cfgReplica.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		cfgReplica.setProperty("sslMode", "VERIFY_IDENTITY");
		cfgReplica.setProperty("hibernate.connection.url", connection_url);
		cfgReplica.setProperty("hibernate.connection.username", username);
		cfgReplica.setProperty("hibernate.connection.password", password);
		cfgReplica.setProperty("hibernate.show_sql", "true");
		cfgReplica.addAnnotatedClass(Image.class);
		cfgReplica.addAnnotatedClass(UserEntity.class);
		// sessionFactoryReplica = cfgReplica.buildSessionFactory();
	}

}
