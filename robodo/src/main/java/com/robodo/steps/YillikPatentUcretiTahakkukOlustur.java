package com.robodo.steps;

import com.robodo.runner.RunnerUtil;

public class YillikPatentUcretiTahakkukOlustur extends BaseEpatsSteps {


	
	public YillikPatentUcretiTahakkukOlustur(RunnerUtil runnerUtil) {
		super(runnerUtil);
	
	}

	
	@Override
	public void run() {
		sistemeGiris();
		dosyaAraIslemSec();
		basvuruYap();
		setVariable("tahakkukNo", "2166777");
		selenium.sleep(60L);
		epatsMenu.cikis();
		
		selenium.stopDriver();
	}


	



	

}
