package com.robodo.turkpatent.steps;



import com.robodo.base.BaseApiStep;
import com.robodo.model.ApiResponse;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

import io.restassured.http.Method;

public class DummyApiSteps extends BaseApiStep {

	public DummyApiSteps(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	

	@Override
	public void run() {
		String endpoint=getVariable("url");
		ApiResponse response = getResponse(Method.GET,endpoint,null,null);
		
		setVariable("response.statusCode", "%d".formatted(response.getResponseCode()));
		setVariable("response.headers", "\n%s".formatted(response.getHeadersPrintable()));
		setVariable("response.cookies", "\n%s".formatted(response.getCookiesPrintable()));
		setVariable("response.body", "\n%s".formatted(response.getBody()));
		
	}


}
