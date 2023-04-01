package com.robodo.steps;

import com.robodo.utils.RunnerUtil;

public class YillikPatentUcretiTahakkukOlustur extends BaseEpatsSteps {


	
	public YillikPatentUcretiTahakkukOlustur(RunnerUtil runnerUtil) {
		super(runnerUtil);
	
	}

	
	@Override
	public void run() {
		
		//test amacli. silinecek
		setVariable("tahakkukNo", "2166777");
		
		
		sistemeGiris();
		dosyaAraIslemSec();
		basvuruYap();
		dosyaBilgisiDogrulaDevamEt();
		hizmetDokumuDevamEt();
		selenium.sleep(60L);
		epatsMenu.cikis();
		
		selenium.stopDriver();
	}


	



	

}
