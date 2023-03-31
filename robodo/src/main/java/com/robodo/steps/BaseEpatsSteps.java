package com.robodo.steps;

import com.robodo.pages.PageEdevletLogin;
import com.robodo.pages.PageEpatsBasvuruYapan;
import com.robodo.pages.PageEpatsBenimSayfam;
import com.robodo.pages.PageEpatsHome;
import com.robodo.pages.PageEpatsMenu;
import com.robodo.pages.PageEpatsTahakkuklarim;
import com.robodo.runner.RunnerUtil;

public class BaseEpatsSteps extends BaseSteps {
	
	PageEpatsHome home;
	PageEdevletLogin edevletLogin;
	PageEpatsMenu  epatsMenu;
	PageEpatsBenimSayfam epatsBenimSayfam;
	PageEpatsTahakkuklarim epatsTahakkuklarim;
	PageEpatsBasvuruYapan epatsBasvuruYapan;
	
	public BaseEpatsSteps(RunnerUtil runnerUtil) {
		super(runnerUtil);
		selenium.startWebDriver();
		this.home=new PageEpatsHome(selenium);
		this.edevletLogin=new PageEdevletLogin(selenium);
		this.epatsMenu=new PageEpatsMenu(selenium);
		this.epatsBenimSayfam=new PageEpatsBenimSayfam(selenium);
		this.epatsTahakkuklarim=new PageEpatsTahakkuklarim(selenium);
		this.epatsBasvuruYapan=new PageEpatsBasvuruYapan(selenium);
	
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
		epatsBenimSayfam.islemSec(islemAdi);
	}
	
	public void basvuruYap() {
		String eposta=runnerUtil.getEnvironmentParameter("eposta");;
		String cepTel=runnerUtil.getEnvironmentParameter("ceptel");
		String referansNo=getVariable("takipNumarasi");
		epatsBasvuruYapan.basvuruBilgileriniDoldur(eposta, cepTel, referansNo);
		epatsBasvuruYapan.devamEt();
	}
	
	@Override
	public void run() {
	}



	

}
