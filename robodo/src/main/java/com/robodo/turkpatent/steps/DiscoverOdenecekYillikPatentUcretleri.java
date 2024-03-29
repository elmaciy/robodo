package com.robodo.turkpatent.steps;

import java.util.HashMap;
import java.util.List;

import com.robodo.model.Discoverable;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.DosyaResponse;
import com.robodo.turkpatent.apimodel.Rumuz;
import com.robodo.turkpatent.apimodel.RumuzEsleme;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

public class DiscoverOdenecekYillikPatentUcretleri extends BaseEpatsStep implements Discoverable {
 

	public DiscoverOdenecekYillikPatentUcretleri(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<ProcessInstance> discover(ProcessDefinition processDefinition) {
		//1: Yıllık Ücret Yenileme,  2: Tescil Sonuçlandırma, 3: Tam Marka Yenileme
		int islemAdimi=Integer.valueOf(runnerUtil.getEnvironmentParameter("PatentYenileme.islemAdimi")); 		

		RumuzEsleme rumuzEsleme =  getRumuzEslemeResponseByIslemAdimi(islemAdimi).getData().stream().filter(
				r->
					r.statu==1
					&& r.getTelefon()!=null && r.getTelefon().length()==10
					&& r.getEposta()!=null && HelperUtil.isValidEmailAddress(r.getEposta())
					&& isValidRumuz(r.getRumuz())
				).findAny().get();

		List<DosyaResponse> dosyalar = getRpaIslemdeDosyalarByIslemAdimi(islemAdimi);
		
		var instances = createEpatsInstances(
				processDefinition,
				dosyalar, 
				(dosya)->"%s.%s (%d)".formatted(processDefinition.getCode(), dosya.getBasvuruno(), dosya.getYil()),
				(dosya)->"%s (%d) dosyası için  Yıllık Ücret Ödeme".formatted(dosya.getBasvuruno(), dosya.getYil())
				);
		
		instances.stream().forEach(p->{
			HashMap<String, String> hmVars = HelperUtil.str2HashMap(p.getInstanceVariables());
			
			DosyaResponse dosya = json2Object(hmVars.get("dosyaResponse.JSON"), DosyaResponse.class);
			
			//hmVars.put("dosyaNumarasi", "2019/06601");
			hmVars.put("dosyaNumarasi", dosya.getBasvuruno());
			hmVars.put("bulusAdi", "TBD");
			hmVars.put("takipNumarasi", dosya.getReferansno());
		
			//bunlar process initial variables den alınabilir. 
			hmVars.put("basvuruTuru", "PATENT");
			//hmVars.put("islemGrubu", "Başvuru Sonrası İşlemler");
			hmVars.put("islemGrubu", "Sahip/Ücret/Diğer İşlemler");
			hmVars.put("islemAdi", "Yıllık Ücret Ödeme");
			

			
			hmVars.put("eposta", rumuzEsleme.getEposta());
			hmVars.put("telefonNumarasi", rumuzEsleme.getTelefon());
			
			hmVars.put("islemAdimi", String.valueOf(islemAdimi));
			
			p.setInstanceVariables(HelperUtil.hashMap2String(hmVars));
			p.setInitialInstanceVariables(p.getInstanceVariables());


		});
		
		return instances;
	
	}

	private boolean isValidRumuz(Rumuz rumuz) {
		if (rumuz==null) return false;
		if (rumuz.parametreturu==2) {
			return  rumuz.getStatu() ==1
					&& rumuz.getCcv()!=null && rumuz.getCcv().length()>=3
					&& rumuz.getKredikartino()!=null && rumuz.getKredikartino().strip().length()==16
					&& rumuz.getSonkullanimtarihi()!=null && rumuz.getSonkullanimtarihi().length()==10;
		} 
		if (rumuz.parametreturu==1) {
			return  rumuz.getStatu() ==1
					&& rumuz.getTckimlik()!=null && rumuz.getTckimlik().length()==11
					&& rumuz.getSifre()!=null;
		}
		
		return false;
	}



}
