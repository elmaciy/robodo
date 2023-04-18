package com.robodo.turkpatent.apimodel;

import java.util.Date;

public class DosyaRequest {
    public int id;
	public String basvuruno;
    public String basvurusahibi;
    public String dekontno;
    public int durum;
    public String gorevadimi;
    public Date islemTarihi;
    public int islemadimi;
    public int islemkategorisi;
    public int islemturu;
    public Date kayittarihi;
    public String linkkontrol;
    public String linkonayla;
    public String linkreddet;
    public Personel personel;
    public String referansno;
    public int statu;
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
	public int getDurum() {
		return durum;
	}
	public void setDurum(int durum) {
		this.durum = durum;
	}
	public String getGorevadimi() {
		return gorevadimi;
	}
	public void setGorevadimi(String gorevadimi) {
		this.gorevadimi = gorevadimi;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getIslemTarihi() {
		return islemTarihi;
	}
	public void setIslemTarihi(Date islemTarihi) {
		this.islemTarihi = islemTarihi;
	}
	public int getIslemadimi() {
		return islemadimi;
	}
	public void setIslemadimi(int islemadimi) {
		this.islemadimi = islemadimi;
	}
	public int getIslemkategorisi() {
		return islemkategorisi;
	}
	public void setIslemkategorisi(int islemkategorisi) {
		this.islemkategorisi = islemkategorisi;
	}
	public int getIslemturu() {
		return islemturu;
	}
	public void setIslemturu(int islemturu) {
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
	public int getStatu() {
		return statu;
	}
	public void setStatu(int statu) {
		this.statu = statu;
	}
	public String getTahakkukno() {
		return tahakkukno;
	}
	public void setTahakkukno(String tahakkukno) {
		this.tahakkukno = tahakkukno;
	}
    
    
}
