package com.rating.api;

import org.springframework.boot.SpringApplication; 
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EntityScan("com.rating.api.docs")
@EnableMongoRepositories("com.rating.api.repository")
@SpringBootApplication(scanBasePackages = "com.rating.api")
public class RatingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RatingServiceApplication.class, args);
	}

}
