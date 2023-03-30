package com.robodo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessDefinitionStep;
import com.robodo.repo.ProcessDefinitionRepo;

@SpringBootApplication
@EnableScheduling
public class RobodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RobodoApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(ProcessDefinitionRepo processDefinitionRepo) {
		return (args) -> {

			
			ProcessDefinition processDef1=new ProcessDefinition();
			//processDef1.setId(1L);
			processDef1.setCode("PATENT_YILLIK_UCRET");
			processDef1.setDescription("Yıllık patent ücreti ödeme süreci");
			processDef1.setMaxRetryCount(1);
			processDef1.setMaxThreadCount(1);
			List<ProcessDefinitionStep> steps=new ArrayList<ProcessDefinitionStep>();			
			processDef1.setSteps(steps);
			processDef1.setSingleAtATime(true);
			processDef1.setDiscovererClass("DiscoverOdenecekYillikPatentUcretleri");
			processDef1.setActive(true);
			
			
			
			
			ProcessDefinitionStep step1=new ProcessDefinitionStep();
			step1.setCode("PATENT_DOSYASINI_OKU");
			step1.setDescription("Ücreti ödenecek patent dosyasının okunması");
			step1.setOrderNo("01");
			step1.setSingleAtATime(false);
			step1.setCommands("runStepClass YillikPatentUcretDosyasiOkuSteps");
			step1.setProcessDefinition(processDef1);
			

			ProcessDefinitionStep step2=new ProcessDefinitionStep();
			step2.setCode("PATENT_YILLIK_UCRET_TAHAKKUK");
			step2.setDescription("Patent yıllık ücreti tahakkuk oluşturma");
			step2.setOrderNo("02");
			step2.setSingleAtATime(true);
			step2.setCommands("runStepClass YillikPatentUcretiTahakkukOlustur");
			step2.setProcessDefinition(processDef1);
			
			ProcessDefinitionStep step3=new ProcessDefinitionStep();
			step3.setCode("TAHAKKUK_ODE");
			step3.setDescription("Tahakkuk ödeme");
			step3.setOrderNo("03");
			step3.setSingleAtATime(false);
			step3.setCommands("runStepClass GenelTahakkukOdeme");
			step3.setProcessDefinition(processDef1);
			
			
			ProcessDefinitionStep step4=new ProcessDefinitionStep();
			step4.setCode("DEKONT_KAYDET");
			step4.setDescription("Ödeme dekont bilgisini sisteme kaydet");
			step4.setOrderNo("04");
			step4.setSingleAtATime(false);
			step4.setCommands("runStepClass GenelDekontKaydetSteps");
			step4.setProcessDefinition(processDef1);
			
			processDef1.getSteps().add(step1);
			processDef1.getSteps().add(step2);
			processDef1.getSteps().add(step3);
			//processDef1.getSteps().add(step4);

			
			processDefinitionRepo.save(processDef1);
			
			
			//--------------------------------------------
			
			
			
			ProcessDefinition processDef2=new ProcessDefinition();
			//processDef1.setId(1L);
			processDef2.setCode("PROCESS2");
			processDef2.setDescription("PROCESS FOR TEST 2");
			processDef2.setMaxRetryCount(1);
			processDef2.setMaxThreadCount(1);
			List<ProcessDefinitionStep> steps2=new ArrayList<ProcessDefinitionStep>();			
			processDef2.setSteps(steps2);
			processDef2.setSingleAtATime(true);
			processDef2.setDiscovererClass("DiscoverProcess2");
			processDef2.setActive(false);
			
			
			
			
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
