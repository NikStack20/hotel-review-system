package com.User.Service.Configurations;
import com.User.Service.loadouts.HotelDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.Set;

@FeignClient(name = "HOTEL-SERVICE")
public interface HotelClient {


    @GetMapping("/hotels/hotelsInBulk/{hotelIds}")
    Map<String, HotelDto> getHotels(@PathVariable Set<String> hotelIds);

}
