package com.User.Service.servicesImpl;

import com.User.Service.Configurations.HotelServiceClient;
import com.User.Service.Configurations.RatingClient;
import com.User.Service.GlobalExceptionHandler.DBExceptions;
import com.User.Service.UserRepos.UserRepository;
import com.User.Service.entities.User;
import com.User.Service.loadouts.HotelDto;
import com.User.Service.loadouts.RatingDto;
import com.User.Service.loadouts.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class UserResilienceService {

    int retryCount = 1;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private HotelServiceClient hotelServiceClient;
    @Autowired
    private RatingClient ratingClient; // OpenFeign Bean Injection
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    // Configuring resilence4j for this controller with fallbackMethod

    @Retry(name = "ratingHotelService", fallbackMethod = "ratingHotelFallback")
    @CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelFallback")
    public UserDto getUserWithResilience(String userId) {

        logger.info("Retry count: {}", retryCount);
        retryCount++;

        // Fetch user from DB
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new DBExceptions("User with given userId: " + userId + " not found"));

        // 1st SERVICE CALL to RATING SERVICE
        List<RatingDto> ratings;
        ratings = ratingClient.getRatings(userId);

        if (ratings == null || ratings.isEmpty()) {
            ratings = Collections.emptyList();
        }

        logger.info("Ratings fetched for user {} : {}", userId, ratings.size());

        // 2nd SERVICE CALL to HOTEL SERVICE
        Set<String> hotelIds = ratings.stream().map(RatingDto::getHotelId).filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, HotelDto> hotelMap = hotelServiceClient.fetchHotelsForIds(hotelIds);

        ratings.forEach(rating -> rating.setHotel(hotelMap.get(rating.getHotelId())));

        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setRatings(ratings);

        return userDto;
    }

    public UserDto ratingHotelFallback(String userId, Throwable ex) {

        logger.error("========== FALLBACK TRIGGERED ==========");
        logger.error("Exception Type : {}", ex.getClass().getName());
        logger.error("Exception Msg  : {}", ex.getMessage(), ex);

        User user = User.builder()
                .email("xyz123@gmail.com")
                .name("John Doe")
                .about("Some services are down, fallback response")
                .userId("fallback-id")
                .build();

        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setRatings(Collections.emptyList());

        return userDto;
    }
}
