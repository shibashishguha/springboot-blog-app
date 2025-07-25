package com.blog.app.dto;

public class EncryptedResponseDTO {
    private String encryptedData;
    
    public EncryptedResponseDTO() {
		
	}

    
    public EncryptedResponseDTO(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    // Getter
    public String getEncryptedData() {
        return encryptedData;
    }
}
