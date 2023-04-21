package com.robodo.turkpatent.discoverer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.robodo.model.Discoverable;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.DosyaResponse;
import com.robodo.turkpatent.steps.BaseEpatsStep;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

public class DiscoverOdenecekMarkaYenilemeUcretleri extends BaseEpatsStep implements Discoverable {

	
	public DiscoverOdenecekMarkaYenilemeUcretleri(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<ProcessInstance> discover(ProcessDefinition processDefinition) {		
		//Yıllık Ücret Yenileme : 1, Tescil Sonuçlandırma: 2, Tam Marka Yenileme : 3
		int islemAdimi=Integer.valueOf(runnerUtil.getEnvironmentParameter("MarkaYenileme.islemAdimi")); 		

		List<DosyaResponse> dosyalar = getTaslakDosyalarByIslemAdimi(islemAdimi);
		
		var instances = createEpatsInstances(
				processDefinition,
				dosyalar, 
				(dosya)->"%s.%s".formatted(processDefinition.getCode(), dosya.getBasvuruno()),
				(dosya)->"%s dosyası için Marka 2nci İtiraz Başvurusu".formatted(dosya.getBasvuruno())
				);
		
		instances.stream().forEach(p->{
			HashMap<String, String> hmVars = HelperUtil.str2HashMap(p.getInstanceVariables());
			
			DosyaResponse dosya = json2Object(hmVars.get("dosyaResponse.JSON"), DosyaResponse.class);
			

			///yeni
			//hmVars.put("dosyaNumarasi", "2013/62638");
			hmVars.put("dosyaNumarasi", dosya.getBasvuruno());
			hmVars.put("takipNumarasi", dosya.getReferansno());
			hmVars.put("basvuruTuru", "MARKA");
			hmVars.put("islemGrubu", "Başvuru Sonrası İşlemler");
			hmVars.put("islemAdi", "Marka Yenileme");
			hmVars.put("eposta", getRumuzEmailByIslemAdimi(islemAdimi));
			hmVars.put("telefonNumarasi", getRumuzTelefonByIslemAdimi(islemAdimi));
			
			hmVars.put("talepTuru", "Tam");
			
			hmVars.put("islemAdimi", String.valueOf(islemAdimi));
			
			p.setInstanceVariables(HelperUtil.hashMap2String(hmVars));
			p.setInitialInstanceVariables(p.getInstanceVariables());
		});
		
		return instances;
		
	}

	

}
