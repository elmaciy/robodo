package com.robodo.turkpatent.steps;



import com.robodo.base.BaseWebStep;
import com.robodo.model.ApiResponse;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

import io.restassured.http.Method;

public class DummyApiSteps extends BaseWebStep {

	public DummyApiSteps(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	

	@Override
	public void run() {
		String endpoint=getVariable("url");
		ApiResponse response = httpRequest(Method.GET,endpoint,null,null);
		
		setVariable("response.statusCode", "%d".formatted(response.getResponseCode()));
		setVariable("response.headers", "\n%s".formatted(response.getHeadersPrintable()));
		setVariable("response.body", "\n%s".formatted(response.getBody()));
		
	}



	@Override
	public void setup() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void teardown() {
		// TODO Auto-generated method stub
		
	}


}
