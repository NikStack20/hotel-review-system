package com.org.Hotel.Service.entities;
import java.util.List; 
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hotels")
public class Hotel {

	@Id
	@Column(name = "hotel_id")
	private String hotelId;

	@Column(name = "hotel_names", nullable = false)
	private String name;

	@Column(name = "longitude/latitude", nullable = false)
	private String location;

	@Column(name = "About", nullable = false)
	private String about;
	
	   @ElementCollection
	    @CollectionTable(
	    		   name = "hotel_facilities",                  // child table ka naam
	    	        joinColumns = @JoinColumn(
	    	            name = "hotel_id",                      // child table column
	    	            referencedColumnName = "hotel_id"       // parent table column
	    	        )
	    	    )
	    	    @Column(name = "facility")                      // child table ke andar value ka naam
    private List<String> facility;
}
