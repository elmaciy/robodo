package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.Rumuz;
import com.robodo.utils.RunnerUtil;

public class EpatsOrtakDosyaKaydetStep extends BaseEpatsStep {


	
	public EpatsOrtakDosyaKaydetStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}


	
	@Override
	public void run() {
		//sistemeGiris();
		sistemeGiris(1,"50053246498","k7e6s3k9");
		tahakkuktanDekontSorgula();
		dosyaIndir();
		epatsMenu.cikis();
		//dosyaDurumGuncelle(EPATS_STATU_ODEME_TAMAMLANDI);
		
	}
	




	
	








	

}
