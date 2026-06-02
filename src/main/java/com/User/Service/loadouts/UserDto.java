package com.User.Service.loadouts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {

    private String userId;

    @NotEmpty(message = "Name can'nt be blank")
    @Size(min = 4, message = "must be atleast with 4 letters")
    private String name;

    @Email
    @NotEmpty(message = "email can'nt be blank")
    @Size(min = 4, message = "write a valid email starting with @ ")
    private String email;

    @NotEmpty(message = "About can'nt be blank")
    @Size(min = 10, message = "about must be atleast with 10 letters")
    private String about;

    private List<RatingDto> ratings;

}
