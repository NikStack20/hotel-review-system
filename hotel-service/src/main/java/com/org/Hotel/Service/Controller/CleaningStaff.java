package com.org.Hotel.Service.Controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cleaningStaff")
public class CleaningStaff {
	
	@GetMapping
	ResponseEntity<List<String>> cleanNow () {
	List<String> list = Arrays.asList("Cleaning ","Room No. ","414."); 
	
	return new ResponseEntity<> (list, HttpStatus.OK);
	}

}
