package com.rating.api.serviceImpl;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rating.api.docs.Rating;
import com.rating.api.loadouts.RatingDto;
import com.rating.api.repository.RatingRepo;
import com.rating.api.service.RatingService;

@Service
public class RatingServiceImpl implements RatingService {
	
	@Autowired
	private RatingRepo ratingRepo;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public RatingDto create(RatingDto ratingDto) {
		Rating rating = this.modelMapper.map(ratingDto, Rating.class);
		Rating saved = this.ratingRepo.save(rating);

		return this.modelMapper.map(saved, RatingDto.class);
	}

	@Override
	public List<RatingDto> getRating() {
		List<Rating> saved = this.ratingRepo.findAll();
		List<RatingDto> dto = saved.stream().map((rating) -> this.modelMapper.map(rating, RatingDto.class))
				.collect(Collectors.toList());
		return dto;

	}

	@Override
	public List<RatingDto> getRatingByUserId(String userId) {
	    List<Rating> ratings = this.ratingRepo.findByUserId(userId);
	    List<RatingDto> result = ratings.stream().map(rating -> modelMapper.map(rating, RatingDto.class)).collect(Collectors.toList());
	    return result;
	}

	@Override
	public List<RatingDto> getRatingByHotelId(String hotelId) {
	    // 1. Fetch ratings from DB (source of truth)
	    List<Rating> ratings = ratingRepo.findByHotelId(hotelId);
	    List<RatingDto> result = ratings.stream().map(rating -> modelMapper.map(rating, RatingDto.class)).collect(Collectors.toList());
	    return result;
	}



}
