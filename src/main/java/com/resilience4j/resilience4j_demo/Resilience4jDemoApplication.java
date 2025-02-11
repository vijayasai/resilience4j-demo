package com.resilience4j.resilience4j_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Resilience4jDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(Resilience4jDemoApplication.class, args);
	}

}
