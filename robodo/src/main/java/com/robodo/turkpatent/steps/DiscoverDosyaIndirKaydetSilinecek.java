package com.robodo.turkpatent.steps;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.robodo.model.Discoverable;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

public class DiscoverDosyaIndirKaydetSilinecek extends BaseEpatsStep implements Discoverable {

	
	public DiscoverDosyaIndirKaydetSilinecek(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<ProcessInstance> discover(ProcessDefinition processDefinition) {
		List<ProcessInstance> instances = new ArrayList<ProcessInstance>();
		
		int islemAdimi=Integer.valueOf(runnerUtil.getEnvironmentParameter("PatentYenileme.islemAdimi"));
		
		String tahakkukNo="2235369";
		ProcessInstance instance =new ProcessInstance();
		instance=new ProcessInstance();
		//instance.setCode(processDefinition.getCode()+"_"+System.currentTimeMillis());
		instance.setCode(processDefinition.getCode()+"_"+tahakkukNo);
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
		hmVars.put("tahakkukNo", tahakkukNo);
		hmVars.put("dosyaNumarasi", "2017/09540");
		hmVars.put("islemAdimi", String.valueOf(islemAdimi));
		
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
		
		return instances;
	}

}
