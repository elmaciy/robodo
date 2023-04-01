package com.robodo.steps;

import com.robodo.pages.PageEdevletLogin;
import com.robodo.pages.PageEpatsBasvuruYapan;
import com.robodo.pages.PageEpatsBenimSayfam;
import com.robodo.pages.PageEpatsDosyaBilgisi;
import com.robodo.pages.PageEpatsHizmetDokumu;
import com.robodo.pages.PageEpatsHome;
import com.robodo.pages.PageEpatsMenu;
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
		setVariable("scr.dosyabilgisi.basvuruNumarasi", epatsDosyaBilgisi.getBasvuruNumarasi());
		setVariable("scr.dosyabilgisi.basvuruTarihi", epatsDosyaBilgisi.getBasvuruTarihi());
		setVariable("scr.dosyabilgisi.bulusBasligi", epatsDosyaBilgisi.getBulusBasligi());
		setVariable("scr.dosyabilgisi.sahip.kimlik", epatsDosyaBilgisi.getSahipKimlikVergiNo());
		setVariable("scr.dosyabilgisi.sahip.unvan", epatsDosyaBilgisi.getSahipUnvan());
		epatsDosyaBilgisi.devamEt();
	}
	
	public void hizmetDokumuDevamEt() {
		String ankaraPatentKodu=runnerUtil.getEnvironmentParameter("ankarapatent.epats.kodu");
		epatsHizmetDokumu.basvuruSahibiSec(ankaraPatentKodu);
	}


	
	@Override
	public void run() {
	}



	

}
