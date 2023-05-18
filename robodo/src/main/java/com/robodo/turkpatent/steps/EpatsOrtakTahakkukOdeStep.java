package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class EpatsOrtakTahakkukOdeStep extends BaseEpatsStep {


	
	public EpatsOrtakTahakkukOdeStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}


	
	@Override
	public void run() {
		sistemeGiris();
		tahakkukSecVeOdemeyeGit();
		kartGirVeOde();
		epatsMenu.cikis();
		dosyaDurumGuncelle(EPATS_STATU_ODEME_YAPILIYOR);
		
	}
	




	
	








	

}
