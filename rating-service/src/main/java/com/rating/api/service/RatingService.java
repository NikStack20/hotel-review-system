package com.rating.api.service;

import java.util.List;

import com.rating.api.loadouts.RatingDto;

public interface RatingService {

	// create
	RatingDto create(RatingDto ratingDto);

	// get all ratings
	List<RatingDto> getAllRatings();

	// get all by UserId
	List<RatingDto> getRatingByUserId(String userId);

	// get all by hotel
	List<RatingDto> getRatingByHotelId(String hotelId);


}
