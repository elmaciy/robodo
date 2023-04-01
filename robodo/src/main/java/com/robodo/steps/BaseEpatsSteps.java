package com.robodo.steps;

import com.robodo.pages.PageEdevletLogin;
import com.robodo.pages.PageEpatsBasvuruYapan;
import com.robodo.pages.PageEpatsBenimSayfam;
import com.robodo.pages.PageEpatsDosyaBilgisi;
import com.robodo.pages.PageEpatsHizmetDokumu;
import com.robodo.pages.PageEpatsHome;
import com.robodo.pages.PageEpatsMenu;
import com.robodo.pages.PageEpatsOnIzleme;
import com.robodo.pages.PageEpatsTahakkuklarim;
import com.robodo.utils.RunnerUtil;

public class BaseEpatsSteps extends BaseSteps {
	
	PageEpatsHome home;
	PageEdevletLogin edevletLogin;
	PageEpatsMenu  epatsMenu;
	PageEpatsBenimSayfam epatsBenimSayfam;
	PageEpatsTahakkuklarim epatsTahakkuklarim;
	PageEpatsBasvuruYapan epatsBasvuruYapan;
	PageEpatsDosyaBilgisi epatsDosyaBilgisi;
	PageEpatsHizmetDokumu epatsHizmetDokumu;
	PageEpatsOnIzleme epatsOnIzleme;
	
	public BaseEpatsSteps(RunnerUtil runnerUtil) {
		super(runnerUtil);
		selenium.startWebDriver();
		this.home=new PageEpatsHome(selenium);
		this.edevletLogin=new PageEdevletLogin(selenium);
		this.epatsMenu=new PageEpatsMenu(selenium);
		this.epatsBenimSayfam=new PageEpatsBenimSayfam(selenium);
		this.epatsTahakkuklarim=new PageEpatsTahakkuklarim(selenium);
		this.epatsBasvuruYapan=new PageEpatsBasvuruYapan(selenium);
		this.epatsDosyaBilgisi=new PageEpatsDosyaBilgisi(selenium);
		this.epatsHizmetDokumu=new PageEpatsHizmetDokumu(selenium);
		this.epatsOnIzleme = new PageEpatsOnIzleme(selenium);
	
	}

	public void sistemeGiris() {
		home.open();
		home.clickEdevlet();
		String tckno=runnerUtil.getEnvironmentParameter("tckno");
		String sifre=runnerUtil.getEnvironmentParameter("sifre");
		edevletLogin.girisEdevlet(tckno, sifre);
	}
	
	
	public void dosyaAraIslemSec() {
		epatsMenu.gotoVekillikSayfam();
		String dosyaNo=getVariable("dosyaNumarasi");
		String basvuruTuru=getVariable("basvuruTuru");
		String islemAdi=getVariable("islemAdi");
		epatsBenimSayfam.dosyaArama(dosyaNo,basvuruTuru);
		//selenium.sleep(10L);
		epatsBenimSayfam.islemSec(islemAdi);
	}
	
	public void basvuruYap() {
		String eposta=runnerUtil.getEnvironmentParameter("eposta");;
		String cepTel=runnerUtil.getEnvironmentParameter("ceptel");
		String referansNo=getVariable("takipNumarasi");
		epatsBasvuruYapan.basvuruBilgileriniDoldur(eposta, cepTel, referansNo);
		epatsBasvuruYapan.devamEt();
	}
	
	public void dosyaBilgisiDogrulaDevamEt() {
		setVariable("dosyabilgisi.dosyabilgisi.basvuruNumarasi", epatsDosyaBilgisi.getBasvuruNumarasi());
		setVariable("dosyabilgisi.dosyabilgisi.basvuruTarihi", epatsDosyaBilgisi.getBasvuruTarihi());
		setVariable("dosyabilgisi.dosyabilgisi.bulusBasligi", epatsDosyaBilgisi.getBulusBasligi());
		setVariable("dosyabilgisi.dosyabilgisi.sahip.kimlik", epatsDosyaBilgisi.getSahipKimlikVergiNo());
		setVariable("dosyabilgisi.dosyabilgisi.sahip.unvan", epatsDosyaBilgisi.getSahipUnvan());
		
		
		karsilastir(getVariable("dosyabilgisi.dosyabilgisi.basvuruNumarasi"), getVariable("dosyaNumarasi"), "dosya numarası karşılaştırılıyor");
		karsilastir(getVariable("dosyabilgisi.dosyabilgisi.bulusBasligi"), getVariable("bulusAdi"), "buluş adı karşılaştırılıyor");
		karsilastir(getVariable("dosyabilgisi.dosyabilgisi.sahip.kimlik"), getVariable("basvuruSahipKimlikNo"), "başvuru sahibi kimlik/vergi no karşılaştırılıyor");
		
		epatsDosyaBilgisi.devamEt();
	}
	
	public void hizmetDokumuDevamEt() {
		String ankaraPatentKodu=runnerUtil.getEnvironmentParameter("ankarapatent.vergino");
		epatsHizmetDokumu.basvuruSahibiSec(ankaraPatentKodu);
		selenium.sleep(3L);
		epatsHizmetDokumu.devamEt();
	}

	public void onizlemeKontrolveTahakkukOlustur() {
		setVariable("onizleme.odenecek.dosyaNumarasi", epatsOnIzleme.getDosyaNumarasi());
		setVariable("onizleme.odenecek.referansNumarasi", epatsOnIzleme.getRefeansTakipNumarasi());
		setVariable("onizleme.odenecek.bulusBasligi", epatsOnIzleme.getBulusBasligi());
		setVariable("onizleme.odenecek.faturaKimlikNumarasi", epatsOnIzleme.getFaturaKimlikNNumarasi());
		setVariable("onizleme.odenecek.cezaTutari", epatsOnIzleme.getCezaTutari());
		setVariable("onizleme.odenecek.genelToplam", epatsOnIzleme.getGenelToplamTutari());
		
		karsilastir(getVariable("onizleme.odenecek.dosyaNumarasi"), getVariable("dosyaNumarasi"), "dosya numarası karşılaştırılıyor");
		karsilastir(getVariable("onizleme.odenecek.referansNumarasi"), getVariable("takipNumarasi"), "başvuru takip/referans numarası karşılaştırılıyor");
		karsilastir(getVariable("onizleme.odenecek.bulusBasligi"), getVariable("bulusAdi"), "buluş adı karşılaştırılıyor");
		karsilastir(getVariable("onizleme.odenecek.faturaKimlikNumarasi"), runnerUtil.getEnvironmentParameter("ankarapatent.vergino"), "ankara patent kimlik/vergi no karşılaştırılıyor");
		karsilastir(getVariable("onizleme.odenecek.genelToplam"), getVariable("odemeTutari"), "ödenecek tutar karşılaştırılıyor");
		
		//epatsOnIzleme.tahakkukOlustur();
		
	}
	
	private void karsilastir(String v1, String v2, String mesaj) {
		runnerUtil.logger("compare [%s] and [%s].".formatted(v1,v2));
		if (v1.equals(v2)) {
			return;
		}
		
		runnerUtil.logger("not validated : %s".formatted(mesaj));
		throw new RuntimeException("%s. v1=%s, v2=%s".formatted(mesaj,v1, v2));
	}

	
	@Override
	public void run() {
	}



	

}
