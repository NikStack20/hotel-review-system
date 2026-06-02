package com.User.Service.loadouts;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
