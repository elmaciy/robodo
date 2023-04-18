package com.robodo.base;

import static io.restassured.RestAssured.given;

import java.util.Iterator;
import java.util.List;

import com.robodo.model.ApiResponse;
import com.robodo.model.KeyValue;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;
import com.robodo.utils.SeleniumUtil;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public abstract class BaseApiStep extends BaseStep {
	
	 protected RunnerUtil runnerUtil;
	 protected ProcessInstanceStep processInstanceStep;
	 protected SeleniumUtil selenium;
	 int fileOrder;
	 
	 public static final String HTTP_GET="GET";
	 public static final String HTTP_POST="POST";
	 public static final String HTTP_PUT="PUT";
	 public static final String HTTP_DELETE="DELETE";
	 
	 public BaseApiStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	 }
	 
	 @Override
	 public void setup() {
		// all static : nothing to setup
		
	 }
	
	 @Override
	 public void teardown() {
			// all static : nothing to teardown
		
	 }
	
	 
	 public ApiResponse getResponse(String endPoint) {
		 return getResponse(HTTP_GET, endPoint, null, null);
	 }
	 
	 public ApiResponse getResponse(String endPoint, List<KeyValue> headers) {
		 return getResponse(HTTP_GET, endPoint, headers, null);
	 }
	 
	 
	 public ApiResponse getResponse(String method, String endPoint, List<KeyValue> headers, String body) {
		 RequestSpecification given = given();
		 given.contentType("application/json; charset=UTF-16");
		 
		 if (headers!=null && !headers.isEmpty()) {
			 for (KeyValue header : headers) {
				 given.header(header.getKey(), header.getValue());
			 } 
		 }
		
		 if (body!=null && !body.isBlank()) {
			 given.body(body);
		 }
		 
		 Response r = given.when().get(endPoint);
		 
		 var apiResponse = ApiResponse.create()
				 .withBody(r.body().asString())
				 .withResponseCode(r.getStatusCode());
		 
		 for (var header : r.headers()) {
			apiResponse = apiResponse.withHeaderEntity(header.getName(), header.getValue()); 
		 }
		 
		 Iterator<String> itCookies = r.cookies().keySet().iterator();
		 while(itCookies.hasNext()) {
			 String key = itCookies.next();
			 String value = r.cookies().get(key);
			 apiResponse = apiResponse.withCookieEntity(key, value);
		 }
		 
		 return apiResponse;
 
	 }
	 
	 



}
