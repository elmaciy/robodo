package com.robodo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessDefinitionStep;
import com.robodo.repo.ProcessDefinitionRepo;

@SpringBootApplication
public class RobodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RobodoApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(ProcessDefinitionRepo processDefinitionRepo) {
		return (args) -> {

			
			ProcessDefinition processDef1=new ProcessDefinition();
			//processDef1.setId(1L);
			processDef1.setCode("PROCESS1");
			processDef1.setDescription("PROCESS FOR TEST");
			processDef1.setMaxRetryCount(1);
			processDef1.setMaxThreadCount(1);
			List<ProcessDefinitionStep> steps=new ArrayList<ProcessDefinitionStep>();			
			processDef1.setSteps(steps);
			processDef1.setSingleAtATime(true);
			processDef1.setDiscovererClass("DiscoverProcess1");
			
			
			
			
			ProcessDefinitionStep step1=new ProcessDefinitionStep();
			step1.setCode("STEP1");
			step1.setDescription("write to epats");
			step1.setOrderNo("01");
			step1.setSingleAtATime(true);
			step1.setCommands("runStepClass WriterSteps");
			step1.setProcessDefinition(processDef1);
			
			processDef1.getSteps().add(step1);

			
			processDefinitionRepo.save(processDef1);
			
			
			//--------------------------------------------
			
			
			
			ProcessDefinition processDef2=new ProcessDefinition();
			//processDef1.setId(1L);
			processDef2.setCode("PROCESS2");
			processDef2.setDescription("PROCESS FOR TEST 2");
			processDef2.setMaxRetryCount(3);
			List<ProcessDefinitionStep> steps2=new ArrayList<ProcessDefinitionStep>();			
			processDef2.setSteps(steps2);
			
			
			
			
			ProcessDefinitionStep step21=new ProcessDefinitionStep();
			//step1.setId(1L);
			step21.setCode("STEP2");
			step21.setDescription("STEP 1 of PROCESS2");
			step21.setOrderNo("01");
			step21.setSingleAtATime(true);
			step21.setCommands("commands");
			step21.setProcessDefinition(processDef2);
			
			
			
			ProcessDefinitionStep step22=new ProcessDefinitionStep();
			//step2.setId(2L);
			step22.setCode("STEP2");
			step22.setDescription("STEP2 of PROCESS2");
			step22.setOrderNo("02");
			step22.setSingleAtATime(true);
			step22.setCommands("commands");
			step22.setProcessDefinition(processDef2);
			
			processDef2.getSteps().add(step21);
			processDef2.getSteps().add(step22);
			
			processDefinitionRepo.save(processDef2);
			
			
		};

	}

}
