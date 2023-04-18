package com.robodo.turkpatent.steps;



import java.util.List;

import com.robodo.base.BaseApiStep;
import com.robodo.model.ApiResponse;
import com.robodo.model.KeyValue;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.DosyaListeleri;
import com.robodo.turkpatent.apimodel.TokenRequest;
import com.robodo.utils.RunnerUtil;

import io.restassured.http.Method;

public class ApiERPBaseStep extends BaseApiStep {

	public ApiERPBaseStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	

	@Override
	public void run() {
		DosyaListeleri listeler=getTaslakDosyalar();
		
		

		
	}
	
	public DosyaListeleri getTaslakDosyalar() {
		String apiHostname = runnerUtil.getEnvironmentParameter("ankarapatent.api.base.url");
		String endPoint="%s/rpaservisleriController/listRpadosyalar".formatted(apiHostname );
		String token=getToken();
		List<KeyValue> headers=List.of(new KeyValue("Authorization",token));
		ApiResponse response = getResponse(Method.GET, endPoint, headers, null);
		if (response.getResponseCode()!=200) {
			throw new RuntimeException("dosyalar listelenemedi");
		}
		
		return json2Object(response.getBody(), DosyaListeleri.class);
	}
	
	
	
	public String getToken() {
		String apiHostname = runnerUtil.getEnvironmentParameter("ankarapatent.api.base.url");
		String tokenUsername = runnerUtil.getEnvironmentParameter("ankarapatent.api.token.username");
		String tokenPassword = runnerUtil.getEnvironmentParameter("ankarapatent.api.token.password");
		
		String endPoint="%s/login".formatted(apiHostname );
		
		TokenRequest tokenRequest= new TokenRequest();
		tokenRequest.setKullaniciadi(tokenUsername);
		tokenRequest.setPassword(tokenPassword);
		
		ApiResponse response = getResponse(Method.POST, endPoint, null, tokenRequest);
		
		String token = response.getHeaderValueByName("Authorization");
		
		if (token==null) {
			throw new RuntimeException("token is not taken");
		}
		
		return token;
	}
	
	


}
