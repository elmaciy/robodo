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
			yillikPatentUcreti.setActive(false);
			
			
			
			
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

			
			if (processDefinitionRepo.findByCode(yillikPatentUcreti.getCode()).isEmpty()) {
				processDefinitionRepo.save(yillikPatentUcreti);
			}
			
			
			
			//--------------------------------------------
			
			
			
			ProcessDefinition getTextAndSearchOnGoogle=new ProcessDefinition();
			//processDef1.setId(1L);
			getTextAndSearchOnGoogle.setCode("GOOGLESEARCH");
			getTextAndSearchOnGoogle.setDescription("Get text from somewhere and search on google");
			getTextAndSearchOnGoogle.setMaxRetryCount(1);
			getTextAndSearchOnGoogle.setMaxThreadCount(1);
			List<ProcessDefinitionStep> steps2=new ArrayList<ProcessDefinitionStep>();			
			getTextAndSearchOnGoogle.setSteps(steps2);
			getTextAndSearchOnGoogle.setSingleAtATime(true);
			getTextAndSearchOnGoogle.setDiscovererClass("DiscoverProcessGooggleSearch");
			getTextAndSearchOnGoogle.setActive(true);
			
			
			
			
			ProcessDefinitionStep googleWaitApproval=new ProcessDefinitionStep();
			googleWaitApproval.setCode("WAIT_APPROVAL");
			googleWaitApproval.setDescription("Wait for approval");
			googleWaitApproval.setOrderNo("01");
			googleWaitApproval.setSingleAtATime(true);
			googleWaitApproval.setCommands("waitHumanInteraction GOOGLE");
			googleWaitApproval.setProcessDefinition(getTextAndSearchOnGoogle);
			
			
			
			ProcessDefinitionStep googleSearchStep=new ProcessDefinitionStep();
			googleSearchStep.setCode("DOSEARCH");
			googleSearchStep.setDescription("Search keyword");
			googleSearchStep.setOrderNo("02");
			googleSearchStep.setSingleAtATime(true);
			googleSearchStep.setCommands("runStepClass DummyGoogleSearchByKeywordSteps");
			googleSearchStep.setProcessDefinition(getTextAndSearchOnGoogle);
			
			getTextAndSearchOnGoogle.getSteps().add(googleWaitApproval);
			getTextAndSearchOnGoogle.getSteps().add(googleSearchStep);
			
			
			if (processDefinitionRepo.findByCode(getTextAndSearchOnGoogle.getCode()).isEmpty()) {
				processDefinitionRepo.save(getTextAndSearchOnGoogle);
			}
			
			
			//---------------------------------------------------
			EmailTemplate emailForYillikPatentUcreti=new EmailTemplate();
			emailForYillikPatentUcreti.setCode("YILLIK_UCRET_ONAY");
			emailForYillikPatentUcreti.setToAddress("elmaciy@hotmail.com,y.elmaci@astoundcommerce.com");
			emailForYillikPatentUcreti.setSubject("Onayınız bekleniyor. Dosya No ${dosyaNumarasi}");
			emailForYillikPatentUcreti.setBody(
					"Sayın ilgili;"
					+ "<br>"
					+ "<br>"
					+ " ${dosyaNumarasi} numaralı dosyanın ${tahakkukNo} nolu tahakkuk kaydı oluşturulmuştur. "
					+ "<br>"
					+ "Onayınızın ardından ${onizleme.odenecek.genelToplam} tutarındaki ödemesi gerçekleştirilecektir."
					+ "<br>"
					+ "İyi çalışmalar"
					+ "<hr>"
					+ "<center>"
					+ "<a href=\"http://localhost:8080/approve?instanceId=${processInstance.code}&action=APPROVE\"><b><font color=green>[+ Onayla]</font></b></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/approve?instanceId=${processInstance.code}&action=DECLINE\"><font color=red>[-Reddet]</font></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/approve?instanceId=${processInstance.code}&action=VIEW\"><font color=blue>[İncele]</font></a> "
					+ "</center>"
					+ "<hr>"
					+ " ");
			
			if (emailTemplateRepo.findByCode(emailForYillikPatentUcreti.getCode()).isEmpty()) {
				emailTemplateRepo.save(emailForYillikPatentUcreti);
			}
			
			
			
			//---------------------------------------------------
			EmailTemplate emailForGoogleSearch=new EmailTemplate();
			emailForGoogleSearch.setCode("GOOGLE");
			emailForGoogleSearch.setToAddress("elmaciy@hotmail.com");
			emailForGoogleSearch.setSubject("Onayınız bekleniyor. Aranacak Kelime : ${keyword}");
			emailForGoogleSearch.setBody(
					"Sayın ilgili;"
					+ "<br>"
					+ "<br>"
					+ " ${keyword} kelimesi, onayınız sonrası aranacaktır "
					+ "<br>"
					+ "İyi çalışmalar"
					+ "<hr>"
					+ "<center>"
					+ "<a href=\"http://localhost:8080/approve?instanceId=${processInstance.code}&action=APPROVE\"><b><font color=green>[+ Onayla]</font></b></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/approve?instanceId=${processInstance.code}&action=DECLINE\"><font color=red>[-Reddet]</font></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/approve?instanceId=${processInstance.code}&action=VIEW\"><font color=blue>[İncele]</font></a> "
					+ "</center>"
					+ "<hr>"
					+ " ");
			
			if (emailTemplateRepo.findByCode(emailForGoogleSearch.getCode()).isEmpty()) {
				emailTemplateRepo.save(emailForGoogleSearch);
			}
			
		};

	}

}
