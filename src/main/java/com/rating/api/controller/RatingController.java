package com.rating.api.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rating.api.loadouts.RatingDto;
import com.rating.api.service.RatingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ratings")
public class RatingController {

	@Autowired
	private RatingService ratingService;

	// create
	@PostMapping("/create")
	ResponseEntity<RatingDto> createRating(@Valid @RequestBody RatingDto ratingDto) {
		return new ResponseEntity<RatingDto>(this.ratingService.create(ratingDto), HttpStatus.CREATED);
	}

	// getAll
	@GetMapping
	ResponseEntity<List<RatingDto>> getRating() {
		return new ResponseEntity<List<RatingDto>>(this.ratingService.getRating(), HttpStatus.OK);
	}

	// getAllByHotelId
	@GetMapping("/getAllByHotelId/{hotelId}")
	ResponseEntity<List<RatingDto>> getAllByHotelId(@PathVariable String hotelId) {
		return new ResponseEntity<List<RatingDto>>(this.ratingService.getRatingByHotelId(hotelId), HttpStatus.OK);
	}

	// getAllByUserId
	@GetMapping("/getAllByUserId/{userId}")
	ResponseEntity<List<RatingDto>> getAllByUserId(@PathVariable String userId) {
		return new ResponseEntity<List<RatingDto>>(this.ratingService.getRatingByUserId(userId), HttpStatus.OK);
	}

}
