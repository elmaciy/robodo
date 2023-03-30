package com.robodo.steps;

import com.robodo.runner.RunnerUtil;

public class YillikPatentUcretiTahakkukOlustur extends BaseEpatsSteps {


	
	public YillikPatentUcretiTahakkukOlustur(RunnerUtil runnerUtil) {
		super(runnerUtil);
	
	}

	
	@Override
	public void run() {
		sistemeGiris();

		epatsMenu.gotoBenimSayfam();
		String dosyaNo=getVariable("dosyaNumarasi");
		epatsBenimSayfam.dosyaArama(dosyaNo);
		setVariable("tahakkukNo", "2166777");
		selenium.sleep(5L);
		epatsMenu.cikis();
		
		selenium.stopDriver();
	}



	

}
