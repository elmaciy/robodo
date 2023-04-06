package com.robodo.discoverer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

public class DiscoverOdenecekMarkaYenilemeUcretleri extends BaseDiscoverer {

	public DiscoverOdenecekMarkaYenilemeUcretleri(RunnerUtil runnerUtil) {
		super(runnerUtil);
	}

	@Override
	public List<ProcessInstance> discover(ProcessDefinition processDefinition) {
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
			instance.setCurrentStepCode(null);
			instance.setProcessDefinition(processDefinition);
			instance.setSteps(new ArrayList<ProcessInstanceStep>());
			
			HashMap<String, String> hmVars=new HashMap<String, String>();
			hmVars.put("processInstance.code", instance.getCode());
			hmVars.put("dosyaNumarasi", "2013/62638");
			hmVars.put("markaAdi", "patrades");
			hmVars.put("takipNumarasi", "TKP_%s".formatted(String.valueOf(System.currentTimeMillis())));
			hmVars.put("basvuruTuru", "MARKA");
			hmVars.put("islemGrubu", "Başvuru Sonrası İşlemler");
			hmVars.put("islemAdi", "Marka Yenileme");
			hmVars.put("eposta", "ipmaintenance.epats@ankarapatent.com");
			hmVars.put("odemeTutari", HelperUtil.normalizeAmount("₺2.450,00"));
			hmVars.put("talepTuru", "Tam");
			instance.setInstanceVariables(HelperUtil.hashMap2String(hmVars));
			
			for (var definitedSteps : processDefinition.getSteps()) {
				ProcessInstanceStep instanceStep = new ProcessInstanceStep();
				instanceStep.setStepCode(definitedSteps.getCode());
				instanceStep.setProcessInstance(instance);
				instanceStep.setStatus(ProcessInstanceStep.STATUS_NEW);
				instanceStep.setCommands(definitedSteps.getCommands());
				instanceStep.setCreated(LocalDateTime.now());
				instanceStep.setOrderNo(definitedSteps.getOrderNo());
				instanceStep.setFiles(new ArrayList<ProcessInstanceStepFile>());

				instance.getSteps().add(instanceStep);
			}
			
			instances.add(instance);
		}
		
		return instances;
	}

}
