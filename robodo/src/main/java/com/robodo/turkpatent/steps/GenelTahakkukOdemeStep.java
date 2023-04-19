package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

public class GenelTahakkukOdemeStep extends BaseEpatsStep {


	
	public GenelTahakkukOdemeStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}


	
	@Override
	public void run() {
		sistemeGiris();
		epatsMenu.gotoTahakkuklarim();
		tahakkukSecVeOdemeyeGit();
		kartGirVeOde();
		epatsMenu.cikis();
		
		//selenium.stopDriver();
	}
	
	private void tahakkukSecVeOdemeyeGit() {
		String tahakkukNo=getVariable("tahakkukNo");
		epatsTahakkuklarim.tahakkukNoAramaSecme(tahakkukNo);
		takeStepScreenShot(this.processInstanceStep, "Tahakkuk seçildi", false);
		epatsTahakkuklarim.tahakkukOde();
	}
	
	private void kartGirVeOde() {
		String kartNo=runnerUtil.getEnvironmentParameter("kredikarti.no");
		String kartGecerlilik=runnerUtil.getEnvironmentParameter("kredikarti.gecerlilik");
		String kartCVV=runnerUtil.getEnvironmentParameter("kredikarti.cvv");
		selenium.switchIframe((p)->p.getAttribute("src").contains("/estpay/pay/"));
		
		epatsTahakkukOde.kartBilgileriniGir(kartNo, kartGecerlilik, kartCVV);
		takeStepScreenShot(this.processInstanceStep, "Kart bilgileri girildi", false);
		epatsTahakkukOde.odemeYap();
		String dekontNo=epatsTahakkukOde.getDekontNo();
		takeStepScreenShot(this.processInstanceStep, "Ödeme yapıldı ve dekont oluştu.", false);
		selenium.switchToMainFrame();
		
		setVariable("dekontNo", dekontNo);
		
	}



	

}
