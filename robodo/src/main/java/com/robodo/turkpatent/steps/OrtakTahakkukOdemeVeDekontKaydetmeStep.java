package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.Rumuz;
import com.robodo.utils.RunnerUtil;

public class OrtakTahakkukOdemeVeDekontKaydetmeStep extends BaseEpatsStep {


	
	public OrtakTahakkukOdemeVeDekontKaydetmeStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}


	
	@Override
	public void run() {
		dosyaDurumGuncelle(EPATS_STATU_ODEME_YAPILIYOR);
		sistemeGiris();
		epatsMenu.gotoTahakkuklarim();
		tahakkukSecVeOdemeyeGit();
		kartGirVeOde();
		dosyaDekontKaydet();
		dosyaDurumGuncelle(EPATS_STATU_ODEME_TAMAMLANDI);
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
		
		
		String dekontNo="DEK_TEST_%s".formatted(String.valueOf(System.currentTimeMillis()));
		
		if (isProduction()) {
			epatsTahakkukOde.odemeYap();
			dekontNo=epatsTahakkukOde.getDekontNo();
		} else {
			selenium.switchToMainFrame();
			epatsTahakkukOde.odemeVazgec();
			takeStepScreenShot(this.processInstanceStep, "Ödeme yapıldı ve dekont oluştu.", false);
		}
		
		
		setVariable("dekontNo", dekontNo);
		selenium.sleep(60L);
	}
	
	




	private String convert2SonKullanmaTarihi(String sonkullanimtarihi) {
		if (sonkullanimtarihi==null) {
			return null;
		}
		// "2025-12-30"
		String year=sonkullanimtarihi.substring(2,4);
		String month=sonkullanimtarihi.substring(5,7);
		
		return month + year;
	}



	

}
