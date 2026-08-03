package com.org.Hotel.Service.Controller;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.org.Hotel.Service.loadouts.HotelDto;
import com.org.Hotel.Service.service.HotelService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/hotels")
public class HotelController {
	
	@Autowired
	private HotelService hotelService;
	
	
	   //create
	@PostMapping("/create")
	ResponseEntity<HotelDto> createHotel(@Valid @RequestBody HotelDto hotelDto) {
	      return new ResponseEntity<HotelDto>(this.hotelService.createHotel(hotelDto), HttpStatus.CREATED) ;
	}

	
	//get single
	@GetMapping("/getHotel/{hotelId}")
	ResponseEntity<HotelDto> getHotel(@PathVariable String hotelId) {
		return new ResponseEntity<HotelDto> (this.hotelService.getHotel(hotelId), HttpStatus.OK);
	}
	
	//get single
		@GetMapping("/getHotelByUserId/{userId}")
		ResponseEntity<HotelDto> getHotelByUserId(@PathVariable String userId) {
			return new ResponseEntity<HotelDto> (this.hotelService.getHotel(userId), HttpStatus.OK);
		}

    //get hotels by userId
    @GetMapping("/hotelsInBulk/{hotelIds}")
    ResponseEntity<Map<String, HotelDto>> getHotelsByHotelIds(@PathVariable Set<String> hotelIds) {
        return new ResponseEntity<Map<String, HotelDto>>(this.hotelService.getHotelsByHotelIds(hotelIds), HttpStatus.OK);
    }


}
