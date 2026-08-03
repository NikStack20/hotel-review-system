package com.org.Hotel.Service.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.org.Hotel.Service.loadouts.HotelDto;

public interface HotelService {

	// create
	HotelDto createHotel(HotelDto hotelDto);

	// getSingleHotel
	HotelDto getHotel(String hotelId);

    // get hotels by userId
    Map<String, HotelDto> getHotelsByHotelIds(Set<String> hotelIds);

}
