package com.org.Hotel.Service.Configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HotelConfig {

	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
