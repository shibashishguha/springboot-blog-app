package com.blog.app.service.impl;

import com.blog.app.dto.UserDTO;
import com.blog.app.entity.Role;
import com.blog.app.entity.User;
import com.blog.app.repository.RoleRepository;
import com.blog.app.repository.UserRepository;
import com.blog.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username.trim().toLowerCase());
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public UserDTO createUser(UserDTO userDto) {
    	
    	 if (userRepo.existsByEmail(userDto.getEmail())) {
    	        throw new RuntimeException("Email already exists");
    	    }
    	 
        User user = this.dtoToUser(userDto);

        Role role = roleRepo.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);

        User savedUser = userRepo.save(user);
        return this.userToDto(savedUser);
    }
    
    @Override
    public boolean emailExists(String email) {
        return userRepo.existsByEmail(email);
    }

    private User dtoToUser(UserDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUsername(dto.getUsername());
        user.setAbout(dto.getAbout());
        return user;
    }

    private UserDTO userToDto(User user) {
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
