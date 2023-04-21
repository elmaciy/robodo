package com.robodo.turkpatent.apimodel;

import java.util.Date;

public class RumuzEsleme {
    public int id;
    public int islemadimi;
    public String eposta;
    public String telefon;
    public Rumuz rumuz;
    public int statu;
    public Personel personel;
    public Date islemTarihi;
    
    
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIslemadimi() {
		return islemadimi;
	}
	public void setIslemadimi(int islemadimi) {
		this.islemadimi = islemadimi;
	}
	public String getEposta() {
		return eposta;
	}
	public void setEposta(String eposta) {
		this.eposta = eposta;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public Rumuz getRumuz() {
		return rumuz;
	}
	public void setRumuz(Rumuz rumuz) {
		this.rumuz = rumuz;
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
	public Date getIslemTarihi() {
		return islemTarihi;
	}
	public void setIslemTarihi(Date islemTarihi) {
		this.islemTarihi = islemTarihi;
	}
    
    

	
	

	

}
