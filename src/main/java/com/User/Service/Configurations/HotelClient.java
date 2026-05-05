package com.User.Service.Configurations;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.User.Service.loadouts.HotelDto;

@FeignClient(name = "HOTEL-SERVICE")
public interface HotelClient {

	@GetMapping("/hotels/getHotel/{hotelId}")
	HotelDto getHotel(@PathVariable String hotelId);

}
