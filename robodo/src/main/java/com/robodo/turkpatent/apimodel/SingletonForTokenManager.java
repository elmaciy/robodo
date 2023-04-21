package com.robodo.turkpatent.apimodel;

import com.robodo.model.ApiResponse;
import com.robodo.turkpatent.steps.BaseEpatsStep;

import io.restassured.http.Method;

public class SingletonForTokenManager {
	
	//10 dakika yeter
	public static final long TTL=10*60*1000;
	
	private static SingletonForTokenManager instance;
	private String jwtToken;
	private long startTs=0;
	
	
	private SingletonForTokenManager() {
		
	}
	
	public static SingletonForTokenManager getInstance() {
		if (instance==null) {
			instance =new SingletonForTokenManager();
		}
		
		return instance;
	}

	public String getJwtToken(BaseEpatsStep epatsStep) {
		if (!isTokenValid()) {
			setToken(epatsStep);
		}

		return this.jwtToken;
	}
	
	
	private boolean isTokenValid() {
		return !(this.jwtToken==null || (System.currentTimeMillis()-startTs) > TTL);
	}

	private void setToken(BaseEpatsStep baseEpatsStep) {

		String apiHostname = baseEpatsStep.getEnvironmentParameter("ankarapatent.api.base.url");
		String tokenLang = baseEpatsStep.getEnvironmentParameter("ankarapatent.api.token.lang");
		String tokenUsername = baseEpatsStep.getEnvironmentParameter("ankarapatent.api.token.username");
		String tokenPassword = baseEpatsStep.getEnvironmentParameter("ankarapatent.api.token.password");
		
		String endPoint="%s/login".formatted(apiHostname );
		
		TokenRequest tokenRequest= new TokenRequest();
		tokenRequest.setDil(tokenLang);
		tokenRequest.setKullaniciadi(tokenUsername);
		tokenRequest.setSifre(tokenPassword);
		
		ApiResponse response = baseEpatsStep.httpRequest(Method.POST, endPoint, null, null,  tokenRequest);
		
		if (response.getResponseCode()!=200) {
			throw new RuntimeException("token request başarısız");
		}
		
		if (response.getBody()==null) {
			throw new RuntimeException("token body is null.");
		}
		
		TokenResponse tokenResponse = baseEpatsStep.json2Object(response.getBody(), TokenResponse.class);
		String token = tokenResponse.getData().getJwttoken();
		
		
		if (token==null) {
			throw new RuntimeException("token headeri bulunamadı");
		}
		
		this.jwtToken=token;
		this.startTs = System.currentTimeMillis();
		
	}

}
