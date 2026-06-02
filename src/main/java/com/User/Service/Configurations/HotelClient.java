package com.User.Service.Configurations;

import com.User.Service.loadouts.HotelDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "HOTEL-SERVICE")
public interface HotelClient {

    @GetMapping("/hotels/getHotel/{hotelId}")
    HotelDto getHotel(@PathVariable String hotelId);

}
