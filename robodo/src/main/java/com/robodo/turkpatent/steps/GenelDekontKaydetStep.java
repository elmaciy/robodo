package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class GenelDekontKaydetStep extends BaseEpatsStep {

	public GenelDekontKaydetStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	@Override
	public void run() {
		dosyaDekontKaydet();

	}

}
