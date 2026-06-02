package com.User.Service.loadouts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {

    private String ratingId;
    private String hotelId;
    private int rating;
    private String feedback;
    private HotelDto hotel;

}
