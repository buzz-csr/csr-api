package com.naturalmotion.csr_api.service.check;

public class CheckReport {

	private ErrorType error;

	private String message;

	public ErrorType getError() {
		return error;
	}

	public void setError(ErrorType error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
