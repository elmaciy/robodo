package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.Rumuz;
import com.robodo.utils.RunnerUtil;

public class EpatsOrtakDekontKaydetStep extends BaseEpatsStep {


	
	public EpatsOrtakDekontKaydetStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}


	
	@Override
	public void run() {
		sistemeGiris();
		tahakkuktanDekontSorgula();
		dosyaDekontKaydet();
		epatsMenu.cikis();
		dosyaDurumGuncelle(EPATS_STATU_ODEME_TAMAMLANDI);
		
	}
	




	
	








	

}
