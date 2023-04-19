package com.robodo.turkpatent.discoverer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.robodo.model.Discoverable;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.DosyaListeleri;
import com.robodo.turkpatent.apimodel.DosyaResponse;
import com.robodo.turkpatent.steps.BaseEpatsStep;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

public class DiscoverMarka2nciItiraz extends BaseEpatsStep implements Discoverable {

	public DiscoverMarka2nciItiraz(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	}

	@Override
	public List<ProcessInstance> discover(ProcessDefinition processDefinition) {
		DosyaListeleri taslakDosyalar = getTaslakDosyalar();
		List<DosyaResponse> dosyalar = taslakDosyalar.getData();
		
		List<ProcessInstance> instances = new ArrayList<ProcessInstance>();
		int islemTuru=1; 		//Marka : 1 Patent:2  Tasarım:3
		int islemKategorisi=1; 	//Başvuru Öncesi : 1 Başvuru : 2
		int islemAdimi=1; 		//Yıllık Ücret Yenileme : 1, Tescil Sonuçlandırma: 2, Tam Marka Yenileme : 3
		
		for (DosyaResponse dosya : dosyalar) {
			//bu süreçle ilgili olmayan dosyaların filtrelenmesi
			if (dosya.getIslemturu()!=islemTuru || dosya.getIslemkategorisi()!=islemKategorisi) {
				continue;
			}
			
			System.out.println("dosya : basvuru No : %s, referans No : %s".formatted(dosya.getBasvuruno(), dosya.getReferansno()));
			String instanceKey="%s.%d".formatted(dosya.getBasvuruno(), dosya.getIslemturu());
			
			ProcessInstance instance =new ProcessInstance();
			instance=new ProcessInstance();
			instance.setCode(instanceKey);
			instance.setDescription(processDefinition.getDescription());
			instance.setCreated(LocalDateTime.now());
			instance.setStarted(LocalDateTime.now());
			instance.setFinished(null);
			instance.setAttemptNo(0);
			instance.setStatus(ProcessInstance.STATUS_NEW);
			instance.setProcessDefinitionId(processDefinition.getId());
			instance.setSteps(new ArrayList<ProcessInstanceStep>());
			
			HashMap<String, String> hmVars=new HashMap<String, String>();
			hmVars.put("processInstance.code", instance.getCode());
			hmVars.put("url", instanceKey);
			
			//this step must be included 
			createApprovalLinks(hmVars, instance.getCode());
			
			instance.setInstanceVariables(HelperUtil.hashMap2String(hmVars));
			instance.setInitialInstanceVariables(instance.getInstanceVariables());

			
			for (var definitedSteps : processDefinition.getSteps()) {
				ProcessInstanceStep instanceStep = new ProcessInstanceStep();
				instanceStep.setStepCode(definitedSteps.getCode());
				instanceStep.setProcessInstance(instance);
				instanceStep.setStatus(ProcessInstanceStep.STATUS_NEW);
				instanceStep.setCommands(definitedSteps.getCommands());
				instanceStep.setCreated(LocalDateTime.now());
				instanceStep.setOrderNo(definitedSteps.getOrderNo());

				instance.getSteps().add(instanceStep);
			}
			
			instances.add(instance);
		}

		return instances;
		
		/*
		List<ProcessInstance> instances = new ArrayList<ProcessInstance>();
		for (int i=0;i<1;i++) {
			ProcessInstance instance =new ProcessInstance();
			instance=new ProcessInstance();
			//instance.setCode(processDefinition.getCode()+"_"+System.currentTimeMillis());
			instance.setCode(processDefinition.getCode()+"_"+i);
			instance.setDescription(processDefinition.getDescription());
			instance.setCreated(LocalDateTime.now());
			instance.setStarted(LocalDateTime.now());
			instance.setFinished(null);
			instance.setAttemptNo(0);
			instance.setStatus(ProcessInstance.STATUS_NEW);
			instance.setProcessDefinitionId(processDefinition.getId());
			instance.setSteps(new ArrayList<ProcessInstanceStep>());
			
			HashMap<String, String> hmVars=new HashMap<String, String>();
			hmVars.put("processInstance.code", instance.getCode());
			hmVars.put("dosyaNumarasi", "2022/203652");
			hmVars.put("markaAdi", "bilal koçotomotiv ticari yedek parça");
			hmVars.put("takipNumarasi", "TKP_%s".formatted(String.valueOf(System.currentTimeMillis())));
			hmVars.put("basvuruTuru", "MARKA");
			hmVars.put("islemGrubu", "Üçüncü Kişi İşlemleri");
			hmVars.put("islemAdi", "Marka Yayıma İtirazın Yeniden İncelenmesi (YİDD)");
			hmVars.put("eposta", "legal@ankarapatent.com");
			hmVars.put("odemeTutari", HelperUtil.normalizeAmount("₺2.450,00"));
			hmVars.put("itirazSahibiAdi", "KOÇ HOLDİNG ANONİM ŞİRKETİ");
			hmVars.put("itirazSahibiKimlikNo", "5700020575");
			
			hmVars.put("Benzerlik/Karıştırılma İhtimali (6/1)", "YES");
			hmVars.put("Eskiye Dayalı Kullanım (6/3)", "YES");
			hmVars.put("Ortak/Garanti Markanın Yenilenmemesi (6/7)", "YES");
			hmVars.put("Kötü Niyet (6/9)", "YES");
			
			
			//hmVars.put("itirazaGerekceDosyaNumaralari", "T/00132, 2018/62877, 2021/059708");
			hmVars.put("itirazaGerekceDosyaNumaralari", "2019");
			
			hmVars.put("itirazaIliskinDosya", "C:\\projects\\robodo\\files\\itirazailiskindosya1.pdf");
			
			
			instance.setInstanceVariables(HelperUtil.hashMap2String(hmVars));
			instance.setInitialInstanceVariables(instance.getInstanceVariables());
			
			for (var definitedSteps : processDefinition.getSteps()) {
				ProcessInstanceStep instanceStep = new ProcessInstanceStep();
				instanceStep.setStepCode(definitedSteps.getCode());
				instanceStep.setProcessInstance(instance);
				instanceStep.setStatus(ProcessInstanceStep.STATUS_NEW);
				instanceStep.setCommands(definitedSteps.getCommands());
				instanceStep.setCreated(LocalDateTime.now());
				instanceStep.setOrderNo(definitedSteps.getOrderNo());
				instance.getSteps().add(instanceStep);
			}
			
			instances.add(instance);
		}
		
		return instances;
		*/
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
