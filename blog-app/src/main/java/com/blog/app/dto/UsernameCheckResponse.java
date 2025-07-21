package com.blog.app.dto;

public class UsernameCheckResponse {
	private int status;
    private String message;
    private boolean exists;

    public UsernameCheckResponse(int status, String message, boolean exists) {
        this.status = status;
        this.message = message;
        this.exists = exists;
    }

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}
}
