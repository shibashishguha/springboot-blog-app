package com.blog.app.dto;

public class JwtAuthResponse {
	private String token;
	private String status;
	private String message;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getToken() {
		return token;
	}
	public JwtAuthResponse(String token) {
		this.token=token;
	}
	public JwtAuthResponse() {}
	public String getTokeString() {
		return token;
	}
	public void setToken(String token) {
		this.token=token;
	}
}
