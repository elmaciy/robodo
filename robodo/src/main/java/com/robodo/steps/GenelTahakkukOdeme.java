package com.robodo.steps;

import com.robodo.runner.RunnerUtil;

public class GenelTahakkukOdeme extends BaseEpatsSteps {


	
	public GenelTahakkukOdeme(RunnerUtil runnerUtil) {
		super(runnerUtil);
	
	}


	
	@Override
	public void run() {
		sistemeGiris();
		epatsMenu.gotoTahakkuklarim();
		epatsTahakkuklarim.tahakkukNoArama("2166777");
		selenium.sleep(10L);
		epatsMenu.cikis();
		selenium.sleep(10L);
		
		selenium.stopDriver();
	}



	

}
