package com.User.Service.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.User.Service.loadouts.UserDto;
import com.User.Service.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class Controller {

	@Autowired
	private UserService userService;

	// create
	@PostMapping("/create")
	public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {

		return new ResponseEntity<UserDto>(this.userService.saveUser(userDto), HttpStatus.CREATED);
	}

	// update
	@PutMapping("/update/{userId}")
	public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable String userId) {
		return new ResponseEntity<UserDto>(this.userService.updateUser(userDto, userId), HttpStatus.OK);
	}

	@GetMapping("/getUser/{userId}")
	public ResponseEntity<UserDto> getUser(@PathVariable String userId) {
		return new ResponseEntity<UserDto>(this.userService.getUser(userId), HttpStatus.OK);
	}

	// getAllUser
	@GetMapping
	public ResponseEntity<List<UserDto>> getAllUser() {

		return new ResponseEntity<List<UserDto>>(this.userService.getAllUsers(), HttpStatus.OK);
	}

	// delete
	@DeleteMapping("/deleteUser/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	void deleteUser(@PathVariable String userId) {
		this.userService.deleteUser(userId);
	}

}
