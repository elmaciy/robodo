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
import com.robodo.model.User;
import com.robodo.model.UserRole;
import com.robodo.repo.EmailTemplateRepo;
import com.robodo.repo.ProcessDefinitionRepo;
import com.robodo.repo.UserRepo;
import com.robodo.repo.UserRoleRepo;
import com.robodo.utils.HelperUtil;

@SpringBootApplication
@EnableScheduling
public class RobodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RobodoApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(ProcessDefinitionRepo processDefinitionRepo, EmailTemplateRepo emailTemplateRepo, UserRepo userRepo, UserRoleRepo userRoleRepo) {
		return (args) -> {
			

			
			User admin=new User();
			admin.setEmail("admin@hotmail.com");
			admin.setUsername("admin");
			admin.setPassword(HelperUtil.encrypt("admin123"));
			admin.setFullname("Administrator");
			admin.setValid(true);
			
			
			if (userRepo.findByUsername(admin.getUsername()).isEmpty()) {
				User savedUser = userRepo.save(admin);
				UserRole roleAdmin=new UserRole(savedUser.getId(), UserRole.ROLE_ADMIN);
				UserRole roleUser=new UserRole(savedUser.getId(), UserRole.ROLE_USER);
				
				userRoleRepo.save(roleAdmin);
				userRoleRepo.save(roleUser);
			}
			
			User user=new User();
			user.setEmail("elmaciy@hotmail.com");
			user.setUsername("elmaciy");
			user.setPassword(HelperUtil.encrypt("123"));
			user.setFullname("Yıldıray Elmacı");
			user.setValid(true);

			
			if (userRepo.findByUsername(user.getUsername()).isEmpty()) {
				User savedUser = userRepo.save(user);
				UserRole roleUser=new UserRole(savedUser.getId(), UserRole.ROLE_USER);
				
				userRoleRepo.save(roleUser);
			}
			
			
			//----------------------------------------------------------------------

			
			ProcessDefinition yillikPatentUcretiProcess=new ProcessDefinition();
			yillikPatentUcretiProcess.setCode("PATENT_YILLIK_UCRET");
			yillikPatentUcretiProcess.setDescription("Yıllık patent ücreti ödeme süreci");
			yillikPatentUcretiProcess.setMaxAttemptCount(1);
			yillikPatentUcretiProcess.setMaxThreadCount(2);
			yillikPatentUcretiProcess.setSteps(new ArrayList<ProcessDefinitionStep>());
			yillikPatentUcretiProcess.setDiscovererClass("DiscoverOdenecekYillikPatentUcretleri");
			yillikPatentUcretiProcess.setActive(false);
			
			
			
			
			ProcessDefinitionStep stepPatentDosyaOku=new ProcessDefinitionStep();
			stepPatentDosyaOku.setCode("PATENT_DOSYASINI_OKU");
			stepPatentDosyaOku.setDescription("Ücreti ödenecek patent dosyasının okunması");
			stepPatentDosyaOku.setOrderNo("01");
			stepPatentDosyaOku.setSingleAtATime(false);
			stepPatentDosyaOku.setCommands("runStepClass YillikPatentUcretDosyasiOkuStep");
			stepPatentDosyaOku.setProcessDefinition(yillikPatentUcretiProcess);
			

			ProcessDefinitionStep stepPatentTahakkukOlustur=new ProcessDefinitionStep();
			stepPatentTahakkukOlustur.setCode("PATENT_YILLIK_UCRET_TAHAKKUK");
			stepPatentTahakkukOlustur.setDescription("Patent yıllık ücreti tahakkuk oluşturma");
			stepPatentTahakkukOlustur.setOrderNo("02");
			stepPatentTahakkukOlustur.setSingleAtATime(false);
			stepPatentTahakkukOlustur.setCommands("runStepClass YillikPatentUcretiTahakkukOlusturStep");
			stepPatentTahakkukOlustur.setProcessDefinition(yillikPatentUcretiProcess);
			
			ProcessDefinitionStep stepOnay=new ProcessDefinitionStep();
			stepOnay.setCode("PATENT_ONAY_BEKLE");
			stepOnay.setDescription("Patent yıllık ücreti ödeme için onay bekle");
			stepOnay.setOrderNo("03");
			stepOnay.setSingleAtATime(false);
			stepOnay.setCommands("waitHumanInteraction TAHAKKUK_ONAY");
			stepOnay.setProcessDefinition(yillikPatentUcretiProcess);
			
			ProcessDefinitionStep stepPatentOde=new ProcessDefinitionStep();
			stepPatentOde.setCode("PATENT_TAHAKKUK_ODE");
			stepPatentOde.setDescription("Tahakkuk ödeme");
			stepPatentOde.setOrderNo("04");
			stepPatentOde.setSingleAtATime(true);  //bu ödeme için true olacak
			stepPatentOde.setCommands("runStepClass GenelTahakkukOdemeStep");
			stepPatentOde.setProcessDefinition(yillikPatentUcretiProcess);
			
			
			ProcessDefinitionStep stepPatentDekontIsle=new ProcessDefinitionStep();
			stepPatentDekontIsle.setCode("PATENT_DEKONT_KAYDET");
			stepPatentDekontIsle.setDescription("Ödeme dekont bilgisini sisteme kaydet");
			stepPatentDekontIsle.setOrderNo("05");
			stepPatentDekontIsle.setSingleAtATime(false);
			stepPatentDekontIsle.setCommands("runStepClass GenelDekontKaydetStep");
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
			markaYenilemeProcess.setMaxAttemptCount(1);
			markaYenilemeProcess.setMaxThreadCount(2);
			markaYenilemeProcess.setSteps(new ArrayList<ProcessDefinitionStep>());
			markaYenilemeProcess.setDiscovererClass("DiscoverOdenecekMarkaYenilemeUcretleri");
			markaYenilemeProcess.setActive(false);
			
			ProcessDefinitionStep stepMarkaYenilemeDosyaOku=new ProcessDefinitionStep();
			stepMarkaYenilemeDosyaOku.setCode("MARKA_YENILEME_DOSYASINI_OKU");
			stepMarkaYenilemeDosyaOku.setDescription("Ücreti ödenecek marka yenileme dosyasının okunması");
			stepMarkaYenilemeDosyaOku.setOrderNo("01");
			stepMarkaYenilemeDosyaOku.setSingleAtATime(false);
			stepMarkaYenilemeDosyaOku.setCommands("runStepClass MarkaYenilemeDosyasiOkuStep");
			stepMarkaYenilemeDosyaOku.setProcessDefinition(markaYenilemeProcess);

			ProcessDefinitionStep stepMarkeYenilemeTahakkukOlustur=new ProcessDefinitionStep();
			stepMarkeYenilemeTahakkukOlustur.setCode("MARKA_YENILEME_TAHAKKUK");
			stepMarkeYenilemeTahakkukOlustur.setDescription("Marka yenileme tahakkuk oluşturma");
			stepMarkeYenilemeTahakkukOlustur.setOrderNo("02");
			stepMarkeYenilemeTahakkukOlustur.setSingleAtATime(false);
			stepMarkeYenilemeTahakkukOlustur.setCommands("runStepClass MarkaYenilemeTahakkukOlusturStep");
			stepMarkeYenilemeTahakkukOlustur.setProcessDefinition(markaYenilemeProcess);
			
			ProcessDefinitionStep stepMarkaYenilemeOnay=new ProcessDefinitionStep();
			stepMarkaYenilemeOnay.setCode("MARKA_ONAY_BEKLE");
			stepMarkaYenilemeOnay.setDescription("Marke yenileme tahakkuk ödeme için onay bekle");
			stepMarkaYenilemeOnay.setOrderNo("03");
			stepMarkaYenilemeOnay.setSingleAtATime(false);
			stepMarkaYenilemeOnay.setCommands("waitHumanInteraction TAHAKKUK_ONAY");
			stepMarkaYenilemeOnay.setProcessDefinition(markaYenilemeProcess);
			
			ProcessDefinitionStep stepMarkaOde=new ProcessDefinitionStep();
			stepMarkaOde.setCode("MARKA_TAHAKKUK_ODE");
			stepMarkaOde.setDescription("Tahakkuk ödeme");
			stepMarkaOde.setOrderNo("04");
			stepMarkaOde.setSingleAtATime(true);  //bu ödeme için true olacak
			stepMarkaOde.setCommands("runStepClass GenelTahakkukOdemeStep");
			stepMarkaOde.setProcessDefinition(markaYenilemeProcess);
			
			ProcessDefinitionStep stepMarkaDekontIsle=new ProcessDefinitionStep();
			stepMarkaDekontIsle.setCode("MARKA_DEKONT_KAYDET");
			stepMarkaDekontIsle.setDescription("Ödeme dekont bilgisini sisteme kaydet");
			stepMarkaDekontIsle.setOrderNo("05");
			stepMarkaDekontIsle.setSingleAtATime(false);
			stepMarkaDekontIsle.setCommands("runStepClass GenelDekontKaydetStep");
			stepMarkaDekontIsle.setProcessDefinition(markaYenilemeProcess);
			
			//markaYenilemeProcess.getSteps().add(stepMarkaYenilemeDosyaOku);
			markaYenilemeProcess.getSteps().add(stepMarkeYenilemeTahakkukOlustur);
			markaYenilemeProcess.getSteps().add(stepMarkaYenilemeOnay);
			markaYenilemeProcess.getSteps().add(stepMarkaOde);
			//markaYenilemeProcess.getSteps().add(stepMarkaDekontIsle);

			if (processDefinitionRepo.findByCode(markaYenilemeProcess.getCode()).isEmpty()) {
				processDefinitionRepo.save(markaYenilemeProcess);
			}
			
			//--------------------------------------------
			
			
			ProcessDefinition markaItiraz2=new ProcessDefinition();
			markaItiraz2.setCode("MARKA_2NCI_ITIRAZ");
			markaItiraz2.setDescription("Marka Yayıma İtirazın Yeniden İncelenmesi (YİDD) süreci");
			markaItiraz2.setMaxAttemptCount(1);
			markaItiraz2.setMaxThreadCount(2);
			markaItiraz2.setSteps(new ArrayList<ProcessDefinitionStep>());
			markaItiraz2.setDiscovererClass("DiscoverMarka2nciItiraz");
			markaItiraz2.setActive(false);
			
			ProcessDefinitionStep stepMarka2nciItirazDosyaOku=new ProcessDefinitionStep();
			stepMarka2nciItirazDosyaOku.setCode("MARKA_2NCI_ITIRAZ_DOSYASINI_OKU");
			stepMarka2nciItirazDosyaOku.setDescription("Marka Yayıma itirazın yeniden inceleneceği kayıtların okunması");
			stepMarka2nciItirazDosyaOku.setOrderNo("01");
			stepMarka2nciItirazDosyaOku.setSingleAtATime(false);
			stepMarka2nciItirazDosyaOku.setCommands("runStepClass Marka2nciItirazOkuStep");
			stepMarka2nciItirazDosyaOku.setProcessDefinition(markaItiraz2);

			ProcessDefinitionStep stepMarka2nciItirazOlustur=new ProcessDefinitionStep();
			stepMarka2nciItirazOlustur.setCode("MARKA_2NCI_ITIRAZ_TAHAKKUK");
			stepMarka2nciItirazOlustur.setDescription("Marka yayınına itirazın yeniden inceleneceği kayıtlar için tahakkuk oluşturma");
			stepMarka2nciItirazOlustur.setOrderNo("02");
			stepMarka2nciItirazOlustur.setSingleAtATime(false);
			stepMarka2nciItirazOlustur.setCommands("runStepClass Marka2nciItirazOlusturStep");
			stepMarka2nciItirazOlustur.setProcessDefinition(markaItiraz2);
			
			ProcessDefinitionStep stepMarka2nciItirazOnay=new ProcessDefinitionStep();
			stepMarka2nciItirazOnay.setCode("MARKA_2NCI_ITIRAZ_ONAY");
			stepMarka2nciItirazOnay.setDescription("Marka 2nci itiraz tahakkuk ödeme için onay bekle");
			stepMarka2nciItirazOnay.setOrderNo("03");
			stepMarka2nciItirazOnay.setSingleAtATime(false);
			stepMarka2nciItirazOnay.setCommands("waitHumanInteraction TAHAKKUK_ONAY");
			stepMarka2nciItirazOnay.setProcessDefinition(markaItiraz2);
			
			ProcessDefinitionStep stepMarka2nciItirazOde=new ProcessDefinitionStep();
			stepMarka2nciItirazOde.setCode("MARKA_2NCI_ITIRAZ_ODE");
			stepMarka2nciItirazOde.setDescription("Tahakkuk ödeme");
			stepMarka2nciItirazOde.setOrderNo("04");
			stepMarka2nciItirazOde.setSingleAtATime(true); //bu ödeme için true olacak
			stepMarka2nciItirazOde.setCommands("runStepClass GenelTahakkukOdemeStep");
			stepMarka2nciItirazOde.setProcessDefinition(markaItiraz2);
			
			ProcessDefinitionStep stepMarka2nciItirazDekontIsle=new ProcessDefinitionStep();
			stepMarka2nciItirazDekontIsle.setCode("MARKA_2NCI_ITIRAZ_DEKONT_KAYDET");
			stepMarka2nciItirazDekontIsle.setDescription("Ödeme dekont bilgisini sisteme kaydet");
			stepMarka2nciItirazDekontIsle.setOrderNo("05");
			stepMarka2nciItirazDekontIsle.setSingleAtATime(false);
			stepMarka2nciItirazDekontIsle.setCommands("runStepClass GenelDekontKaydetStep");
			stepMarka2nciItirazDekontIsle.setProcessDefinition(markaItiraz2);
			
			//markaItiraz2.getSteps().add(stepMarka2nciItirazDosyaOku);
			markaItiraz2.getSteps().add(stepMarka2nciItirazOlustur);
			markaItiraz2.getSteps().add(stepMarka2nciItirazOnay);
			markaItiraz2.getSteps().add(stepMarka2nciItirazOde);
			//markaItiraz2.getSteps().add(stepMarka2nciItirazDekontIsle);

			if (processDefinitionRepo.findByCode(markaItiraz2.getCode()).isEmpty()) {
				processDefinitionRepo.save(markaItiraz2);
			}
			
			//--------------------------------------------
			
			ProcessDefinition dummyProcess=new ProcessDefinition();
			dummyProcess.setCode("GOOGLESEARCH");
			dummyProcess.setDescription("Get text from somewhere and search on google");
			dummyProcess.setMaxAttemptCount(2);
			dummyProcess.setMaxThreadCount(4);
			dummyProcess.setSteps(new ArrayList<ProcessDefinitionStep>());
			dummyProcess.setDiscovererClass("DiscoverProcessGooggleSearch");
			dummyProcess.setActive(false);


			ProcessDefinitionStep googleSearchStep=new ProcessDefinitionStep();
			googleSearchStep.setCode("GOOGLE");
			googleSearchStep.setDescription("Search on google");
			googleSearchStep.setOrderNo("01");
			googleSearchStep.setSingleAtATime(false);
			googleSearchStep.setCommands("runStepClass DummyGoogleSearchByKeywordStep");
			googleSearchStep.setProcessDefinition(dummyProcess);


			ProcessDefinitionStep googleWaitApproval=new ProcessDefinitionStep();
			googleWaitApproval.setCode("WAIT_APPROVAL");
			googleWaitApproval.setDescription("Wait for approval");
			googleWaitApproval.setOrderNo("02");
			googleWaitApproval.setSingleAtATime(false);
			googleWaitApproval.setCommands("waitHumanInteraction SEARCH");
			googleWaitApproval.setProcessDefinition(dummyProcess);
			
			ProcessDefinitionStep bingSearchStep=new ProcessDefinitionStep();
			bingSearchStep.setCode("BING");
			bingSearchStep.setDescription("Search on bing");
			bingSearchStep.setOrderNo("03");
			bingSearchStep.setSingleAtATime(true);
			bingSearchStep.setCommands("runStepClass DummyBingSearchByKeywordStep");
			bingSearchStep.setProcessDefinition(dummyProcess);
			
			dummyProcess.getSteps().add(googleSearchStep);
			dummyProcess.getSteps().add(googleWaitApproval);
			dummyProcess.getSteps().add(bingSearchStep);
			
			
			if (processDefinitionRepo.findByCode(dummyProcess.getCode()).isEmpty()) {
				processDefinitionRepo.save(dummyProcess);
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
					+ "<a href=\"http://localhost:8080/approve/${processInstance.code}/APPROVE/EMAIL/${token}\"><b><font color=green>[+ Onayla]</font></b></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/approve/${processInstance.code}/DECLINE/EMAIL/${token}\"><font color=red>[-Reddet]</font></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/approve/${processInstance.code}/VIEW/EMAIL/${token}\"><font color=blue>[İncele]</font></a> "
					+ "</center>"
					+ "<hr>"
					+ " ");
			
			if (emailTemplateRepo.findByCode(emailForYillikPatentUcreti.getCode()).isEmpty()) {
				emailTemplateRepo.save(emailForYillikPatentUcreti);
			}
			
			
			
			//---------------------------------------------------
			EmailTemplate emailForGoogleSearch=new EmailTemplate();
			emailForGoogleSearch.setCode("SEARCH");
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
					+ "<a href=\"http://localhost:8080/approve/${processInstance.code}/APPROVE/EMAIL/${token}\"><b><font color=green>[+ Onayla]</font></b></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/approve/${processInstance.code}/DECLINE/EMAIL/${token}\"><font color=red>[-Reddet]</font></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"http://localhost:8080/approve/${processInstance.code}/VIEW/EMAIL/${token}\"><font color=blue>[İncele]</font></a> "
					+ "</center>"
					+ "<hr>"
					+ " ");
			
			if (emailTemplateRepo.findByCode(emailForGoogleSearch.getCode()).isEmpty()) {
				emailTemplateRepo.save(emailForGoogleSearch);
			}
			
		};

	}

}
