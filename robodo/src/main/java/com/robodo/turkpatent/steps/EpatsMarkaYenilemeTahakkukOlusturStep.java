package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class EpatsMarkaYenilemeTahakkukOlusturStep extends BaseEpatsStep {


	
	public EpatsMarkaYenilemeTahakkukOlusturStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}

	
	@Override
	public void run() {

		dosyaLinkleriGuncelle(this.processInstanceStep.getProcessInstance());
		dosyaDurumGuncelle(EPATS_STATU_TAHAKKUK);
		
		sistemeGiris();
		//bu kısımı iptal ettik çünkü işlem adımı Patentteki gibi olacak 
		//dosyaAra();
		islemSec();
		basvuruYap();
		dosyaBilgisiDogrulaDevamEt();
		talepTuruTamSecVeDevamEt();
		hizmetDokumuKontrolu();
		onizlemeKontrolveTahakkukOlustur();
		tahakkukNumarasiAl();
		anaSayfayaDon();
		epatsMenu.cikis();
		
		//selenium.stopDriver();
	}


	


	



	

}
