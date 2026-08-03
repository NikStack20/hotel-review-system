package com.User.Service.Configurations;

import com.User.Service.loadouts.HotelDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class HotelServiceClient {

    @Autowired
    private HotelClient hotelClient;

    private Logger logger = org.slf4j.LoggerFactory.getLogger(HotelServiceClient.class);

    @CircuitBreaker(name = "userHotelBreaker", fallbackMethod = "userHotelFallback")
    @Retry(name = "userHotelService", fallbackMethod = "userHotelFallback")
    @RateLimiter(name = "userRateLimiter", fallbackMethod = "userHotelFallback")
    public Map<String, HotelDto> fetchHotelsForIds(Set<String> hotelIds) {

        if (hotelIds == null || hotelIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return hotelClient.getHotels(hotelIds);
    }

    public Map<String, HotelDto> userHotelFallback(Set<String> hotelIds, Throwable ex) {

        logger.error("Fallback triggered for hotel service: {}", ex.getMessage());

        Map<String, HotelDto> fallbackMap = new HashMap<>();

        for (String hotelId : hotelIds) {
            HotelDto hotel = new HotelDto();
            hotel.setHotelId(hotelId);
            hotel.setName("Fallback Hotel");
            hotel.setLocation("Service Down");
            hotel.setAbout("Dummy fallback...");
            fallbackMap.put(hotelId, hotel);
        }

        return fallbackMap;
    }
}
