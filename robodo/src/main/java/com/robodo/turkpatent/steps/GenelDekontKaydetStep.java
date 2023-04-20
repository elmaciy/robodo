package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class GenelDekontKaydetStep extends BaseEpatsStep {

	public GenelDekontKaydetStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	
	@Override
	public void setup() {
		//selenium webdriver/chrome acmasin diye eziyoruz
	}
	
	@Override
	public void teardown() {
		//selenium webdriver/chrome acmadigi icn eziyoruz
	}
	
	@Override
	public void run() {
		dosyaDekontKaydet();

	}

}
