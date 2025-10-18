package com.morago_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.morago_backend.entity")
public class MoragoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoragoBackendApplication.class, args);
	}

}
