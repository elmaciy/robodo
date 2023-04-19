package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class YillikPatentUcretiTahakkukOlusturStep extends BaseEpatsStep {


	
	public YillikPatentUcretiTahakkukOlusturStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}


	
	@Override
	public void run() {
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
