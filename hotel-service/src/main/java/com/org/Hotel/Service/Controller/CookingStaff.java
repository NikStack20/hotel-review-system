package com.org.Hotel.Service.Controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cookingStaff")
public class CookingStaff {
	
	@GetMapping
	ResponseEntity<List<String>> create () {
	List<String> list = Arrays.asList("Cooking ","a ","Special Dish."); 
	
	return new ResponseEntity<> (list, HttpStatus.OK);
	}

}
