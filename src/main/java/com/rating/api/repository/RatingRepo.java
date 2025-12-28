package com.rating.api.repository;
import java.util.List;   
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.rating.api.docs.Rating;

@Repository
public interface RatingRepo extends MongoRepository<Rating, String> {
	// Rating (@Document)
	// custom finder methods
	List<Rating> findByUserId(String userId);

	// Repository didn'nt understand Dto's i.e. we're using Rating(@Document)
	List<Rating> findByHotelId(String hotelId);
	
	//HotelDto findByRatingId(String ratingId);
}
