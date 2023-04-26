package com.robodo.turkpatent.apimodel;

import java.util.Date;

public class DosyaResponse {
	public int id;
    public int islemturu;
    public int islemadimi;
    public String gorevadimi;
    public String basvurusahibi;
    public String basvuruno;
    public String referansno;
    public Date kayittarihi;
    public int statu;
    public int durum;
    public String tahakkukno;
    public int islemkategorisi;
    public Personel personel;
    public Date islemTarihi;
    public String dekontno;
    public String linkkontrol;
    public String linkonayla;
    public String linkreddet;
    public int yil;
    
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIslemturu() {
		return islemturu;
	}
	public void setIslemturu(int islemturu) {
		this.islemturu = islemturu;
	}
	public int getIslemadimi() {
		return islemadimi;
	}
	public void setIslemadimi(int islemadimi) {
		this.islemadimi = islemadimi;
	}
	public String getGorevadimi() {
		return gorevadimi;
	}
	public void setGorevadimi(String gorevadimi) {
		this.gorevadimi = gorevadimi;
	}
	public String getBasvurusahibi() {
		return basvurusahibi;
	}
	public void setBasvurusahibi(String basvurusahibi) {
		this.basvurusahibi = basvurusahibi;
	}
	public String getBasvuruno() {
		return basvuruno;
	}
	public void setBasvuruno(String basvuruno) {
		this.basvuruno = basvuruno;
	}
	public String getReferansno() {
		return referansno;
	}
	public void setReferansno(String referansno) {
		this.referansno = referansno;
	}
	public Date getKayittarihi() {
		return kayittarihi;
	}
	public void setKayittarihi(Date kayittarihi) {
		this.kayittarihi = kayittarihi;
	}
	public int getStatu() {
		return statu;
	}
	public void setStatu(int statu) {
		this.statu = statu;
	}
	public int getDurum() {
		return durum;
	}
	public void setDurum(int durum) {
		this.durum = durum;
	}
	public String getTahakkukno() {
		return tahakkukno;
	}
	public void setTahakkukno(String tahakkukno) {
		this.tahakkukno = tahakkukno;
	}
	public int getIslemkategorisi() {
		return islemkategorisi;
	}
	public void setIslemkategorisi(int islemkategorisi) {
		this.islemkategorisi = islemkategorisi;
	}
	public Personel getPersonel() {
		return personel;
	}
	public void setPersonel(Personel personel) {
		this.personel = personel;
	}
	public Date getIslemTarihi() {
		return islemTarihi;
	}
	public void setIslemTarihi(Date islemTarihi) {
		this.islemTarihi = islemTarihi;
	}
	public String getDekontno() {
		return dekontno;
	}
	public void setDekontno(String dekontno) {
		this.dekontno = dekontno;
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
	public int getYil() {
		return yil;
	}
	public void setYil(int yil) {
		this.yil = yil;
	}
    
    
}
