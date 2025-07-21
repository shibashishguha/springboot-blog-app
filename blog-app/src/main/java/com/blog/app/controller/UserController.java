package com.blog.app.controller;

import com.blog.app.dto.ApiResponse;
import com.blog.app.dto.JwtAuthRequest;
import com.blog.app.dto.UserDTO;
import com.blog.app.dto.UsernameCheckResponse;
import com.blog.app.entity.User;
import com.blog.app.repository.UserRepository;
import com.blog.app.security.JwtTokenHelper;
import com.blog.app.service.AuthServiceHelper;
import com.blog.app.service.CustomUserDetailsService;
import com.blog.app.service.EmailService;
import com.blog.app.service.OtpGenerator;
import com.blog.app.service.UserService;
import jakarta.validation.Valid;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import static com.blog.app.constants.AppResponseStatus.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
    private AuthenticationManager authenticationManager;
	
	@Autowired
	private AuthServiceHelper authServiceHelper;
	
	@Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDto) {

		UserDTO createdUser = userService.createUser(userDto);
		ApiResponse<UserDTO> response = new ApiResponse<>(SUCCESS, "User registered successfully", createdUser);
	    return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@GetMapping("/check-username")
	public ResponseEntity<ApiResponse<UsernameCheckResponse>> checkUsername(@RequestParam String username) {
	    boolean exists = userService.existsByUsername(username.trim());

	    String message = exists
	        ? "Username already exists"
	        : "Username is available";

	    UsernameCheckResponse data = new UsernameCheckResponse(
	        HttpStatus.OK.value(),
	        message,
	        exists
	    );

	    ApiResponse<UsernameCheckResponse> response = new ApiResponse<>(SUCCESS, message, data);
	    return ResponseEntity.ok(response);
	}
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Void>> loginUser(@RequestBody JwtAuthRequest request) {
	    Authentication authentication = authServiceHelper.authenticate(
	        request.getUsername(),
	        request.getPassword()
	    );

	    User user = userRepo.findByUsername(request.getUsername())
	        .orElseThrow(() -> new RuntimeException("User not found"));

	    String otp = OtpGenerator.generateOtp();
	    LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

	    user.setOtp(otp);
	    user.setOtpExpiryTime(expiry);
	    userRepo.save(user);

	    emailService.sendOtpEmail(user.getEmail(), otp);

	    ApiResponse<Void> response = new ApiResponse<>(PENDING, "OTP sent to your registered email", null);
	    return ResponseEntity.ok(response);
	}
	
	
	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@RequestBody JwtAuthRequest request) {

	    User user = userRepo.findByUsername(request.getUsername())
	        .orElseThrow(() -> new RuntimeException("User not found"));

	    
	    if (user.getOtp() == null || user.getOtpExpiryTime() == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
	            Map.of("status",FAILURE, "message", "OTP not requested")
	        );
	    }

	    
	    if (LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
	            Map.of("status",FAILURE, "message", "OTP expired")
	        );
	    }

	    
	    if (!user.getOtp().equals(request.getOtp())) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
	            Map.of("status",FAILURE, "message", "Invalid OTP")
	        );
	    }

	   
	    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
	    String token = jwtTokenHelper.generateToken(userDetails);

	    
	    user.setOtp(null);
	    user.setOtpExpiryTime(null);
	    userRepo.save(user);

	    Map<String, String> data = Map.of("token", token);

	    return ResponseEntity.ok(new ApiResponse<>(SUCCESS, "Logged In succcessfully", data));
	}
	
	@PutMapping("/update")
	public ResponseEntity<ApiResponse<UserDTO>> updateCurrentUser(@RequestBody UserDTO userDto) {

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String currentUsername = auth.getName();

	    User user = userRepo.findByUsername(currentUsername)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    user.setName(userDto.getName());
	    user.setEmail(userDto.getEmail());
	    user.setAbout(userDto.getAbout());

	    if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
	        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
	    }

	    User updatedUser = userRepo.save(user);

	    UserDTO updatedDto = userService.convertToDto(updatedUser);

	    return ResponseEntity.ok(new ApiResponse<>(SUCCESS, "User details updated successfully", updatedDto));
	}
	
	@GetMapping("/currentuser")
	public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(Principal principal) {
	    String username = principal.getName();
	    User user = userRepo.findByUsername(username)
	        .orElseThrow(() -> new RuntimeException("User not found"));
	    
	    UserDTO dto = userService.convertToDto(user);
	    
	    return ResponseEntity.ok(
	        new ApiResponse<>(SUCCESS, "Current user fetched successfully", dto)
	    );
	}
}