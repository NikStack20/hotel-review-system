package com.User.Service.servicesImpl;

import com.User.Service.GlobalExceptionHandler.ConflictHandler;
import com.User.Service.GlobalExceptionHandler.DBExceptions;
import com.User.Service.UserRepos.UserRepository;
import com.User.Service.entities.User;
import com.User.Service.loadouts.UserDto;
import com.User.Service.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserResilienceService userResilienceService;

    @Override
    public UserDto saveUser(UserDto userDto) {

        User user = this.modelMapper.map(userDto, User.class);

        if (userRepo.existsByEmail(user.getEmail())) {
            throw new ConflictHandler("Email already registered: " + user.getEmail());
        }
        // 3) Generate ID if missing
        if (user.getUserId() == null || user.getUserId().isBlank()) {
            user.setUserId(UUID.randomUUID().toString());
        }

        user.setName(user.getName());
        user.setEmail(user.getEmail());
        user.setAbout(user.getAbout());
        User saved = this.userRepo.save(user);
        return this.modelMapper.map(saved, UserDto.class);

//		OR User savedUser=this.userRepo.save(user);
//		return savedUser;
    }

    @Override
    public List<UserDto> getAllUsers() {

        List<User> allUser = this.userRepo.findAll();

        List<UserDto> userDtos = allUser.stream().map(user -> this.modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
        return userDtos;
    }

    // getUser

    @Override
    public UserDto getUser(String userId) {

        return userResilienceService.getUserWithResilience(userId);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        User user = this.modelMapper.map(userDto, User.class);
        User hashuser = this.userRepo.findById(userId)
                .orElseThrow(() -> new DBExceptions("User with given userId:" + userId + ", not Found on server x_X"));
        hashuser.setName(user.getName());
        hashuser.setEmail(user.getEmail());
        hashuser.setAbout(user.getAbout());

        User updated = this.userRepo.save(hashuser);

        return this.modelMapper.map(updated, UserDto.class);
    }

    @Override
    public void deleteUser(String userId) {

        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new DBExceptions("User with given userId:" + userId + ", not Found on server x_X"));
        this.userRepo.delete(user);
    }

    /*
     * public boolean findByEmail(String email) { Optional<User> user =
     * this.userRepo.findByEmail(email); return user.isPresent();
     *
     * }
     */

}
