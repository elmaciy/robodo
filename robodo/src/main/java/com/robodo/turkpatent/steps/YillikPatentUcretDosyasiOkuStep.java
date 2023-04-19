package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class YillikPatentUcretDosyasiOkuStep extends BaseEpatsStep {

	public YillikPatentUcretDosyasiOkuStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	@Override
	public void run() {
		
		dosyaLinkSifirla();
		dosyaLinkleriGuncelle();

	}

	

	

	

}
