package com.robodo.turkpatent.apimodel;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_EMPTY)
public class DosyaRequest {
    public Integer id;
	public String basvuruno;
    public String basvurusahibi;
    public String dekontno;
    public Integer durum;
    public String gorevadimi;
    public Date islemTarihi;
    public Integer islemadimi;
    public Integer islemkategorisi;
    public Integer islemturu;
    public Date kayittarihi;
    public String linkkontrol;
    public String linkonayla;
    public String linkreddet;
    public Personel personel;
    public String referansno;
    public Integer statu;
    public String tahakkukno;
    
    
    
	public String getBasvuruno() {
		return basvuruno;
	}
	public void setBasvuruno(String basvuruno) {
		this.basvuruno = basvuruno;
	}
	public String getBasvurusahibi() {
		return basvurusahibi;
	}
	public void setBasvurusahibi(String basvurusahibi) {
		this.basvurusahibi = basvurusahibi;
	}
	public String getDekontno() {
		return dekontno;
	}
	public void setDekontno(String dekontno) {
		this.dekontno = dekontno;
	}
	public Integer getDurum() {
		return durum;
	}
	public void setDurum(Integer durum) {
		this.durum = durum;
	}
	public String getGorevadimi() {
		return gorevadimi;
	}
	public void setGorevadimi(String gorevadimi) {
		this.gorevadimi = gorevadimi;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getIslemTarihi() {
		return islemTarihi;
	}
	public void setIslemTarihi(Date islemTarihi) {
		this.islemTarihi = islemTarihi;
	}
	public Integer getIslemadimi() {
		return islemadimi;
	}
	public void setIslemadimi(Integer islemadimi) {
		this.islemadimi = islemadimi;
	}
	public Integer getIslemkategorisi() {
		return islemkategorisi;
	}
	public void setIslemkategorisi(Integer islemkategorisi) {
		this.islemkategorisi = islemkategorisi;
	}
	public Integer getIslemturu() {
		return islemturu;
	}
	public void setIslemturu(Integer islemturu) {
		this.islemturu = islemturu;
	}
	public Date getKayittarihi() {
		return kayittarihi;
	}
	public void setKayittarihi(Date kayittarihi) {
		this.kayittarihi = kayittarihi;
	}
	public String getLinkkontrol() {
		return linkkontrol;
	}
	public void setLinkkontrol(String linkkontrol) {
		this.linkkontrol = linkkontrol;
	}
	public String getLinkonayla() {
		return linkonayla;
	}
	public void setLinkonayla(String linkonayla) {
		this.linkonayla = linkonayla;
	}
	public String getLinkreddet() {
		return linkreddet;
	}
	public void setLinkreddet(String linkreddet) {
		this.linkreddet = linkreddet;
	}
	public Personel getPersonel() {
		return personel;
	}
	public void setPersonel(Personel personel) {
		this.personel = personel;
	}
	public String getReferansno() {
		return referansno;
	}
	public void setReferansno(String referansno) {
		this.referansno = referansno;
	}
	public Integer getStatu() {
		return statu;
	}
	public void setStatu(Integer statu) {
		this.statu = statu;
	}
	public String getTahakkukno() {
		return tahakkukno;
	}
	public void setTahakkukno(String tahakkukno) {
		this.tahakkukno = tahakkukno;
	}
    
    public static DosyaRequest create(Integer id) {
    	DosyaRequest dosyaRequest= new DosyaRequest();
    	dosyaRequest.setId(id);
    	return dosyaRequest;
    }
    
    public DosyaRequest withStatu(int statu) {
    	this.setStatu(statu);
    	return this;
    }
    
    public DosyaRequest withLinks(String linkkontrol, String linkonayla, String linkreddet) {
    	this.setLinkkontrol(linkkontrol);
    	this.setLinkonayla(linkonayla);
    	this.setLinkreddet(linkreddet);
    	return this;
    }
	public DosyaRequest withTahakkukNo(String tahakkukNo) {
		this.setTahakkukno(tahakkukNo);
    	return this;
	}
	public DosyaRequest withDekontNo(String dekontNo) {
		this.setDekontno(dekontNo);
    	return this;
		
	}
}
