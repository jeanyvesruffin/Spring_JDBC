package com.pluralsight.util;

public class ServiceError {
	private int code;
	private String message;

	public ServiceError() {
		super();
	}

	public ServiceError(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
