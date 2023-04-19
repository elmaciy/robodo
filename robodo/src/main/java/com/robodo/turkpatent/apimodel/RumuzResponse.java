package com.robodo.turkpatent.apimodel;

import java.util.ArrayList;
import java.util.List;

public class RumuzResponse {
    public int code;
    public String message;
    public String success;
    public String shortCode;
    public String responseEnum;
    public List<Rumuz> data=new ArrayList<Rumuz>();
    
    
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
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
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
	public List<Rumuz> getData() {
		return data;
	}
	public void setData(List<Rumuz> data) {
		this.data = data;
	}
    
    
    
}
