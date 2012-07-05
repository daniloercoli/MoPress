package com.danais.net;

public class MyHttpConnResponse {
	private boolean error=false;
	private String response ="";
	private String cookie ="";
		
	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookies) {
		this.cookie = cookies;
	}

}
