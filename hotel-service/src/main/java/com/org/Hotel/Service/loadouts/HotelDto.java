package com.org.Hotel.Service.loadouts;
import java.util.List; 
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDto {

	
	private String hotelId;
	
	@NotEmpty
	private String name;
	
	@NotEmpty
	private String location;
	
	@NotEmpty
	private String about;
	
	@NotEmpty
	private List<String> facility;
}
