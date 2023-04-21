package com.robodo.turkpatent.apimodel;

import java.util.Date;

public class Rumuz {
    public int id;
    public int parametreturu;
    public String kredikartirumuz;
    public String kredikartino;
    public String ccv;
    public String sonkullanimtarihi;
    public String edevletrumuz;
    public String tckimlik;
    public String sifre;
    public int statu;
    public Personel personel;
    public Date islem_tarihi;
    
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getParametreturu() {
		return parametreturu;
	}
	public void setParametreturu(int parametreturu) {
		this.parametreturu = parametreturu;
	}
	public String getKredikartirumuz() {
		return kredikartirumuz;
	}
	public void setKredikartirumuz(String kredikartirumuz) {
		this.kredikartirumuz = kredikartirumuz;
	}
	public String getKredikartino() {
		return kredikartino;
	}
	public void setKredikartino(String kredikartino) {
		this.kredikartino = kredikartino;
	}
	public String getCcv() {
		return ccv;
	}
	public void setCcv(String ccv) {
		this.ccv = ccv;
	}
	public String getSonkullanimtarihi() {
		return sonkullanimtarihi;
	}
	public void setSonkullanimtarihi(String sonkullanimtarihi) {
		this.sonkullanimtarihi = sonkullanimtarihi;
	}
	public String getEdevletrumuz() {
		return edevletrumuz;
	}
	public void setEdevletrumuz(String edevletrumuz) {
		this.edevletrumuz = edevletrumuz;
	}
	public String getTckimlik() {
		return tckimlik;
	}
	public void setTckimlik(String tckimlik) {
		this.tckimlik = tckimlik;
	}
	public String getSifre() {
		return sifre;
	}
	public void setSifre(String sifre) {
		this.sifre = sifre;
	}
	public int getStatu() {
		return statu;
	}
	public void setStatu(int statu) {
		this.statu = statu;
	}
	public Personel getPersonel() {
		return personel;
	}
	public void setPersonel(Personel personel) {
		this.personel = personel;
	}
	public Date getIslem_tarihi() {
		return islem_tarihi;
	}
	public void setIslem_tarihi(Date islem_tarihi) {
		this.islem_tarihi = islem_tarihi;
	}
	
	
	public void print() {
		
		if (this.getParametreturu()==1) {
			System.err.println("Edevlet Rumuz [%d] : tckn : %s, sifre : ********".formatted(this.getId(), this.getTckimlik()));
			
		} else {
			System.err.println("KK Rumuz [%d] : no : %s, sonkul. : ***, cvv : ***".formatted(this.getId(), this.getKredikartino()));
		}
		
	}
	

    
    
}
