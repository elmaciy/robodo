package com.robodo.turkpatent.steps;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.RunnerUtil;

public class YillikPatentUcretDosyasiOkuStep extends ApiERPBaseStep {

	public YillikPatentUcretDosyasiOkuStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	@Override
	public void run() {
		String lnkKontrol=getVariable("LINK.VIEW");
		String lnkOnay=getVariable("LINK.APPROVE");
		String lnkRed=getVariable("LINK.DECLINE");
		
		String tahakkukNo=getVariable("tahakkukNo");
		String dekontNo=getVariable("dekontNo");

		int id=1;
		
		dosyaLinkleriGuncelle(id, lnkKontrol, lnkOnay, lnkRed);
		dosyaDurumGuncelle(id, EPATS_STATU_ISLEMDE);
		
		dosyaLTahakkukNoGuncelle(id, tahakkukNo);
		dosyaLDekontNoGuncelle(id, dekontNo);

	}

	

}
