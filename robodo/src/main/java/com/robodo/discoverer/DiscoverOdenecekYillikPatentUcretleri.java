package com.robodo.discoverer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.runner.RunnerUtil;

public class DiscoverOdenecekYillikPatentUcretleri extends BaseDiscoverer {

	public DiscoverOdenecekYillikPatentUcretleri(RunnerUtil runnerUtil) {
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
			instance.setRetryNo(0);
			instance.setStatus(ProcessInstance.STATUS_NEW);
			instance.setCurrentStepCode(ProcessInstance.BEGIN);
			instance.setProcessDefinition(processDefinition);
			instance.setSteps(new ArrayList<ProcessInstanceStep>());
			
			HashMap<String, String> hmVars=new HashMap<String, String>();
			hmVars.put("processInstance.code", instance.getCode());
			hmVars.put("dosyaNumarasi", "2019/06601");
			hmVars.put("takipNumarasi", "TTuu20029SSSa112");
			hmVars.put("basvuruTuru", "PATENT");
			hmVars.put("islemAdi", "Yıllık Ücret Ödeme");
			instance.setInstanceVariables(RunnerUtil.hashMap2String(hmVars));
			
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
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return instances;
	}

}
