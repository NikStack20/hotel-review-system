package com.rating.api.configuration;
import org.modelmapper.ModelMapper;  
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RatingConfig {

	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
