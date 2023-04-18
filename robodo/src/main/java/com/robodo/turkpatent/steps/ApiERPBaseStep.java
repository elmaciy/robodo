package com.robodo.turkpatent.steps;



import java.util.List;

import com.robodo.base.BaseApiStep;
import com.robodo.model.ApiResponse;
import com.robodo.model.KeyValue;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.DosyaListeleri;
import com.robodo.turkpatent.apimodel.DosyaRequest;
import com.robodo.turkpatent.apimodel.TokenRequest;
import com.robodo.utils.RunnerUtil;

import io.restassured.http.Method;

public class ApiERPBaseStep extends BaseApiStep {
	
	

	public final static Integer EPATS_STATU_TASLAK=0; //????
	public final static Integer EPATS_STATU_ISLEMDE=1; // "RPA işlemde";
	public final static Integer EPATS_STATU_TAHAKKUK=2; // "RPA Tahakkuk";
	public final static Integer EPATS_STATU_ODEME=3; //"RPA ödeme";

	public ApiERPBaseStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	

	@Override
	public void run() {
	
		
		
	}
	
	
	
	public void dosyaLinkleriGuncelle(int id, String linkKontrol, String linkOnayla, String linkReddet) {
		dosyaGuncelle(DosyaRequest.create(id).withLinks(linkKontrol, linkOnayla, linkReddet),
				"dosya [%s] linkleri güncelle => kontrol : <%s>, onay : <%s>, red : <%s>".formatted(id, linkKontrol, linkOnayla, linkReddet));
	}
	
	
	
	public void dosyaDurumGuncelle(int id, int statu) {
		dosyaGuncelle(DosyaRequest.create(id).withStatu(statu),
				"dosya [%s] durum güncelle => %d".formatted(id, statu));
	}
	
	public void dosyaLTahakkukNoGuncelle(int id, String tahakkukNo) {
		dosyaGuncelle(DosyaRequest.create(id).withTahakkukNo(tahakkukNo),
				"dosya [%s] tahakkuk no güncelle => %s".formatted(id, tahakkukNo));
	}
	
	public void dosyaLDekontNoGuncelle(int id, String dekontNo) {
		dosyaGuncelle(DosyaRequest.create(id).withDekontNo(dekontNo),
				"dosya [%s] dekont no güncelle => %s".formatted(id, dekontNo));
	}
	
	
	private void dosyaGuncelle(DosyaRequest dosyaRequest, String description) {
		String apiHostname = runnerUtil.getEnvironmentParameter("ankarapatent.api.base.url");
		String endPoint="%s/rpaservisleriController/updateRpadosyaislemleri".formatted(apiHostname );
		String token=getToken();
		List<KeyValue> headers=List.of(new KeyValue("Authorization",token));
		
		ApiResponse response = httpRequest(Method.GET, endPoint, headers, dosyaRequest);

		if (response.getResponseCode()!=200) {
			throw new RuntimeException("Güncelleme başarısız : ".formatted(description));
		}
	}
	
	public DosyaListeleri getTaslakDosyalar() {
		String apiHostname = runnerUtil.getEnvironmentParameter("ankarapatent.api.base.url");
		String endPoint="%s/rpaservisleriController/listRpadosyalar".formatted(apiHostname );
		String token=getToken();
		List<KeyValue> headers=List.of(new KeyValue("Authorization",token));
		ApiResponse response = httpRequest(Method.GET, endPoint, headers, null);
		
		
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
		
		ApiResponse response = httpRequest(Method.POST, endPoint, null, tokenRequest);
		
		if (response.getResponseCode()!=200) {
			throw new RuntimeException("token alınamadı");
		}
		
		String token = response.getHeaderValueByName("Authorization");
		
		if (token==null) {
			throw new RuntimeException("token headeri bulunamadı");
		}
		
		return token;
		
	}
	
	


}
