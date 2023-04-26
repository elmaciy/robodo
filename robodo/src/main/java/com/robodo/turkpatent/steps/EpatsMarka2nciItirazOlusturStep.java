package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class EpatsMarka2nciItirazOlusturStep extends BaseEpatsStep {


	
	public EpatsMarka2nciItirazOlusturStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}

	
	@Override
	public void run() {
		
		dosyaLinkleriGuncelle(this.processInstanceStep.getProcessInstance());
		dosyaDurumGuncelle(EPATS_STATU_TAHAKKUK);
		
		sistemeGiris();
		islemSec();
		basvuruYap();
		dosyaBilgisiAra();
		dosyaBilgisiDogrulaDevamEt();
		itirazSahibiEkleDevamEt();
		itirazGerekceleriEkle();
		itirazaGerekceDosyaNumaralariEkleDevamEt();
		itirazaIliskibBilgileriEkleDevamEt();
		itirazaIliskinEkleriEkleDevamEt();
		hizmetDokumuDevamEt();
		//bu kisimlar acilacak...
		//onizlemeKontrolveTahakkukOlustur();
		//tahakkukNumarasiAl();
		//anaSayfayaDon();
		
		//gecici olarak ana sayfaya donuyoruz. 
		epatsMenu.vazgecVeSayfayaDon();
		epatsMenu.cikis();
		
		selenium.sleep(10L);
		//selenium.stopDriver();
	}


	


	



	

}
