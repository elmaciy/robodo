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
		//kartGirVeOde();
		tahakkuktanDekontSorgula();
		dosyaDekontKaydet();
		dosyaDurumGuncelle(EPATS_STATU_ODEME_TAMAMLANDI);
		epatsMenu.cikis();
		
		//selenium.stopDriver();
	}
	




	
	








	

}
