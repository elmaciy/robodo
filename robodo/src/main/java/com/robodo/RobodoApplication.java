package com.robodo;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.robodo.model.CorporateParameter;
import com.robodo.model.EmailTemplate;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessDefinitionStep;
import com.robodo.model.User;
import com.robodo.model.UserRole;
import com.robodo.repo.CorporateParameterRepo;
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
	public CommandLineRunner demo(
			ProcessDefinitionRepo processDefinitionRepo, 
			EmailTemplateRepo emailTemplateRepo, 
			UserRepo userRepo, 
			UserRoleRepo userRoleRepo,
			CorporateParameterRepo corporateParameterRepo) {
		return (args) -> {
			

			//Yıllık Ücret Yenileme : 1, Tescil Sonuçlandırma: 2, Tam Marka Yenileme : 3
			CorporateParameter paramIslemAdimi=new CorporateParameter("PatentYenileme.islemAdimi","1");
			CorporateParameter paramMarkaTescil=new CorporateParameter("MarkaTescil.islemAdimi","2");
			CorporateParameter paramMarkaYenileme=new CorporateParameter("MarkaYenileme.islemAdimi","3");
			
			if (corporateParameterRepo.findAllByCode(paramIslemAdimi.getCode()).isEmpty()) {
				corporateParameterRepo.save(paramIslemAdimi);
			}
			if (corporateParameterRepo.findAllByCode(paramMarkaTescil.getCode()).isEmpty()) {
				corporateParameterRepo.save(paramMarkaTescil);
			}
			if (corporateParameterRepo.findAllByCode(paramMarkaYenileme.getCode()).isEmpty()) {
				corporateParameterRepo.save(paramMarkaYenileme);
			}

			
			CorporateParameter paramAkaraPatentVergiNo=new CorporateParameter("ankarapatent.vergino","0700694032");
			if (corporateParameterRepo.findAllByCode(paramAkaraPatentVergiNo.getCode()).isEmpty()) {
				corporateParameterRepo.save(paramAkaraPatentVergiNo);
			}
			
			
			CorporateParameter paramApiBaseUrl=new CorporateParameter("ankarapatent.api.base.url","http://10.0.6.7:8080");
			CorporateParameter paramApiLang=new CorporateParameter("ankarapatent.api.token.lang","tr");
			CorporateParameter paramApiUsername=new CorporateParameter("ankarapatent.api.token.username","rpaislemleri");
			CorporateParameter paramApiPassword=new CorporateParameter("ankarapatent.api.token.password","yildiray1524elmaci");
			
			if (corporateParameterRepo.findAllByCode(paramApiBaseUrl.getCode()).isEmpty()) {
				corporateParameterRepo.save(paramApiBaseUrl);
				corporateParameterRepo.save(paramApiLang);
				corporateParameterRepo.save(paramApiUsername);
				corporateParameterRepo.save(paramApiPassword);
			}
			
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
			yillikPatentUcretiProcess.setRetryStep("EpatsOrtakRetryBildirmeStep");
			yillikPatentUcretiProcess.setFailStep("EpatsOrtakFailBildirmeStep");
			
			

			ProcessDefinitionStep stepPatentTahakkukOlustur=new ProcessDefinitionStep();
			stepPatentTahakkukOlustur.setCode("PATENT_YILLIK_UCRET_TAHAKKUK");
			stepPatentTahakkukOlustur.setDescription("Patent yıllık ücreti tahakkuk oluşturma");
			stepPatentTahakkukOlustur.setOrderNo("01");
			stepPatentTahakkukOlustur.setSingleAtATime(false);
			stepPatentTahakkukOlustur.setCommands("runStepClass EpatsYillikPatentTahakkukOlusturStep");
			stepPatentTahakkukOlustur.setProcessDefinition(yillikPatentUcretiProcess);
			
			ProcessDefinitionStep stepOnay=new ProcessDefinitionStep();
			stepOnay.setCode("PATENT_ONAY_BEKLE");
			stepOnay.setDescription("Patent yıllık ücreti ödeme için onay bekle");
			stepOnay.setOrderNo("02");
			stepOnay.setSingleAtATime(false);
			//stepOnay.setCommands("waitHumanInteraction TAHAKKUK_ONAY");
			stepOnay.setCommands("waitHumanInteraction ");
			stepOnay.setProcessDefinition(yillikPatentUcretiProcess);
			
			ProcessDefinitionStep stepPatentOde=new ProcessDefinitionStep();
			stepPatentOde.setCode("TAHAKKUK_ODE");
			stepPatentOde.setDescription("Tahakkuk ödeme");
			stepPatentOde.setOrderNo("03");
			stepPatentOde.setSingleAtATime(true);  //bu ödeme için true olacak
			stepPatentOde.setCommands("runStepClass EpatsOrtakTahakkukOdeStep");
			stepPatentOde.setProcessDefinition(yillikPatentUcretiProcess);
			
			ProcessDefinitionStep stepPatentDekontKaydet=new ProcessDefinitionStep();
			stepPatentDekontKaydet.setCode("DEKONT_KAYDET");
			stepPatentDekontKaydet.setDescription("Dekont Kaydetme");
			stepPatentDekontKaydet.setOrderNo("04");
			stepPatentDekontKaydet.setSingleAtATime(false);
			stepPatentDekontKaydet.setCommands("runStepClass EpatsOrtakDekontKaydetStep");
			stepPatentDekontKaydet.setProcessDefinition(yillikPatentUcretiProcess);
			


			yillikPatentUcretiProcess.getSteps().add(stepPatentTahakkukOlustur);
			yillikPatentUcretiProcess.getSteps().add(stepOnay);
			yillikPatentUcretiProcess.getSteps().add(stepPatentOde);
			yillikPatentUcretiProcess.getSteps().add(stepPatentDekontKaydet);
			
			if (processDefinitionRepo.findByCode(yillikPatentUcretiProcess.getCode()).isEmpty()) {
				processDefinitionRepo.save(yillikPatentUcretiProcess);
			}
			
			
			//----------------------------------------------------------------------

			
			ProcessDefinition markaTescilUcretiProcess=new ProcessDefinition();
			markaTescilUcretiProcess.setCode("MARKA_TESCIL_UCRET");
			markaTescilUcretiProcess.setDescription("Marka tescil ücreti ödeme süreci");
			markaTescilUcretiProcess.setMaxAttemptCount(1);
			markaTescilUcretiProcess.setMaxThreadCount(2);
			markaTescilUcretiProcess.setSteps(new ArrayList<ProcessDefinitionStep>());
			markaTescilUcretiProcess.setDiscovererClass("DiscoverOdenecekMarkaTescilUcretleri");
			markaTescilUcretiProcess.setActive(false);
			markaTescilUcretiProcess.setRetryStep("EpatsOrtakRetryBildirmeStep");
			markaTescilUcretiProcess.setFailStep("EpatsOrtakFailBildirmeStep");
			
			

			ProcessDefinitionStep stepMarkaTescilTahakkukOlustur=new ProcessDefinitionStep();
			stepMarkaTescilTahakkukOlustur.setCode("MARKA_TESCIL_UCRET_TAHAKKUK");
			stepMarkaTescilTahakkukOlustur.setDescription("Marka tescil ücreti tahakkuk oluşturma");
			stepMarkaTescilTahakkukOlustur.setOrderNo("01");
			stepMarkaTescilTahakkukOlustur.setSingleAtATime(false);
			stepMarkaTescilTahakkukOlustur.setCommands("runStepClass EpatsMarkaTescilTahakkukOlusturStep");
			stepMarkaTescilTahakkukOlustur.setProcessDefinition(markaTescilUcretiProcess);
			
			ProcessDefinitionStep stepMarkaTescilOnay=new ProcessDefinitionStep();
			stepMarkaTescilOnay.setCode("MARKA_TESCIL_ONAY_BEKLE");
			stepMarkaTescilOnay.setDescription("Marka tescil ücreti ödeme için onay bekle");
			stepMarkaTescilOnay.setOrderNo("02");
			stepMarkaTescilOnay.setSingleAtATime(false);
			stepMarkaTescilOnay.setCommands("waitHumanInteraction ");
			stepMarkaTescilOnay.setProcessDefinition(markaTescilUcretiProcess);
			
			ProcessDefinitionStep stepMarkaTescilOde=new ProcessDefinitionStep();
			stepMarkaTescilOde.setCode("TAHAKKUK_ODE");
			stepMarkaTescilOde.setDescription("Tahakkuk ödeme");
			stepMarkaTescilOde.setOrderNo("03");
			stepMarkaTescilOde.setSingleAtATime(true);  //bu ödeme için true olacak
			stepMarkaTescilOde.setCommands("runStepClass EpatsOrtakTahakkukOdeStep");
			stepMarkaTescilOde.setProcessDefinition(markaTescilUcretiProcess);
			
			ProcessDefinitionStep stepMarkaTescilDekontKaydet=new ProcessDefinitionStep();
			stepMarkaTescilDekontKaydet.setCode("DEKONT_KAYDET");
			stepMarkaTescilDekontKaydet.setDescription("Dekont Kaydetme");
			stepMarkaTescilDekontKaydet.setOrderNo("04");
			stepMarkaTescilDekontKaydet.setSingleAtATime(false);
			stepMarkaTescilDekontKaydet.setCommands("runStepClass EpatsOrtakDekontKaydetStep");
			stepMarkaTescilDekontKaydet.setProcessDefinition(markaTescilUcretiProcess);


			markaTescilUcretiProcess.getSteps().add(stepMarkaTescilTahakkukOlustur);
			markaTescilUcretiProcess.getSteps().add(stepMarkaTescilOnay);
			markaTescilUcretiProcess.getSteps().add(stepMarkaTescilOde);
			markaTescilUcretiProcess.getSteps().add(stepMarkaTescilDekontKaydet);
			
			if (processDefinitionRepo.findByCode(markaTescilUcretiProcess.getCode()).isEmpty()) {
				processDefinitionRepo.save(markaTescilUcretiProcess);
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
			markaYenilemeProcess.setRetryStep("EpatsOrtakRetryBildirmeStep");
			markaYenilemeProcess.setFailStep("EpatsOrtakFailBildirmeStep");

			ProcessDefinitionStep stepMarkeYenilemeTahakkukOlustur=new ProcessDefinitionStep();
			stepMarkeYenilemeTahakkukOlustur.setCode("MARKA_YENILEME_TAHAKKUK");
			stepMarkeYenilemeTahakkukOlustur.setDescription("Marka yenileme tahakkuk oluşturma");
			stepMarkeYenilemeTahakkukOlustur.setOrderNo("01");
			stepMarkeYenilemeTahakkukOlustur.setSingleAtATime(false);
			stepMarkeYenilemeTahakkukOlustur.setCommands("runStepClass EpatsMarkaYenilemeTahakkukOlusturStep");
			stepMarkeYenilemeTahakkukOlustur.setProcessDefinition(markaYenilemeProcess);
			
			ProcessDefinitionStep stepMarkaYenilemeOnay=new ProcessDefinitionStep();
			stepMarkaYenilemeOnay.setCode("MARKA_ONAY_BEKLE");
			stepMarkaYenilemeOnay.setDescription("Marke yenileme tahakkuk ödeme için onay bekle");
			stepMarkaYenilemeOnay.setOrderNo("02");
			stepMarkaYenilemeOnay.setSingleAtATime(false);
			//stepMarkaYenilemeOnay.setCommands("waitHumanInteraction TAHAKKUK_ONAY");
			stepMarkaYenilemeOnay.setCommands("waitHumanInteraction");
			stepMarkaYenilemeOnay.setProcessDefinition(markaYenilemeProcess);
			
			ProcessDefinitionStep stepMarkaYenilemeOde=new ProcessDefinitionStep();
			stepMarkaYenilemeOde.setCode("TAHAKKUK_ODE");
			stepMarkaYenilemeOde.setDescription("Tahakkuk ödeme");
			stepMarkaYenilemeOde.setOrderNo("03");
			stepMarkaYenilemeOde.setSingleAtATime(true);  //bu ödeme için true olacak
			stepMarkaYenilemeOde.setCommands("runStepClass EpatsOrtakTahakkukOdeStep");
			stepMarkaYenilemeOde.setProcessDefinition(markaYenilemeProcess);
			
			
			ProcessDefinitionStep stepMarkaYenilemeDekontKaydet=new ProcessDefinitionStep();
			stepMarkaYenilemeDekontKaydet.setCode("DEKONT_KAYDET");
			stepMarkaYenilemeDekontKaydet.setDescription("Dekont Kaydetme");
			stepMarkaYenilemeDekontKaydet.setOrderNo("04");
			stepMarkaYenilemeDekontKaydet.setSingleAtATime(false);
			stepMarkaYenilemeDekontKaydet.setCommands("runStepClass EpatsOrtakDekontKaydetStep");
			stepMarkaYenilemeDekontKaydet.setProcessDefinition(markaYenilemeProcess);
			
			
			markaYenilemeProcess.getSteps().add(stepMarkeYenilemeTahakkukOlustur);
			markaYenilemeProcess.getSteps().add(stepMarkaYenilemeOnay);
			markaYenilemeProcess.getSteps().add(stepMarkaYenilemeOde);
			markaYenilemeProcess.getSteps().add(stepMarkaYenilemeDekontKaydet);

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
			markaItiraz2.setRetryStep("EpatsOrtakRetryBildirmeStep");
			markaItiraz2.setFailStep("EpatsOrtakFailBildirmeStep");
			
			ProcessDefinitionStep stepMarka2nciItirazOlustur=new ProcessDefinitionStep();
			stepMarka2nciItirazOlustur.setCode("MARKA_2NCI_ITIRAZ_TAHAKKUK");
			stepMarka2nciItirazOlustur.setDescription("Marka yayınına itirazın yeniden inceleneceği kayıtlar için tahakkuk oluşturma");
			stepMarka2nciItirazOlustur.setOrderNo("01");
			stepMarka2nciItirazOlustur.setSingleAtATime(false);
			stepMarka2nciItirazOlustur.setCommands("runStepClass EpatsMarka2nciItirazOlusturStep");
			stepMarka2nciItirazOlustur.setProcessDefinition(markaItiraz2);
			
			ProcessDefinitionStep stepMarka2nciItirazOnay=new ProcessDefinitionStep();
			stepMarka2nciItirazOnay.setCode("MARKA_2NCI_ITIRAZ_ONAY");
			stepMarka2nciItirazOnay.setDescription("Marka 2nci itiraz tahakkuk ödeme için onay bekle");
			stepMarka2nciItirazOnay.setOrderNo("02");
			stepMarka2nciItirazOnay.setSingleAtATime(false);
			//stepMarka2nciItirazOnay.setCommands("waitHumanInteraction TAHAKKUK_ONAY");
			stepMarka2nciItirazOnay.setCommands("waitHumanInteraction");
			stepMarka2nciItirazOnay.setProcessDefinition(markaItiraz2);
			
			ProcessDefinitionStep stepMarka2nciItirazOde=new ProcessDefinitionStep();
			stepMarka2nciItirazOde.setCode("TAHAKKUK_ODE");
			stepMarka2nciItirazOde.setDescription("Tahakkuk ödeme");
			stepMarka2nciItirazOde.setOrderNo("03");
			stepMarka2nciItirazOde.setSingleAtATime(true); //bu ödeme için true olacak
			stepMarka2nciItirazOde.setCommands("runStepClass EpatsOrtakTahakkukOdeStep");
			stepMarka2nciItirazOde.setProcessDefinition(markaItiraz2);

			ProcessDefinitionStep stepMarkaMarka2nciItirazDekontKaydet=new ProcessDefinitionStep();
			stepMarkaMarka2nciItirazDekontKaydet.setCode("DEKONT_KAYDET");
			stepMarkaMarka2nciItirazDekontKaydet.setDescription("Dekont Kaydetme");
			stepMarkaMarka2nciItirazDekontKaydet.setOrderNo("04");
			stepMarkaMarka2nciItirazDekontKaydet.setSingleAtATime(false);
			stepMarkaMarka2nciItirazDekontKaydet.setCommands("runStepClass EpatsOrtakDekontKaydetStep");
			stepMarkaMarka2nciItirazDekontKaydet.setProcessDefinition(markaItiraz2);
			
			
			markaItiraz2.getSteps().add(stepMarka2nciItirazOlustur);
			markaItiraz2.getSteps().add(stepMarka2nciItirazOnay);
			markaItiraz2.getSteps().add(stepMarka2nciItirazOde);
			markaItiraz2.getSteps().add(stepMarkaMarka2nciItirazDekontKaydet);

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
					+ "<a href=\"${LINK.APPROVE}\"><b><font color=green>[+ Onayla]</font></b></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"${LINK.DECLINE}\"><font color=red>[-Reddet]</font></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"${LINK.VIEW}\"><font color=blue>[İncele]</font></a> "
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
					+ "<a href=\"${LINK.APPROVE}\"><b><font color=green>[+ Onayla]</font></b></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"${LINK.DECLINE}\"><font color=red>[-Reddet]</font></a> "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<a href=\"${LINK.VIEW}\"><font color=blue>[İncele]</font></a> "
					+ "</center>"
					+ "<hr>"
					+ " ");
			
			if (emailTemplateRepo.findByCode(emailForGoogleSearch.getCode()).isEmpty()) {
				emailTemplateRepo.save(emailForGoogleSearch);
			}
			
		};

	}

}
