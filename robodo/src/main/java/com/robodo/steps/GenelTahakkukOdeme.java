package com.robodo.steps;

import com.robodo.utils.RunnerUtil;

public class GenelTahakkukOdeme extends BaseEpatsSteps {


	
	public GenelTahakkukOdeme(RunnerUtil runnerUtil) {
		super(runnerUtil);
	
	}


	
	@Override
	public void run() {
		sistemeGiris();
		epatsMenu.gotoTahakkuklarim();
		String tahakkukNo=getVariable("tahakkukNo");
		epatsTahakkuklarim.tahakkukNoArama(tahakkukNo);
		epatsMenu.cikis();
		
		selenium.stopDriver();
	}



	

}
