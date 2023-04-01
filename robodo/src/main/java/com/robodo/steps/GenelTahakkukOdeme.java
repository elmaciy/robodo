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
		tahakkukSecVeOdemeyeGit();
		kartGirVeOde();
		epatsMenu.cikis();
		
		selenium.stopDriver();
	}
	
	private void tahakkukSecVeOdemeyeGit() {
		String tahakkukNo=getVariable("tahakkukNo");
		epatsTahakkuklarim.tahakkukNoArama(tahakkukNo);
		selenium.sleep(60L);
		epatsTahakkuklarim.tahakkukOde();
	}
	
	private void kartGirVeOde() {
		String kartNo=runnerUtil.getEnvironmentParameter("kredikarti.no");
		String kartGecerlilik=runnerUtil.getEnvironmentParameter("kredikarti.gecerlilik");
		String kartCVV=runnerUtil.getEnvironmentParameter("kredikarti.cvv");
		epatsTahakkukOde.kartBilgileriniGir(kartNo, kartGecerlilik, kartCVV);
		selenium.sleep(60L);
		epatsTahakkukOde.odemeYap();
		String dekontNo=epatsTahakkukOde.getDekontNo();
		setVariable("dekontNo", dekontNo);
		
	}



	

}
