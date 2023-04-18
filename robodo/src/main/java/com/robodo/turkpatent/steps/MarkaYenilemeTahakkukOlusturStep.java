package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class MarkaYenilemeTahakkukOlusturStep extends BaseEpatsStep {


	
	public MarkaYenilemeTahakkukOlusturStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}

	
	@Override
	public void run() {
		sistemeGiris();
		dosyaAra();
		islemSec();
		basvuruYap();
		dosyaBilgisiDogrulaDevamEt();
		talepTuruTamSecVeDevamEt();
		hizmetDokumuDevamEt();
		onizlemeKontrolveTahakkukOlustur();
		tahakkukNumarasiAl();
		anaSayfayaDon();
		epatsMenu.cikis();
		
		//selenium.stopDriver();
	}


	


	



	

}
