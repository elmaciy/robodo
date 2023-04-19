package com.robodo.turkpatent.apimodel;

public class TokenResponse {
	
	public int code;
    public String message;
    public boolean success;
    public String shortCode;
    public String responseEnum;
    public TokenData data;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getShortCode() {
		return shortCode;
	}
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	public String getResponseEnum() {
		return responseEnum;
	}
	public void setResponseEnum(String responseEnum) {
		this.responseEnum = responseEnum;
	}
	public TokenData getData() {
		return data;
	}
	public void setData(TokenData data) {
		this.data = data;
	}
    
    

}
