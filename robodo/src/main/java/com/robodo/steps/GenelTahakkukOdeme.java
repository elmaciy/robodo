package com.robodo.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

public class GenelTahakkukOdeme extends BaseEpatsSteps {


	
	public GenelTahakkukOdeme(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
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
		epatsTahakkuklarim.tahakkukNoAramaSecme(tahakkukNo);
		takeStepScreenShot(this.processInstanceStep, "Tahakkuk seçildi");
		epatsTahakkuklarim.tahakkukOde();
	}
	
	private void kartGirVeOde() {
		String kartNo=runnerUtil.getEnvironmentParameter("kredikarti.no");
		String kartGecerlilik=runnerUtil.getEnvironmentParameter("kredikarti.gecerlilik");
		String kartCVV=runnerUtil.getEnvironmentParameter("kredikarti.cvv");
		selenium.switchIframe((p)->p.getAttribute("src").contains("/estpay/pay/"));
		
		karsilastir(HelperUtil.normalizeAmount(epatsTahakkukOde.getOdemeTutari()), HelperUtil.normalizeAmount(getVariable("odemeTutari")), "ödenecek tutarı karşılaştır");
		epatsTahakkukOde.kartBilgileriniGir(kartNo, kartGecerlilik, kartCVV);
		takeStepScreenShot(this.processInstanceStep, "Kart bilgileri girildi");
		epatsTahakkukOde.odemeYap();
		String dekontNo=epatsTahakkukOde.getDekontNo();
		takeStepScreenShot(this.processInstanceStep, "Ödeme yapıldı ve dekont oluştu.");
		selenium.switchToMainFrame();
		
		setVariable("dekontNo", dekontNo);
		
	}



	

}
