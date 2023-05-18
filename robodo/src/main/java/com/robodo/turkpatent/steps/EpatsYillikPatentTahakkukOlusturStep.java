package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class EpatsYillikPatentTahakkukOlusturStep extends BaseEpatsStep {


	
	public EpatsYillikPatentTahakkukOlusturStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}


	
	@Override
	public void run() {
		
		dosyaLinkleriGuncelle(this.processInstanceStep.getProcessInstance());
		dosyaDurumGuncelle(EPATS_STATU_TAHAKKUK);
		
		sistemeGiris();
		islemSec();
		basvuruYap();
		dosyaBilgisiDigerIslemler();
		hizmetDokumuKontrolu();
		onizlemeKontrolveTahakkukOlustur();
		tahakkukNumarasiAl();
		anaSayfayaDon();
		epatsMenu.cikis();
		
		dosyaTahakkukKaydetOnayaGonder();

		
	}



	




}
