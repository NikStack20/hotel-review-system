package com.org.Hotel.Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EntityScan("com.org.Hotel.Service.entities")
//@EnableJpaRepositories("com.org.Hotel.Service.Repository")
@SpringBootApplication(scanBasePackages = "com.org.Hotel")
public class HotelServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelServiceApplication.class, args);

	}

}
