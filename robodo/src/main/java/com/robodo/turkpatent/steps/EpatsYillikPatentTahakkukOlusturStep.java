package com.robodo.turkpatent.steps;

import java.util.List;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.RumuzEsleme;
import com.robodo.utils.RunnerUtil;

public class EpatsYillikPatentTahakkukOlusturStep extends BaseEpatsStep {


	
	public EpatsYillikPatentTahakkukOlusturStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}


	
	@Override
	public void run() {
		
		dosyaLinkSifirla();
		dosyaLinkleriGuncelle(this.processInstanceStep.getProcessInstance());
		
		sistemeGiris();
		dosyaAra();
		islemSec();
		basvuruYap();
		dosyaBilgisiDogrulaDevamEt();
		hizmetDokumuDevamEt();
		onizlemeKontrolveTahakkukOlustur();
		tahakkukNumarasiAl();
		anaSayfayaDon();
		epatsMenu.cikis();
		
		dosyaTahakkukKaydet();

		
	}




}
