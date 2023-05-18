package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.Rumuz;
import com.robodo.utils.RunnerUtil;

public class EpatsOrtakRetryBildirmeStep extends BaseEpatsStep {


	
	public EpatsOrtakRetryBildirmeStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}

	@Override
	public void setup() {
		
	}
	
	@Override
	public void teardown() {
		
	}
	
	
	@Override
	public void run() {
		dosyaTahakkukNoGuncelle("-");
		dosyaDekontNoGuncelle("-");
		dosyaLinkSifirla();
	}




	

}
