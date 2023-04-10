package com.robodo.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.robodo.services.ProcessService;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "tokenization", 
indexes = {
		@Index(name="ndx_tokenization_token", columnList = "token", unique = true),
		@Index(name="ndx_tokenization_purpose", columnList = "purpose,purposeDetail", unique = true)
			})
public class Tokenization {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long id;
	@Column(length = 1000, nullable = false)
	String token;
	String purpose;
	String purposeDetail;
	LocalDateTime validFrom;
	LocalDateTime validTo;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(LocalDateTime validFrom) {
		this.validFrom = validFrom;
	}

	public LocalDateTime getValidTo() {
		return validTo;
	}

	public void setValidTo(LocalDateTime validTo) {
		this.validTo = validTo;
	}
	
	
	
	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getPurposeDetail() {
		return purposeDetail;
	}

	public void setPurposeDetail(String purposeDetail) {
		this.purposeDetail = purposeDetail;
	}

	public static Tokenization generateNewToken(ProcessService processService, String purpose, String purposeDetail, long tokenDuration) {
		var validTo = LocalDateTime.now().plusSeconds(tokenDuration);
		
		Tokenization t1= processService.getToken(purpose, purposeDetail);
		
		if (t1!=null) {
			t1.setValidTo(validTo);
			processService.saveToken(t1);
			return t1;
		}

		Tokenization token=new Tokenization();
		token.setPurpose(purpose);
		token.setPurposeDetail(purposeDetail);
		token.setToken(UUID.randomUUID().toString());
		token.setValidFrom(LocalDateTime.now());
		token.setValidTo(validTo);
		processService.saveToken(token);
		return token;
	}

}
