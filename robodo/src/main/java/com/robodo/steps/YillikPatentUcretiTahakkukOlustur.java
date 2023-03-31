package com.robodo.steps;

import com.robodo.runner.RunnerUtil;

public class YillikPatentUcretiTahakkukOlustur extends BaseEpatsSteps {


	
	public YillikPatentUcretiTahakkukOlustur(RunnerUtil runnerUtil) {
		super(runnerUtil);
	
	}

	
	@Override
	public void run() {
		sistemeGiris();

		epatsMenu.gotoVekillikSayfam();
		String dosyaNo=getVariable("dosyaNumarasi");
		String basvuruTuru=getVariable("basvuruTuru");
		String islemAdi=getVariable("islemAdi");
		epatsBenimSayfam.dosyaArama(dosyaNo,basvuruTuru);
		epatsBenimSayfam.islemSec(islemAdi);
		setVariable("tahakkukNo", "2166777");
		selenium.sleep(60L);
		epatsMenu.cikis();
		
		selenium.stopDriver();
	}



	

}
