package com.robodo.turkpatent.discoverer;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import com.robodo.model.Discoverable;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.DosyaResponse;
import com.robodo.turkpatent.steps.BaseEpatsStep;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

public class DiscoverOdenecekYillikPatentUcretleri extends BaseEpatsStep implements Discoverable {
 

	public DiscoverOdenecekYillikPatentUcretleri(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<ProcessInstance> discover(ProcessDefinition processDefinition) {
		
		//Yıllık Ücret Yenileme : 1, Tescil Sonuçlandırma: 2, Tam Marka Yenileme : 3
		int islemAdimi=Integer.valueOf(runnerUtil.getEnvironmentParameter("PatentYenileme.islemAdimi")); 		

		List<DosyaResponse> dosyalar = getTaslakDosyalarByIslemAdimi(islemAdimi);
		
		var instances = createEpatsInstances(
				processDefinition,
				dosyalar, 
				(dosya)->"%s.%s".formatted(processDefinition.getCode(), dosya.getBasvuruno()),
				(dosya)->"%s dosyası için Marka 2nci İtiraz Başvurusu".formatted(dosya.getBasvuruno())
				);
		
		instances.stream().forEach(p->{
			HashMap<String, String> hmVars = HelperUtil.String2HashMap(p.getInstanceVariables());
			
			DosyaResponse dosya = json2Object(hmVars.get("dosyaResponse.JSON"), DosyaResponse.class);
			
			//hmVars.put("dosyaNumarasi", "2019/06601");
			hmVars.put("dosyaNumarasi", dosya.getBasvuruno());
			//hmVars.put("bulusAdi", "HİYALURONİK ASİT/KİTOSAN/KARBOKSİMETİL SELÜLOZ İÇEREN BİYOUYUMLU, BİYOBOZUNUR VE BİYOEMİLEBİLİR BİR ADEZYON MEMBRAN VE ÜRETİM YÖNTEMİ");
			hmVars.put("bulusAdi", "TBD");
			hmVars.put("takipNumarasi", dosya.getReferansno());
			hmVars.put("basvuruTuru", "PATENT");
			hmVars.put("islemGrubu", "Başvuru Sonrası İşlemler");
			hmVars.put("islemAdi", "Yıllık Ücret Ödeme");
			hmVars.put("eposta", getRumuzEmailByIslemAdimi(islemAdimi));
			hmVars.put("telefonNumarasi", getRumuzTelefonByIslemAdimi(islemAdimi));
			
			
			hmVars.put("islemAdimi", String.valueOf(islemAdimi));
			
			p.setInstanceVariables(HelperUtil.hashMap2String(hmVars));
			p.setInitialInstanceVariables(p.getInstanceVariables());
		});
		
		return instances;
	
	}



}
