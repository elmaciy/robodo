package com.robodo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.robodo.model.EmailTemplate;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessDefinitionStep;
import com.robodo.repo.EmailTemplateRepo;
import com.robodo.repo.ProcessDefinitionRepo;

@SpringBootApplication
@EnableScheduling
public class RobodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RobodoApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(ProcessDefinitionRepo processDefinitionRepo, EmailTemplateRepo emailTemplateRepo) {
		return (args) -> {

			
			ProcessDefinition yillikPatentUcreti=new ProcessDefinition();
			//processDef1.setId(1L);
			yillikPatentUcreti.setCode("PATENT_YILLIK_UCRET");
			yillikPatentUcreti.setDescription("Yıllık patent ücreti ödeme süreci");
			yillikPatentUcreti.setMaxRetryCount(1);
			yillikPatentUcreti.setMaxThreadCount(2);
			List<ProcessDefinitionStep> steps=new ArrayList<ProcessDefinitionStep>();			
			yillikPatentUcreti.setSteps(steps);
			yillikPatentUcreti.setSingleAtATime(true);
			yillikPatentUcreti.setDiscovererClass("DiscoverOdenecekYillikPatentUcretleri");
			yillikPatentUcreti.setActive(true);
			
			
			
			
			ProcessDefinitionStep stepDosyaOku=new ProcessDefinitionStep();
			stepDosyaOku.setCode("PATENT_DOSYASINI_OKU");
			stepDosyaOku.setDescription("Ücreti ödenecek patent dosyasının okunması");
			stepDosyaOku.setOrderNo("01");
			stepDosyaOku.setSingleAtATime(false);
			stepDosyaOku.setCommands("runStepClass YillikPatentUcretDosyasiOkuSteps");
			stepDosyaOku.setProcessDefinition(yillikPatentUcreti);
			

			ProcessDefinitionStep stepTahakkukOlustur=new ProcessDefinitionStep();
			stepTahakkukOlustur.setCode("PATENT_YILLIK_UCRET_TAHAKKUK");
			stepTahakkukOlustur.setDescription("Patent yıllık ücreti tahakkuk oluşturma");
			stepTahakkukOlustur.setOrderNo("02");
			stepTahakkukOlustur.setSingleAtATime(true);
			stepTahakkukOlustur.setCommands("runStepClass YillikPatentUcretiTahakkukOlustur");
			stepTahakkukOlustur.setProcessDefinition(yillikPatentUcreti);
			
			ProcessDefinitionStep stepOnay=new ProcessDefinitionStep();
			stepOnay.setCode("PATENT_ONAY_BEKLE");
			stepOnay.setDescription("Patent yıllık ücreti ödeme için onay bekle");
			stepOnay.setOrderNo("03");
			stepOnay.setSingleAtATime(true);
			stepOnay.setCommands("waitHumanInteraction YILLIK_UCRET_ONAY");
			stepOnay.setProcessDefinition(yillikPatentUcreti);
			
			
			ProcessDefinitionStep stepOde=new ProcessDefinitionStep();
			stepOde.setCode("TAHAKKUK_ODE");
			stepOde.setDescription("Tahakkuk ödeme");
			stepOde.setOrderNo("04");
			stepOde.setSingleAtATime(false);
			stepOde.setCommands("runStepClass GenelTahakkukOdeme");
			stepOde.setProcessDefinition(yillikPatentUcreti);
			
			
			ProcessDefinitionStep stepDekontIsle=new ProcessDefinitionStep();
			stepDekontIsle.setCode("DEKONT_KAYDET");
			stepDekontIsle.setDescription("Ödeme dekont bilgisini sisteme kaydet");
			stepDekontIsle.setOrderNo("05");
			stepDekontIsle.setSingleAtATime(false);
			stepDekontIsle.setCommands("runStepClass GenelDekontKaydetSteps");
			stepDekontIsle.setProcessDefinition(yillikPatentUcreti);
			
			//yillikPatentUcreti.getSteps().add(stepDosyaOku);
			yillikPatentUcreti.getSteps().add(stepTahakkukOlustur);
			yillikPatentUcreti.getSteps().add(stepOnay);
			yillikPatentUcreti.getSteps().add(stepOde);
			//yillikPatentUcreti.getSteps().add(stepDekontIsle);

			
			processDefinitionRepo.save(yillikPatentUcreti);
			
			
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
			
			//---------------------------------------------------
			EmailTemplate email=new EmailTemplate();
			email.setCode("YILLIK_UCRET_ONAY");
			email.setToAddress("elmaciy@hotmail.com,y.elmaci@astoundcommerce.com");
			email.setSubject("Onayınız bekleniyor. Dosya No ${dosyaNumarasi}");
			email.setBody(
					"Sayın ilgili;"
					+ "<br>"
					+ "<br>"
					+ " ${dosyaNumarasi} numaralı dosyanın ${tahakkukNo} nolu tahakkuk kaydı oluşturulmuştur. "
					+ "<br>"
					+ "Onayınızın ardından ${onizleme.odenecek.genelToplam} tutarındaki ödemesi gerçekleştirilecektir."
					+ "<br>"
					+ "İyi çalışmalar"
					+ "<hr>"
					+ "<a href=\"http://localhost:8080/processses?onay=Y&instanceid=${instanceId}\"><b><font color=green>[+ Onayla]</font></b></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/processses?onay=N&instanceid=${instanceId}\"><font color=red>[-Reddet]</font></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/processses?onay=V&instanceid=${instanceId}\"><font color=blue>[İncele]</font></a> "
					+ "<hr>"
					+ " ");
			emailTemplateRepo.save(email);
			
		};

	}

}
