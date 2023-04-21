package com.robodo.turkpatent.steps;

import java.util.HashMap;
import java.util.List;

import com.robodo.model.Discoverable;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.DosyaResponse;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

public class DiscoverMarka2nciItiraz extends BaseEpatsStep implements Discoverable {

	public DiscoverMarka2nciItiraz(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	@Override
	public List<ProcessInstance> discover(ProcessDefinition processDefinition) {
		
		//Yıllık Ücret Yenileme : 1, Tescil Sonuçlandırma: 2, Tam Marka Yenileme : 3
		//bu id ler degisebilir. Bu durumda asagidaki parametre ezilerek halledilir. 
		int islemAdimi=Integer.valueOf(runnerUtil.getEnvironmentParameter("Marka2nciItiraz.islemAdimi")); 		
			
		List<DosyaResponse> dosyalar = getTaslakDosyalarByIslemAdimi(islemAdimi);
		
		var instances =  createEpatsInstances(
				processDefinition,
				dosyalar, 
				(dosya)->"%s.%s".formatted(processDefinition.getCode(), dosya.getBasvuruno()),
				(dosya)->"%s dosyası için Marka 2nci İtiraz Başvurusu".formatted(dosya.getBasvuruno())
				);
		
		instances.stream().forEach(p->{
			HashMap<String, String> hmVars = HelperUtil.str2HashMap(p.getInstanceVariables());
			
			DosyaResponse dosya = json2Object(hmVars.get("dosyaResponse.JSON"), DosyaResponse.class);
			
			hmVars.put("dosyaNumarasi", dosya.getBasvuruno());
			hmVars.put("islemAdimi", String.valueOf(islemAdimi));
			
			p.setInstanceVariables(HelperUtil.hashMap2String(hmVars));
			p.setInitialInstanceVariables(p.getInstanceVariables());
		});
		
		return instances;
		

	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
