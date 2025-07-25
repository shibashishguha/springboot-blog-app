package com.blog.app.service;

import java.util.List;

import com.blog.app.dto.UserDTO;
import com.blog.app.entity.User;

public interface UserService {
    UserDTO createUser(UserDTO userDto);
    boolean emailExists(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    UserDTO convertToDto(User user);
    UserDTO getUserById(Long userId);
    List<UserDTO> getAllUsers();
}