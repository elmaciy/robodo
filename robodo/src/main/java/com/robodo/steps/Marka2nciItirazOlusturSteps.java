package com.robodo.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class Marka2nciItirazOlusturSteps extends BaseEpatsSteps {


	
	public Marka2nciItirazOlusturSteps(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	
	}

	
	@Override
	public void run() {
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
