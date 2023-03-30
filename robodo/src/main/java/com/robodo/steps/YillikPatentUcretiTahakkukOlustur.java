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
		epatsBenimSayfam.dosyaArama("2019/06601");
		selenium.sleep(10L);
		epatsMenu.cikis();
		selenium.sleep(10L);
		
		selenium.stopDriver();
	}



	

}
