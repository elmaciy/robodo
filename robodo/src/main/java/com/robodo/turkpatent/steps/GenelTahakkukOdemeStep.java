package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.Rumuz;
import com.robodo.utils.RunnerUtil;

public class GenelTahakkukOdemeStep extends BaseEpatsStep {


	
	public GenelTahakkukOdemeStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}


	
	@Override
	public void run() {
		sistemeGiris();
		epatsMenu.gotoTahakkuklarim();
		tahakkukSecVeOdemeyeGit();
		kartGirVeOde();
		epatsMenu.cikis();
		
		//selenium.stopDriver();
	}
	
	private void tahakkukSecVeOdemeyeGit() {
		String tahakkukNo=getVariable("tahakkukNo");
		epatsTahakkuklarim.tahakkukNoAramaSecme(tahakkukNo);
		takeStepScreenShot(this.processInstanceStep, "Tahakkuk seçildi", false);
		epatsTahakkuklarim.tahakkukOde();
	}
	
	private void kartGirVeOde() {
		
		int islemAdimi=Integer.valueOf(getVariable("islemAdimi")); 		
		Rumuz krediKartiRumuz =getRumuzKrediKartiByIslemAdimi(islemAdimi);
		
		String kartNo=krediKartiRumuz.getKredikartino();
		String kartGecerlilik=convert2SonKullanmaTarihi(krediKartiRumuz.getSonkullanimtarihi());
		String kartCVV=krediKartiRumuz.getCcv();
		
		selenium.switchIframe((p)->p.getAttribute("src").contains("/estpay/pay/"));
		
		epatsTahakkukOde.kartBilgileriniGir(kartNo, kartGecerlilik, kartCVV);
		//bu kisim guvenlik sebebiyle kapatildi. kerdi karti bilgisi icermektedir. 
		//akeStepScreenShot(this.processInstanceStep, "Kart bilgileri girildi", false);
		epatsTahakkukOde.odemeYap();
		String dekontNo=epatsTahakkukOde.getDekontNo();
		takeStepScreenShot(this.processInstanceStep, "Ödeme yapıldı ve dekont oluştu.", false);
		selenium.switchToMainFrame();
		
		setVariable("dekontNo", dekontNo);
		
	}
	
	
	private String convert2SonKullanmaTarihi(String sonkullanimtarihi) {
		if (sonkullanimtarihi==null) {
			return null;
		}
		// "2025-12-30"
		String year=sonkullanimtarihi.substring(5,7);
		String month=sonkullanimtarihi.substring(2,4);
		
		return month + year;
	}



	

}
