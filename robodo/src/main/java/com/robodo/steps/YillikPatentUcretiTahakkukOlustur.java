package com.robodo.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class YillikPatentUcretiTahakkukOlustur extends BaseEpatsSteps {


	
	public YillikPatentUcretiTahakkukOlustur(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}

	
	@Override
	public void run() {
		sistemeGiris();
		dosyaAra();
		islemSec();
		basvuruYap();
		dosyaBilgisiDogrulaDevamEt();
		hizmetDokumuDevamEt();
		onizlemeKontrolveTahakkukOlustur();
		tahakkukNumarasiAl();
		anaSayfayaDon();
		epatsMenu.cikis();
		
		selenium.stopDriver();
	}


	


	



	

}
