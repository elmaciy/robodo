package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.Rumuz;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

public class YillikPatentUcretDosyasiOkuStep extends BaseEpatsStep {

	public YillikPatentUcretDosyasiOkuStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	@Override
	public void run() {
		
		Rumuz edevletRumuz = getEdevletRumuz();
		setVariable("edevletRumuz.kimlikNo", edevletRumuz.getTckimlik());
		setVariable("edevletRumuz.sifre", edevletRumuz.getSifre());

		Rumuz krediKartiRumuz = getKrediKartiRumuz();
		setVariable("krediKartiRumuz.kartNo", edevletRumuz.getKredikartino());
		setVariable("krediKartiRumuz.sonKullanmaTarihi", date2SonKullanmaTarihi(edevletRumuz.getSonkullanimtarihi()));
		setVariable("krediKartiRumuz.cvv", edevletRumuz.getCcv());
		
		
		
		dosyaLinkSifirla();
		dosyaLinkleriGuncelle();

	}

	


	

	

	

}
