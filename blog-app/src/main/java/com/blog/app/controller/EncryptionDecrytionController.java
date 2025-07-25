package com.blog.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.app.dto.ApiResponse;
import com.blog.app.util.EncryptedRequest;
import com.blog.app.util.EncryptionUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/security")
public class EncryptionDecrytionController {

    private final UserController userController;

	@Autowired
	private EncryptionUtil cryptUtils;

	@Autowired
	private ObjectMapper objectMapper;

    EncryptionDecrytionController(UserController userController) {
        this.userController = userController;
    }

	@PostMapping("/encrypt")
	public ResponseEntity<ApiResponse<String>> encryptData(@RequestBody String request) {
		try {
			String plainText = request;
			String encryptedText = EncryptionUtil.encrypt(plainText);
			
			ApiResponse<String> response = new ApiResponse<>("Success", "Encryption successful", encryptedText);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@PostMapping("/decrypt")
	public ResponseEntity<ApiResponse<JsonNode>> decryptData(@RequestBody EncryptedRequest request) {

		try {
			String encryptedText = request.getEncryptedData();
			String decryptedText = cryptUtils.decrypt(encryptedText);
			JsonNode jsonData = objectMapper.readTree(decryptedText);
			ApiResponse<JsonNode> response = new ApiResponse<>("Success", "Decryption successful", jsonData);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
//            throw new ApiException("Decryption failed: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
