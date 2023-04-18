package com.robodo.base;

import static io.restassured.RestAssured.given;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robodo.model.ApiResponse;
import com.robodo.model.KeyValue;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;
import com.robodo.utils.SeleniumUtil;

import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public abstract class BaseApiStep extends BaseStep {
	
	 protected RunnerUtil runnerUtil;
	 protected ProcessInstanceStep processInstanceStep;
	 protected SeleniumUtil selenium;
	 int fileOrder;
	 
	 
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


	 
	 public ApiResponse getResponse(Method method, String endPoint, List<KeyValue> headers, Object body) {
		 RequestSpecification given = given();
		 given.contentType("application/json; charset=UTF-16");
		 
		 if (headers!=null && !headers.isEmpty()) {
			 for (KeyValue header : headers) {
				 given.header(header.getKey(), header.getValue());
			 } 
		 }
		
		 if (body!=null) {
			 if (body instanceof String) {
				 given.body(body); 
			 } else {
				 given.body(body);
			 }
			 
		 }


		 
		 Response r = given.when().request(method, endPoint);
		 
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
	 
	 
	 


	 
	 public <T> T json2Object(String jsonInput, Class<T> clz) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				return mapper.readValue(jsonInput, clz);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

	 

}
