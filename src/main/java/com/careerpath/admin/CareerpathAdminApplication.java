package com.careerpath.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;


@SpringBootApplication
@EnableMethodSecurity
public class CareerpathAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(CareerpathAdminApplication.class, args);
	}

}
