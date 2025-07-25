package com.blog.app.controller;

import com.blog.app.dto.ApiResponse;
import com.blog.app.dto.EncryptedRequestDTO;
import com.blog.app.dto.EncryptedResponseDTO;
import com.blog.app.dto.JwtAuthRequest;
import com.blog.app.dto.UserDTO;
import com.blog.app.entity.User;
import com.blog.app.repository.UserRepository;
import com.blog.app.security.JwtTokenHelper;
import com.blog.app.service.AuthServiceHelper;
import com.blog.app.service.CustomUserDetailsService;
import com.blog.app.service.EmailService;
import com.blog.app.service.OtpGenerator;
import com.blog.app.service.UserService;
import com.blog.app.util.EncryptedRequest;
import com.blog.app.util.EncryptionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Validator;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
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
    
    @Autowired
    private Validator validator;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/register")
    public ResponseEntity<EncryptedResponseDTO> createUser(@RequestBody EncryptedRequestDTO request) throws Exception {
        // Step 1: Decrypt the incoming encrypted JSON
        String decryptedJson = EncryptionUtil.decrypt(request.getEncryptedData());

        // Step 2: Convert JSON string to UserDTO
        ObjectMapper objectMapper = new ObjectMapper();
        UserDTO userDto = objectMapper.readValue(decryptedJson, UserDTO.class);

        var violations = validator.validate(userDto);
        
        //validate here
        if(!violations.isEmpty()) {
        	violations.iterator().next().getMessage();
        }
        // Step 3: Register the user
        UserDTO createdUser = userService.createUser(userDto);

        // Step 4: Convert createdUser object to JSON
        String responseJson = objectMapper.writeValueAsString(createdUser);

        // Step 5: Encrypt the response JSON
        String encryptedResponse = EncryptionUtil.encrypt(responseJson);

        // Step 6: Wrap in EncryptedResponseDTO
        return ResponseEntity.ok(new EncryptedResponseDTO(encryptedResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<EncryptedResponseDTO> loginUser(@RequestBody EncryptedRequestDTO request) throws Exception {
        String decryptedJson = EncryptionUtil.decrypt(request.getEncryptedData());
        JwtAuthRequest authRequest = objectMapper.readValue(decryptedJson, JwtAuthRequest.class);

        Authentication authentication = authServiceHelper.authenticate(
            authRequest.getUsername(),
            authRequest.getPassword()
        );

        User user = userRepo.findByUsername(authRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = OtpGenerator.generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        user.setOtp(otp);
        user.setOtpExpiryTime(expiry);
        userRepo.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);

        ApiResponse<Void> apiResponse = new ApiResponse<>(PENDING, "OTP sent to your registered email", null);
        String responseJson = objectMapper.writeValueAsString(apiResponse);
        String encryptedResponse = EncryptionUtil.encrypt(responseJson);
        return ResponseEntity.ok(new EncryptedResponseDTO(encryptedResponse));
    }
	
	
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody EncryptedRequest request) {
        try {
            // 1. Decrypt the request
            String decryptedJson = EncryptionUtil.decrypt(request.getEncryptedData());

            // 2. Convert decrypted string to JwtAuthRequest
            ObjectMapper objectMapper = new ObjectMapper();
            JwtAuthRequest authRequest = objectMapper.readValue(decryptedJson, JwtAuthRequest.class);

            // 3. Proceed with your existing logic
            User user = userRepo.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getOtp() == null || user.getOtpExpiryTime() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        Map.of("status", "FAILURE", "message", "OTP not requested")
                );
            }

            if (LocalDateTime.now().isAfter(user.getOtpExpiryTime())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("status", "FAILURE", "message", "OTP expired")
                );
            }

            if (!user.getOtp().equals(authRequest.getOtp())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("status", "FAILURE", "message", "Invalid OTP")
                );
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String token = jwtTokenHelper.generateToken(userDetails);

            user.setOtp(null);
            user.setOtpExpiryTime(null);
            userRepo.save(user);

            Map<String, String> data = Map.of("token", token);

            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Logged In successfully", data));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("status", "FAILURE", "message", "Error during OTP verification")
            );
        }
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
	
	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long userId) {
	    UserDTO userDto = userService.getUserById(userId);
	    return ResponseEntity.ok(
	        new ApiResponse<>(SUCCESS, "User fetched successfully", userDto)
	    );
	}
	
	@GetMapping()
	public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
	    List<UserDTO> users = userService.getAllUsers();
	    return ResponseEntity.ok(
	        new ApiResponse<>(SUCCESS, "All users fetched successfully", users)
	    );
}
}