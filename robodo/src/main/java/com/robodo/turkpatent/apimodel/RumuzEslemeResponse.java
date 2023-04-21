package com.robodo.turkpatent.apimodel;

import java.util.List;

public class RumuzEslemeResponse {
    public int code;
    public String message;
    public Object success;
    public String shortCode;
    public Object responseEnum;
    public List<RumuzEsleme> data;

    
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
	public Object getSuccess() {
		return success;
	}
	public void setSuccess(Object success) {
		this.success = success;
	}
	public String getShortCode() {
		return shortCode;
	}
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	public Object getResponseEnum() {
		return responseEnum;
	}
	public void setResponseEnum(Object responseEnum) {
		this.responseEnum = responseEnum;
	}
	public List<RumuzEsleme> getData() {
		return data;
	}
	public void setData(List<RumuzEsleme> data) {
		this.data = data;
	}
	public void print() {
		this.getData().forEach(p->{
			System.err.println("esleme  id :%d, islemAdimi : %d, statu : %d, eposta : %s, telefon %s"
					.formatted(
						p.getId(),
						p.getIslemadimi(),
						p.getStatu(),
						p.getEposta(),
						p.getTelefon()
			));
			
			
			p.getRumuz().print();
			
		});
		
	}
    
    

}
