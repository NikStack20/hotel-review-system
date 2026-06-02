package com.User.Service.Configurations;

import com.User.Service.loadouts.RatingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "RATING-SERVICE")
public interface RatingClient {
    @GetMapping("/ratings/getAllByUserId/{userId}")
    List<RatingDto> getRatings(@PathVariable String userId);

}
