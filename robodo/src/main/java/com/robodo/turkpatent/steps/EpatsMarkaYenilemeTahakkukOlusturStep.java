package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class EpatsMarkaYenilemeTahakkukOlusturStep extends BaseEpatsStep {


	
	public EpatsMarkaYenilemeTahakkukOlusturStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}

	
	@Override
	public void run() {
		
		dosyaLinkSifirla();
		dosyaLinkleriGuncelle(this.processInstanceStep.getProcessInstance());
		
		
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
