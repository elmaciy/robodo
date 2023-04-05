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
			

			
			
			
			//----------------------------------------------------------------------

			
			ProcessDefinition yillikPatentUcretiProcess=new ProcessDefinition();
			yillikPatentUcretiProcess.setCode("PATENT_YILLIK_UCRET");
			yillikPatentUcretiProcess.setDescription("Yıllık patent ücreti ödeme süreci");
			yillikPatentUcretiProcess.setMaxRetryCount(1);
			yillikPatentUcretiProcess.setMaxThreadCount(2);
			yillikPatentUcretiProcess.setSteps(new ArrayList<ProcessDefinitionStep>());
			yillikPatentUcretiProcess.setSingleAtATime(false);
			yillikPatentUcretiProcess.setDiscovererClass("DiscoverOdenecekYillikPatentUcretleri");
			yillikPatentUcretiProcess.setActive(false);
			
			
			
			
			ProcessDefinitionStep stepPatentDosyaOku=new ProcessDefinitionStep();
			stepPatentDosyaOku.setCode("PATENT_DOSYASINI_OKU");
			stepPatentDosyaOku.setDescription("Ücreti ödenecek patent dosyasının okunması");
			stepPatentDosyaOku.setOrderNo("01");
			stepPatentDosyaOku.setSingleAtATime(false);
			stepPatentDosyaOku.setCommands("runStepClass YillikPatentUcretDosyasiOkuSteps");
			stepPatentDosyaOku.setProcessDefinition(yillikPatentUcretiProcess);
			

			ProcessDefinitionStep stepPatentTahakkukOlustur=new ProcessDefinitionStep();
			stepPatentTahakkukOlustur.setCode("PATENT_YILLIK_UCRET_TAHAKKUK");
			stepPatentTahakkukOlustur.setDescription("Patent yıllık ücreti tahakkuk oluşturma");
			stepPatentTahakkukOlustur.setOrderNo("02");
			stepPatentTahakkukOlustur.setSingleAtATime(true);
			stepPatentTahakkukOlustur.setCommands("runStepClass YillikPatentUcretiTahakkukOlustur");
			stepPatentTahakkukOlustur.setProcessDefinition(yillikPatentUcretiProcess);
			
			ProcessDefinitionStep stepOnay=new ProcessDefinitionStep();
			stepOnay.setCode("PATENT_ONAY_BEKLE");
			stepOnay.setDescription("Patent yıllık ücreti ödeme için onay bekle");
			stepOnay.setOrderNo("03");
			stepOnay.setSingleAtATime(true);
			stepOnay.setCommands("waitHumanInteraction TAHAKKUK_ONAY");
			stepOnay.setProcessDefinition(yillikPatentUcretiProcess);
			
			ProcessDefinitionStep stepPatentOde=new ProcessDefinitionStep();
			stepPatentOde.setCode("PATENT_TAHAKKUK_ODE");
			stepPatentOde.setDescription("Tahakkuk ödeme");
			stepPatentOde.setOrderNo("04");
			stepPatentOde.setSingleAtATime(false);
			stepPatentOde.setCommands("runStepClass GenelTahakkukOdeme");
			stepPatentOde.setProcessDefinition(yillikPatentUcretiProcess);
			
			
			ProcessDefinitionStep stepPatentDekontIsle=new ProcessDefinitionStep();
			stepPatentDekontIsle.setCode("PATENT_DEKONT_KAYDET");
			stepPatentDekontIsle.setDescription("Ödeme dekont bilgisini sisteme kaydet");
			stepPatentDekontIsle.setOrderNo("05");
			stepPatentDekontIsle.setSingleAtATime(false);
			stepPatentDekontIsle.setCommands("runStepClass GenelDekontKaydetSteps");
			stepPatentDekontIsle.setProcessDefinition(yillikPatentUcretiProcess);
			
			
			//yillikPatentUcreti.getSteps().add(stepDosyaOku);
			yillikPatentUcretiProcess.getSteps().add(stepPatentTahakkukOlustur);
			yillikPatentUcretiProcess.getSteps().add(stepOnay);
			yillikPatentUcretiProcess.getSteps().add(stepPatentOde);
			//yillikPatentUcreti.getSteps().add(stepPatentDekontIsle);

			
			if (processDefinitionRepo.findByCode(yillikPatentUcretiProcess.getCode()).isEmpty()) {
				processDefinitionRepo.save(yillikPatentUcretiProcess);
			}
			
			
			
			//--------------------------------------------
			
			
			ProcessDefinition markaYenilemeProcess=new ProcessDefinition();
			markaYenilemeProcess.setCode("MARKA_YENILEME");
			markaYenilemeProcess.setDescription("Marka Yenileme Süreci");
			markaYenilemeProcess.setMaxRetryCount(1);
			markaYenilemeProcess.setMaxThreadCount(2);
			markaYenilemeProcess.setSteps(new ArrayList<ProcessDefinitionStep>());
			markaYenilemeProcess.setSingleAtATime(true);
			markaYenilemeProcess.setDiscovererClass("DiscoverOdenecekMarkaYenilemeUcretleri");
			markaYenilemeProcess.setActive(true);
			
			ProcessDefinitionStep stepMarkaYenilemeDosyaOku=new ProcessDefinitionStep();
			stepMarkaYenilemeDosyaOku.setCode("MARKA_YENILEME_DOSYASINI_OKU");
			stepMarkaYenilemeDosyaOku.setDescription("Ücreti ödenecek marka yenileme dosyasının okunması");
			stepMarkaYenilemeDosyaOku.setOrderNo("01");
			stepMarkaYenilemeDosyaOku.setSingleAtATime(false);
			stepMarkaYenilemeDosyaOku.setCommands("runStepClass MarkaYenilemeDosyasiOkuSteps");
			stepMarkaYenilemeDosyaOku.setProcessDefinition(markaYenilemeProcess);

			ProcessDefinitionStep stepMarkeYenilemeTahakkukOlustur=new ProcessDefinitionStep();
			stepMarkeYenilemeTahakkukOlustur.setCode("MARKA_YENILEME_TAHAKKUK");
			stepMarkeYenilemeTahakkukOlustur.setDescription("Marka yenileme tahakkuk oluşturma");
			stepMarkeYenilemeTahakkukOlustur.setOrderNo("02");
			stepMarkeYenilemeTahakkukOlustur.setSingleAtATime(false);
			stepMarkeYenilemeTahakkukOlustur.setCommands("runStepClass MarkaYenilemeTahakkukOlustur");
			stepMarkeYenilemeTahakkukOlustur.setProcessDefinition(markaYenilemeProcess);
			
			ProcessDefinitionStep steparkeYenilemeOnay=new ProcessDefinitionStep();
			steparkeYenilemeOnay.setCode("MARKA_ONAY_BEKLE");
			steparkeYenilemeOnay.setDescription("Marke yenileme tahakkuk ödeme için onay bekle");
			steparkeYenilemeOnay.setOrderNo("03");
			steparkeYenilemeOnay.setSingleAtATime(true);
			steparkeYenilemeOnay.setCommands("waitHumanInteraction TAHAKKUK_ONAY");
			steparkeYenilemeOnay.setProcessDefinition(markaYenilemeProcess);
			
			ProcessDefinitionStep stepMarkaOde=new ProcessDefinitionStep();
			stepMarkaOde.setCode("MARKA_TAHAKKUK_ODE");
			stepMarkaOde.setDescription("Tahakkuk ödeme");
			stepMarkaOde.setOrderNo("04");
			stepMarkaOde.setSingleAtATime(false);
			stepMarkaOde.setCommands("runStepClass GenelTahakkukOdeme");
			stepMarkaOde.setProcessDefinition(markaYenilemeProcess);
			
			ProcessDefinitionStep stepMarkaDekontIsle=new ProcessDefinitionStep();
			stepMarkaDekontIsle.setCode("MARKA_DEKONT_KAYDET");
			stepMarkaDekontIsle.setDescription("Ödeme dekont bilgisini sisteme kaydet");
			stepMarkaDekontIsle.setOrderNo("05");
			stepMarkaDekontIsle.setSingleAtATime(false);
			stepMarkaDekontIsle.setCommands("runStepClass GenelDekontKaydetSteps");
			stepMarkaDekontIsle.setProcessDefinition(markaYenilemeProcess);
			
			//markaYenilemeProcess.getSteps().add(stepMarkaYenilemeDosyaOku);
			markaYenilemeProcess.getSteps().add(stepMarkeYenilemeTahakkukOlustur);
			markaYenilemeProcess.getSteps().add(steparkeYenilemeOnay);
			markaYenilemeProcess.getSteps().add(stepMarkaOde);
			//markaYenilemeProcess.getSteps().add(stepMarkaDekontIsle);

			if (processDefinitionRepo.findByCode(markaYenilemeProcess.getCode()).isEmpty()) {
				processDefinitionRepo.save(markaYenilemeProcess);
			}
			
			//--------------------------------------------
			
			ProcessDefinition getTextAndSearchOnGoogle=new ProcessDefinition();
			//processDef1.setId(1L);
			getTextAndSearchOnGoogle.setCode("GOOGLESEARCH");
			getTextAndSearchOnGoogle.setDescription("Get text from somewhere and search on google");
			getTextAndSearchOnGoogle.setMaxRetryCount(1);
			getTextAndSearchOnGoogle.setMaxThreadCount(1);
			getTextAndSearchOnGoogle.setSteps(new ArrayList<ProcessDefinitionStep>());
			getTextAndSearchOnGoogle.setSingleAtATime(true);
			getTextAndSearchOnGoogle.setDiscovererClass("DiscoverProcessGooggleSearch");
			getTextAndSearchOnGoogle.setActive(false);
			
			
			
			
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
			emailForYillikPatentUcreti.setCode("TAHAKKUK_ONAY");
			emailForYillikPatentUcreti.setToAddress("elmaciy@hotmail.com");
			emailForYillikPatentUcreti.setSubject("Onayınız bekleniyor. Dosya No ${dosyaNumarasi}");
			emailForYillikPatentUcreti.setBody(
					"Sayın ilgili;"
					+ "<br>"
					+ "<br>"
					+ " <b>${dosyaNumarasi}</b> numaralı dosyanın, <b>${islemAdi}</b> işlemi için, <b>${tahakkukNo}</b> nolu tahakkuk kaydı oluşturulmuştur. "
					+ "<br>"
					+ "Onayınızın ardından <b>${onizleme.odenecek.genelToplam}</b> tutarındaki ödemesi gerçekleştirilecektir."
					+ "<br>"
					+ "İyi çalışmalar"
					+ "<hr>"
					+ "<center>"
					+ "<a href=\"http://localhost:8080/approve/${processInstance.code}/APPROVE/EMAIL\"><b><font color=green>[+ Onayla]</font></b></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/approve/${processInstance.code}/DECLINE/EMAIL\"><font color=red>[-Reddet]</font></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/approve/${processInstance.code}/VIEW/EMAIL\"><font color=blue>[İncele]</font></a> "
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
					+ "<a href=\"http://localhost:8080/approve/${processInstance.code}/APPROVE/EMAIL\"><b><font color=green>[+ Onayla]</font></b></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/approve/${processInstance.code}/DECLINE/EMAIL\"><font color=red>[-Reddet]</font></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/approve/${processInstance.code}/VIEW/EMAIL\"><font color=blue>[İncele]</font></a> "
					+ "</center>"
					+ "<hr>"
					+ " ");
			
			if (emailTemplateRepo.findByCode(emailForGoogleSearch.getCode()).isEmpty()) {
				emailTemplateRepo.save(emailForGoogleSearch);
			}
			
		};

	}

}
