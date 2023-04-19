package com.robodo.turkpatent.discoverer;

import java.util.List;
import java.util.function.Predicate;

import com.robodo.model.Discoverable;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.DosyaResponse;
import com.robodo.turkpatent.steps.BaseEpatsStep;
import com.robodo.utils.RunnerUtil;

public class DiscoverMarka2nciItiraz extends BaseEpatsStep implements Discoverable {

	public DiscoverMarka2nciItiraz(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	@Override
	public List<ProcessInstance> discover(ProcessDefinition processDefinition) {
		
		int islemTuru=1; 		//Marka : 1 Patent:2  Tasarım:3
		int islemKategorisi=2; 	//Başvuru Öncesi : 1 Başvuru : 2
		int islemAdimi=2; 		//Yıllık Ücret Yenileme : 1, Tescil Sonuçlandırma: 2, Tam Marka Yenileme : 3
		int statu=BaseEpatsStep.EPATS_STATU_TASLAK;
		
		Predicate<DosyaResponse> filter=(p)->
			p.getIslemturu()==islemTuru
			&& p.getIslemkategorisi()==islemKategorisi
			&& p.getIslemadimi() ==islemAdimi
			&& p.getStatu() ==statu;
			
		List<DosyaResponse> dosyalar = getTaslakDosyalar(filter);
		
		return createEpatsInstances(
				processDefinition,
				dosyalar, 
				(dosya)->"%s.%s".formatted(processDefinition.getCode(), dosya.getBasvuruno()),
				(dosya)->"%s dosyası için Marka 2nci İtiraz Başvurusu".formatted(dosya.getBasvuruno())
				);

	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
