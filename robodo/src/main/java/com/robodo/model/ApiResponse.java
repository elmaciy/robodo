package com.robodo.model;

import java.util.ArrayList;
import java.util.List;

public class ApiResponse {
	
	private int responseCode;
	private List<KeyValue> headers=new ArrayList<KeyValue>();
	private ArrayList<KeyValue> cookies =  new ArrayList<KeyValue>();
	private String body;
	
	

	public int getResponseCode() {
		return responseCode;
	}


	public List<KeyValue> getHeaders() {
		return headers;
	}


	public ArrayList<KeyValue> getCookies() {
		return cookies;
	}


	public String getBody() {
		return body;
	}


	private ApiResponse() {
		
	}
	
	
	public static ApiResponse create() {
		return new ApiResponse();
	}
	
	public ApiResponse withResponseCode(int responceCode) {
		this.responseCode=responceCode;
		return this;
	}
	
	public ApiResponse withBody(String body) {
		this.body=body;
		return this;
	}
	
	public ApiResponse withHeaderEntity(String key, String value) {
		this.headers.add(new KeyValue(key, value));
		return this;
	}
	
	public ApiResponse withCookieEntity(String key, String value) {
		this.cookies.add(new KeyValue(key, value));
		return this;
	}
	
	public void print() {
		System.out.println("responseCode : %d".formatted(this.responseCode));
		if (!this.headers.isEmpty()) {
			System.out.println("\t   -------------------------------------------");
			System.out.println("\t   HEADERS -----------------------------------");
			System.out.println("\t   -------------------------------------------");

		}
		for (KeyValue kv : this.headers) {
			System.out.println("\t%s=[%s]".formatted(kv.getKey(), kv.getValue()));
		}

		if (!this.headers.isEmpty()) {
			System.out.println("\t   -------------------------------------------");
			System.out.println("\t   COOKIES -----------------------------------");
			System.out.println("\t   -------------------------------------------");

		}
		for (KeyValue kv : this.cookies) {
			System.out.println("\t%s=[%s]".formatted(kv.getKey(), kv.getValue()));
		}

		
		System.out.println("body : %d".formatted(this.body));
	}


	public String getHeadersPrintable() {
		StringBuilder sb=new StringBuilder();
		for (KeyValue kv : this.getHeaders()) {
			sb.append("%s=%s\n".formatted(kv.getKey(), kv.getValue()));
		}
		return sb.toString();
	}
	
	public String getCookiesPrintable() {
		StringBuilder sb=new StringBuilder();
		for (KeyValue kv : this.getCookies()) {
			sb.append("%s=%s\n".formatted(kv.getKey(), kv.getValue()));
		}
		return sb.toString();
	}
}
