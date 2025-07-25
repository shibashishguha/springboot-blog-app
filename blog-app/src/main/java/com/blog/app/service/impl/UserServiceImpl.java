package com.blog.app.service.impl;

import com.blog.app.dto.UserDTO;
import com.blog.app.entity.Role;
import com.blog.app.entity.User;
import com.blog.app.exception.ResourceNotFoundException;
import com.blog.app.repository.RoleRepository;
import com.blog.app.repository.UserRepository;
import com.blog.app.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public boolean existsByUsername(String username) {
    	logger.info("Checking if username '{}' exists", username);
        return userRepo.existsByUsername(username.trim().toLowerCase());
    }
    
    @Override
    public boolean existsByEmail(String email) {
    	logger.info("Checking if email '{}' exists", email);
        return userRepo.existsByEmail(email);
    }

    @Override
    public UserDTO createUser(UserDTO userDto) {
    	 logger.info("Creating new user with email: {}", userDto.getEmail());
    	
    	 if (userRepo.existsByEmail(userDto.getEmail())) {
    		 logger.warn("User creation failed - Email '{}' already exists", userDto.getEmail());
    	        throw new RuntimeException("Email already exists");
    	    }
    	 
        User user = this.dtoToUser(userDto);

        Role role = roleRepo.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);

        User savedUser = userRepo.save(user);
        logger.info("User '{}' created successfully with ID {}", savedUser.getUsername(), savedUser.getUserId());
        return this.userToDto(savedUser);
    }
    
    @Override
    public boolean emailExists(String email) {
    	 logger.debug("Verifying email existence: {}", email);
        return userRepo.existsByEmail(email);
    }
    
    @Override
    public UserDTO getUserById(Long userId) {
    	 logger.info("Fetching user by ID: {}", userId);
    	 User user = userRepo.findById(userId)
                 .orElseThrow(() -> {
                     logger.warn("User with ID {} not found", userId);
                     return new ResourceNotFoundException("User", "id", userId);
                 });
         logger.info("User with ID {} found: {}", userId, user.getUsername());
         return convertToDto(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
    	logger.info("Fetching all users");
        List<User> users = userRepo.findAll();
        logger.debug("Total users found: {}", users.size());
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private User dtoToUser(UserDTO dto) {
    	logger.debug("Mapping UserDTO to User entity for email: {}", dto.getEmail());
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUsername(dto.getUsername());
        user.setAbout(dto.getAbout());
        return user;
    }

    private UserDTO userToDto(User user) {
    	logger.debug("Mapping User entity to UserDTO for ID: {}", user.getUserId());
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAbout(user.getAbout());
        dto.setUsername(user.getUsername());
        Set<String> roleNames = user.getRoles()
            .stream()
            .map(Role::getName)
            .collect(Collectors.toSet());

        dto.setRoles(roleNames);

        return dto;
    }
    
    @Override
    public UserDTO convertToDto(User user) {
        return userToDto(user);
    }

    
}
