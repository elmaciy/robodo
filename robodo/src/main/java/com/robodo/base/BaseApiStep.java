package com.robodo.base;

import static io.restassured.RestAssured.given;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robodo.model.ApiResponse;
import com.robodo.model.KeyValue;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public abstract class BaseApiStep extends BaseStep {
	
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


	 
	 public ApiResponse httpRequest(Method method, String endPoint, List<KeyValue> headers, Object body)  {
		 RequestSpecification given = given();
		 given.contentType("application/json; charset=UTF-16");
		 
		 runnerUtil.logger("Request: %s %s".formatted(method.toString(),endPoint));

		 
		 if (headers!=null && !headers.isEmpty()) {
			 for (KeyValue header : headers) {
				 runnerUtil.logger("add header=> %s : %s".formatted(header.getKey(), header.getValue()));
				 given.header(header.getKey(), header.getValue());
			 } 
		 }
		
		 if (body!=null) {
			 runnerUtil.logger("-------------------------------------");
			 runnerUtil.logger("body");
			 runnerUtil.logger("-------------------------------------");
			 
			 if (body instanceof String) {
				 runnerUtil.logger((String) body);
				 given.body(body); 
			 } else {
				String bodyString = HelperUtil.obj2String(body);
				runnerUtil.logger(bodyString);

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
