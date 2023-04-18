package com.robodo.turkpatent.steps;



import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robodo.base.BaseApiStep;
import com.robodo.model.ApiResponse;
import com.robodo.model.KeyValue;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.Breeding;
import com.robodo.utils.RunnerUtil;

import io.restassured.http.Method;

public class ApiERPStep extends BaseApiStep {

	public ApiERPStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
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
