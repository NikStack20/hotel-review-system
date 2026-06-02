package com.User.Service.services;

import com.User.Service.loadouts.UserDto;

import java.util.List;

public interface UserService {

    // Creating User
    UserDto saveUser(UserDto user);

    // Get All USers
    List<UserDto> getAllUsers();

    // Get SIngle User with Id
    UserDto getUser(String userId);

    // Update User
    UserDto updateUser(UserDto user, String userId);

    // Delete User
    void deleteUser(String userId);
}
