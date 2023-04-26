package com.robodo.turkpatent.steps;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;
import com.robodo.base.BaseWebStep;
import com.robodo.model.ApiResponse;
import com.robodo.model.KeyValue;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.turkpatent.apimodel.DosyaListeleri;
import com.robodo.turkpatent.apimodel.DosyaRequest;
import com.robodo.turkpatent.apimodel.DosyaResponse;
import com.robodo.turkpatent.apimodel.Rumuz;
import com.robodo.turkpatent.apimodel.RumuzEsleme;
import com.robodo.turkpatent.apimodel.RumuzEslemeResponse;
import com.robodo.turkpatent.apimodel.SingletonForTokenManager;
import com.robodo.turkpatent.pages.PageEdevletLogin;
import com.robodo.turkpatent.pages.PageEpatsBasvuruYapan;
import com.robodo.turkpatent.pages.PageEpatsBenimSayfam;
import com.robodo.turkpatent.pages.PageEpatsDosyaBilgisi;
import com.robodo.turkpatent.pages.PageEpatsDosyaBilgisiDigerIslemler;
import com.robodo.turkpatent.pages.PageEpatsHizmetDokumu;
import com.robodo.turkpatent.pages.PageEpatsHome;
import com.robodo.turkpatent.pages.PageEpatsIslemSonucu;
import com.robodo.turkpatent.pages.PageEpatsItirazGerekceleri;
import com.robodo.turkpatent.pages.PageEpatsItirazSahibiBilgisi;
import com.robodo.turkpatent.pages.PageEpatsItirazaIliskinBilgiler;
import com.robodo.turkpatent.pages.PageEpatsItirazaIliskinEkler;
import com.robodo.turkpatent.pages.PageEpatsMenu;
import com.robodo.turkpatent.pages.PageEpatsOnIzleme;
import com.robodo.turkpatent.pages.PageEpatsTahakkukOde;
import com.robodo.turkpatent.pages.PageEpatsTahakkuklarim;
import com.robodo.turkpatent.pages.PageEpatsTalepTuru;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

import io.restassured.http.Method;

public class BaseEpatsStep extends BaseWebStep {
	
	

	public final static Integer EPATS_STATU_TASLAK=531; //????
	public final static Integer EPATS_STATU_RPA_ISLEMDE=1; // "RPA işlemde";
	public final static Integer EPATS_STATU_TAHAKKUK=2; // "RPA Tahakkuk";
	public final static Integer EPATS_STATU_ODEME_YAPILIYOR=3; //"RPA ödeme";
	public final static Integer EPATS_STATU_ONAY_BEKLIYOR=4; //"RPA Onayı Bekleniyor";
	public final static Integer EPATS_STATU_ODEME_TAMAMLANDI=5; //"RPA Ödeme Tamamlandı";
	public final static Integer EPATS_STATU_HATA_ALDI=6; //"RPA Hata Aldı";
	
	
	PageEpatsHome home;
	PageEdevletLogin edevletLogin;
	PageEpatsMenu  epatsMenu;
	PageEpatsBenimSayfam epatsBenimSayfam;
	PageEpatsTahakkuklarim epatsTahakkuklarim;
	PageEpatsBasvuruYapan epatsBasvuruYapan;
	PageEpatsDosyaBilgisi epatsDosyaBilgisi;
	PageEpatsDosyaBilgisiDigerIslemler epatsDosyaBilgisiDigerIslemler;
	PageEpatsHizmetDokumu epatsHizmetDokumu;
	PageEpatsOnIzleme epatsOnIzleme;
	PageEpatsIslemSonucu epatsIslemSonucu;
	PageEpatsTahakkukOde epatsTahakkukOde;
	PageEpatsTalepTuru epatsTalepTuru;
	PageEpatsItirazSahibiBilgisi epatsItirazSahibiBilgisi;
	PageEpatsItirazGerekceleri epatsItirazGerekceleri;
	PageEpatsItirazaIliskinBilgiler epatsItirazaIliskinBilgiler;
	PageEpatsItirazaIliskinEkler epatsItirazaIliskinEkler;
	
	public BaseEpatsStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
		
		
	}
	
	
	@Override
	public void setup() {
		selenium.startWebDriver();
		
		
		this.home=new PageEpatsHome(selenium);
		this.edevletLogin=new PageEdevletLogin(selenium);
		this.epatsMenu=new PageEpatsMenu(selenium);
		this.epatsBenimSayfam=new PageEpatsBenimSayfam(selenium);
		this.epatsTahakkuklarim=new PageEpatsTahakkuklarim(selenium);
		this.epatsBasvuruYapan=new PageEpatsBasvuruYapan(selenium);
		this.epatsDosyaBilgisi=new PageEpatsDosyaBilgisi(selenium);
		this.epatsDosyaBilgisiDigerIslemler=new PageEpatsDosyaBilgisiDigerIslemler(selenium);
		this.epatsHizmetDokumu=new PageEpatsHizmetDokumu(selenium);
		this.epatsOnIzleme = new PageEpatsOnIzleme(selenium);
		this.epatsIslemSonucu=new PageEpatsIslemSonucu(selenium);
		this.epatsTahakkukOde=new PageEpatsTahakkukOde(selenium);
		this.epatsTalepTuru=new PageEpatsTalepTuru(selenium);
		this.epatsItirazSahibiBilgisi=new PageEpatsItirazSahibiBilgisi(selenium);
		this.epatsItirazGerekceleri=new PageEpatsItirazGerekceleri(selenium);
		this.epatsItirazaIliskinBilgiler=new PageEpatsItirazaIliskinBilgiler(selenium);
		this.epatsItirazaIliskinEkler=new PageEpatsItirazaIliskinEkler(selenium);
		
	}

	@Override
	public void teardown() {
		takeStepScreenShot(processInstanceStep, "end of step", false, ()->waitProcessorGone());
		selenium.stopWebDriver();
		
	}
	
	@Override
	public void run() {
	}



	public void sistemeGiris() {
		//home.open();
		
		int islemAdimi=Integer.valueOf(getVariable("islemAdimi")); 		
		Rumuz edevletRumuz =getRumuzEdevletByIslemAdimi(islemAdimi);
		String tckno=edevletRumuz.getTckimlik();
		String sifre=edevletRumuz.getSifre();
		
		
		home.navigateEdevletGiris();
		home.clickEdevlet();

		edevletLogin.girisEdevlet(tckno, sifre);
		takeStepScreenShot(processInstanceStep, "Sisteme giris yapildi", false, ()->waitProcessorGone());
	}
	
	
	public void dosyaAra() {
		epatsMenu.gotoVekillikSayfam();
		String dosyaNo=getVariable("dosyaNumarasi");
		String basvuruTuru=getVariable("basvuruTuru");
		epatsBenimSayfam.dosyaArama(dosyaNo,basvuruTuru);
		takeStepScreenShot(this.processInstanceStep, "Dosya arama sonucu", false, ()->waitProcessorGone());
		
	}
	
	public void islemSec() {
		String islemGrubu=getVariable("islemGrubu");
		String islemAdi=getVariable("islemAdi");
		epatsBenimSayfam.islemSec(islemGrubu,islemAdi);
		takeStepScreenShot(this.processInstanceStep, "Islem secimi", false, ()->waitProcessorGone());
	}
	
	
	
	public void basvuruYap() {
		String eposta=getVariable("eposta");
		String cepTel=getVariable("telefonNumarasi");
		String referansNo=getVariable("takipNumarasi");
		
		epatsBasvuruYapan.basvuruBilgileriniDoldur(eposta, cepTel, referansNo);
		takeStepScreenShot(this.processInstanceStep, "Basvuru Bilgileri", false, ()->waitProcessorGone());
		epatsBasvuruYapan.devamEt();
	}
	
	
	
	boolean isPatent() {
		return "PATENT".contains(getVariable("basvuruTuru"));
	}
	
	boolean isMarka() {
		return "MARKA".contains(getVariable("basvuruTuru"));
	}
	
	public void dosyaBilgisiAra() {
		epatsDosyaBilgisi.basvuruNumarasiAra(getVariable("dosyaNumarasi"));
	}
	
	public void dosyaBilgisiDigerIslemler() {
		epatsDosyaBilgisiDigerIslemler.basvuruNumarasiAra(getVariable("dosyaNumarasi"));
		waitProcessorGone();
		takeStepScreenShot(processInstanceStep, "dosya bilgisi sonucu", false);
		selenium.sleep(3L);
		epatsDosyaBilgisiDigerIslemler.devamEt();
		
	}
	
	public void dosyaBilgisiDogrulaDevamEt() {
		setVariable("dosyabilgisi.dosyabilgisi.basvuruNumarasi", epatsDosyaBilgisi.getBasvuruNumarasi());
		setVariable("dosyabilgisi.dosyabilgisi.basvuruTarihi", epatsDosyaBilgisi.getBasvuruTarihi());
		
		if (isPatent()) {
			setVariable("dosyabilgisi.dosyabilgisi.bulusBasligi", epatsDosyaBilgisi.getBulusBasligi());
		}
		if (isMarka()) {
			setVariable("dosyabilgisi.dosyabilgisi.markaAdi", epatsDosyaBilgisi.getmarkaAdi());
		}		
		
		karsilastir(getVariable("dosyabilgisi.dosyabilgisi.basvuruNumarasi"), getVariable("dosyaNumarasi"), "dosya numarası karşılaştırılıyor");
		
		if (isMarka()) {
			karsilastir(getVariable("dosyabilgisi.dosyabilgisi.markaAdi"), getVariable("markaAdi"), "marka adı karşılaştırılıyor");
		}

		takeStepScreenShot(this.processInstanceStep, "Dosya bilgisi", false, ()->waitProcessorGone());
		epatsDosyaBilgisi.devamEt();
	}
	
	public void itirazSahibiEkleDevamEt() {
		String itirazSahibiAdi= getVariable("itirazSahibiAdi");
		String itirazSahibiKimlikNo = getVariable("itirazSahibiKimlikNo");
		
		epatsItirazSahibiBilgisi.itirazSahibiEkle(itirazSahibiAdi, itirazSahibiKimlikNo);
		

		epatsItirazSahibiBilgisi.devamEt();
		takeStepScreenShot(processInstanceStep, "Itiraz sabibi ekleme", false, ()->waitProcessorGone());
	}
	
	public void itirazGerekceleriEkle() {
		List<String> itirazSecenekleri = List.of(
				"Benzerlik/Karıştırılma İhtimali (6/1)",
				"Temsilci Tarafından Yapılan İzinsiz Başvuru (6/2)",
				"Eskiye Dayalı Kullanım (6/3)",
				"Paris Sözleşmesi Kapsamında Tanınmışlık (6/4)",
				"Tanınmışlık (6/5)",
				"Diğer Fikri Haklar veya Kişi Hakları (6/6)",
				"Ortak/Garanti Markanın Yenilenmemesi (6/7)",
				"Tescilli Markanın Yenilenmemesi (6/8)",
				"Kötü Niyet (6/9)",
				"Diğer");
		
		
		
		itirazSecenekleri.forEach(itiraz->{
			if (getVariable(itiraz)!=null) {
				epatsItirazGerekceleri.itirazGerekcesiIsaretle(itiraz);	
			}
			
		});
		
		takeStepScreenShot(processInstanceStep, "Itiraz gerekceleri isaretlendi", false, ()->waitProcessorGone());
	}
	
	public void itirazaGerekceDosyaNumaralariEkleDevamEt() {
		String itirazaGerekceDosyaNumaralari=getVariable("itirazaGerekceDosyaNumaralari");
		if (itirazaGerekceDosyaNumaralari==null || itirazaGerekceDosyaNumaralari.strip().isBlank()) {
			runnerUtil.logger("itiraza gerekce dosya belirtilmediğinden bu adım geçiliyor. ");
			return;
		}
		List<String> dosyaNumaralari = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(itirazaGerekceDosyaNumaralari);
		dosyaNumaralari.forEach(dosya->{
			epatsItirazGerekceleri.itirazaGerekceDosyaEkle(dosya);
		});
		
		takeStepScreenShot(processInstanceStep, "Itiraza gerekce dosyalalari ekle", false, ()->waitProcessorGone());
		
		epatsItirazGerekceleri.devamEt();
	}
	
	public void itirazaIliskibBilgileriEkleDevamEt() {
		String itirazaIliskinDosya=getVariable("itirazaIliskinDosya");
		epatsItirazaIliskinBilgiler.itirazaIliskinEvrakYukle(itirazaIliskinDosya);
		takeStepScreenShot(processInstanceStep, "İtiraza iliskin bilgilere ait dosya eklendi", false, ()->waitProcessorGone());
		epatsItirazaIliskinBilgiler.devamEt();

	}
	
	public void itirazaIliskinEkleriEkleDevamEt() {
		epatsItirazaIliskinEkler.devamEt();
	}
	
	public void talepTuruTamSecVeDevamEt() {
		epatsTalepTuru.talepTuruSec("Tam");
		epatsTalepTuru.devamEt();
	}
	
	public void hizmetDokumuDevamEt() {
		String ankaraPatentKodu=runnerUtil.getEnvironmentParameter("ankarapatent.vergino");
		epatsHizmetDokumu.basvuruSahibiSec(ankaraPatentKodu);
		takeStepScreenShot(this.processInstanceStep, "Hizmet dokumu", true, ()->waitProcessorGone());
		selenium.sleep(3L);
		epatsHizmetDokumu.devamEt();
	}

	public void onizlemeKontrolveTahakkukOlustur() {
		
		setVariable("onizleme.odenecek.dosyaNumarasi", epatsOnIzleme.getDosyaNumarasi());
		setVariable("onizleme.odenecek.referansNumarasi", epatsOnIzleme.getRefeansTakipNumarasi());
		
		if (isPatent()) {			
			setVariable("onizleme.odenecek.bulusBasligi", epatsOnIzleme.getBulusBasligi());
		}

		if (isMarka()) {
			setVariable("onizleme.odenecek.markaAdi", epatsOnIzleme.getMarkaAdi());
		}
		
		setVariable("onizleme.odenecek.genelToplam", epatsOnIzleme.getGenelToplamTutari());
		
		
		karsilastir(getVariable("onizleme.odenecek.dosyaNumarasi"), getVariable("dosyaNumarasi"), "dosya numarası karşılaştırılıyor");
		karsilastir(getVariable("onizleme.odenecek.referansNumarasi"), getVariable("takipNumarasi"), "başvuru takip/referans numarası karşılaştırılıyor");
		
		
		if (isMarka()) {			
			karsilastir(getVariable("onizleme.odenecek.markaAdi"), getVariable("markaAdi"), "marka adı karşılaştırılıyor");
		}
				
		takeStepScreenShot(this.processInstanceStep, "Onizleme - 1 ", true, ()->waitProcessorGone());
		epatsOnIzleme.scrollToCenteElement();
		takeStepScreenShot(this.processInstanceStep, "Onizleme - 2", true, ()->waitProcessorGone());

		epatsOnIzleme.tahakkukOlustur();
		
	}
	
	public void  tahakkukNumarasiAl() {
		String sonuc = epatsIslemSonucu.sonucAl();
		setVariable("islemsonucu.sonuc", sonuc);
		takeStepScreenShot(this.processInstanceStep, "Tahakkuk olusturuldu", true, ()->waitProcessorGone());
		try {
			String tahakkukNo=StringUtils.substringAfter(sonuc, "Tahakkuk No:");
			Integer.parseInt(tahakkukNo);
			setVariable("tahakkukNo", tahakkukNo);
		} catch(Exception e) {
			e.printStackTrace();
			String msg="Tahakkuk no çıkarılırken beklenmedik bir hata oluştu : %s".formatted(e.getMessage());
			runnerUtil.logger(msg);
			throw new RuntimeException(msg);
		}
		
	}
	
	public void vazgecVeSayfayaDon() {
		epatsMenu.vazgecVeSayfayaDon();
	}
	
	
	public void anaSayfayaDon() {
		epatsIslemSonucu.anaSayfayaDon();
	}
	
	public void karsilastir(String v1, String v2, String mesaj) {
		runnerUtil.logger("compare [%s] and [%s].".formatted(v1,v2));
		if (v1.equals(v2)) {
			return;
		}
		
		runnerUtil.logger("not validated : %s".formatted(mesaj));
		throw new RuntimeException("%s. v1=%s, v2=%s".formatted(mesaj,v1, v2));
	}



	
	public void waitProcessorGone() {
		home.waitProcessorGone();
		
	}
	
	
	
	public void dosyaLinkleriGuncelle(String linkKontrol, String linkOnayla, String linkReddet) {
		int id=Integer.valueOf(getVariable("dosya.id"));
		dosyaGuncelle(DosyaRequest.create(id).withLinks(linkKontrol, linkOnayla, linkReddet),
				"dosya [%s] linkleri güncelle => kontrol : <%s>, onay : <%s>, red : <%s>".formatted(id, linkKontrol, linkOnayla, linkReddet));
	}
	
	
	
	public void dosyaDurumGuncelle(int statu) {
		int id=Integer.valueOf(getVariable("dosya.id"));
		dosyaDurumGuncelle(id, statu);
	}
	
	public void dosyaDurumGuncelle(int id, int statu) {
		dosyaGuncelle(DosyaRequest.create(id).withStatu(statu),
				"dosya [%s] durum güncelle => %d".formatted(id, statu));
	}
	
	public void dosyaLTahakkukNoGuncelle(String tahakkukNo) {
		int id=Integer.valueOf(getVariable("dosya.id"));
		dosyaGuncelle(DosyaRequest.create(id).withTahakkukNo(tahakkukNo),
				"dosya [%s] tahakkuk no güncelle => %s".formatted(id, tahakkukNo));
	}
	
	public void dosyaLDekontNoGuncelle(String dekontNo) {
		int id=Integer.valueOf(getVariable("dosya.id"));
		dosyaGuncelle(DosyaRequest.create(id).withDekontNo(dekontNo),
				"dosya [%s] dekont no güncelle => %s".formatted(id, dekontNo));
	}
	
	
	private void dosyaGuncelle(DosyaRequest dosyaRequest, String description) {
		String apiHostname = runnerUtil.getEnvironmentParameter("ankarapatent.api.base.url");
		String endPoint="%s/rpaservisleriController/updateRpadosyaislemleri".formatted(apiHostname );
		String token=SingletonForTokenManager.getInstance().getJwtToken(this);
		List<KeyValue> headers=List.of(
				new KeyValue("Authorization","Bearer %s".formatted(token)),
				new KeyValue("Content-type","application/json")
				);
		
		ApiResponse response = httpRequest(Method.PATCH, endPoint, headers, null, dosyaRequest);

		if (response.getResponseCode()!=200) {
			throw new RuntimeException("Güncelleme başarısız : ".formatted(description));
		}
	}
	
	public List<DosyaResponse> getRpaIslemdeDosyalarByIslemAdimi(int islemAdimi) {
		String apiHostname = runnerUtil.getEnvironmentParameter("ankarapatent.api.base.url");
		int statu=BaseEpatsStep.EPATS_STATU_RPA_ISLEMDE; 
		String endPoint="%s/rpaservisleriController/listRpadosyalarByStatu?statu=%d&islemadimi=%d".formatted(apiHostname, statu, islemAdimi);
		String token=SingletonForTokenManager.getInstance().getJwtToken(this);
		List<KeyValue> headers=List.of(new KeyValue("Authorization","Bearer %s".formatted(token)));
		ApiResponse response = httpRequest(Method.GET, endPoint, headers, null, null);
		
		
		if (response.getResponseCode()!=200) {
			throw new RuntimeException("dosyalar listelenemedi");
		}
		
		var dosyaListeleri = json2Object(response.getBody(), DosyaListeleri.class);
		//Predicate<? super DosyaResponse> filter=p->p.getIslemadimi()==islemAdimi && p.getStatu()==BaseEpatsStep.EPATS_STATU_RPA_ISLEMDE;
		Predicate<? super DosyaResponse> filter=p->p.getIslemadimi()==islemAdimi && p.getDurum()==1 && p.getReferansno()!=null && !p.getReferansno().isBlank();
		return dosyaListeleri.getData().stream().filter(filter).collect(Collectors.toList());
	}
	
	
	
	



	public List<ProcessInstance> createEpatsInstances(
			ProcessDefinition processDefinition,
			List<DosyaResponse> dosyalar, 
			Function<DosyaResponse, String> forInstanceCode, 
			Function<DosyaResponse, String> forDescription) {
		
		List<ProcessInstance> instances=new ArrayList<ProcessInstance>();

		for (DosyaResponse dosya : dosyalar) {
			String instanceKey=forInstanceCode.apply(dosya);
			String description=forDescription.apply(dosya);
			
			ProcessInstance instance =new ProcessInstance();
			instance=new ProcessInstance();
			instance.setCode(instanceKey);
			instance.setDescription(description);
			instance.setCreated(LocalDateTime.now());
			instance.setStarted(LocalDateTime.now());
			instance.setFinished(null);
			instance.setAttemptNo(0);
			instance.setStatus(ProcessInstance.STATUS_NEW);
			instance.setProcessDefinitionId(processDefinition.getId());
			instance.setSteps(new ArrayList<ProcessInstanceStep>());
			
			
			HashMap<String, String> hmVars=new HashMap<String, String>();
			hmVars.put("processInstance.code", instance.getCode());
			hmVars.put("dosyaResponse.JSON", HelperUtil.obj2String(dosya));
			hmVars.put("dosya.id", String.valueOf(dosya.getId()));
			
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
		}

		return instances;
	}
	
	protected void dosyaLinkleriGuncelle(ProcessInstance processInstance) {
		String lnkKontrol = HelperUtil.generateInstanceApprovalLink(this.runnerUtil, processInstance,  "VIEW", "EXTERNAL");
		String lnkOnay=HelperUtil.generateInstanceApprovalLink(this.runnerUtil, processInstance, "APPROVE", "EXTERNAL");
		String lnkRed=HelperUtil.generateInstanceApprovalLink(this.runnerUtil, processInstance, "DECLINE", "EXTERNAL");
		
		dosyaLinkleriGuncelle(lnkKontrol, lnkOnay, lnkRed);		
	}
	
	
	


	public void dosyaTahakkukKaydet() {
		String tahakkukNo=getVariable("tahakkukNo");
		dosyaLTahakkukNoGuncelle(tahakkukNo);
		dosyaDurumGuncelle(EPATS_STATU_ONAY_BEKLIYOR);
	}


	public void dosyaDekontKaydet() {
		String dekontNo=getVariable("dekontNo");
		dosyaLDekontNoGuncelle(dekontNo);
	}
	
	public  void dosyaLinkSifirla() {
		dosyaLinkleriGuncelle("-", "-", "-");
		dosyaLTahakkukNoGuncelle("-");
		dosyaLDekontNoGuncelle("-");
	}
	



	public RumuzEslemeResponse getRumuzEslemeResponseByIslemAdimi(int islemAdimi) {

		String apiHostname = runnerUtil.getEnvironmentParameter("ankarapatent.api.base.url");
		String endPoint="%s/rpaservisleriController/islemAdminagoreIslemEslestirmeleriniGetir?islemadimi=%d".formatted(apiHostname, islemAdimi);
		String token=SingletonForTokenManager.getInstance().getJwtToken(this);
		List<KeyValue> headers=List.of(
				new KeyValue("Authorization","Bearer %s".formatted(token)),
				new KeyValue("Content-type","application/json")
				);
		
		ApiResponse response = httpRequest(Method.POST, endPoint, headers,null,  null);
		

		if (response.getResponseCode()!=200) {
			throw new RuntimeException("getRumuzEslemeByIslemAdimi başarısız : %d".formatted(response.getResponseCode()));
		}

		var rumuzEslemeleriResponse= json2Object(response.getBody(), RumuzEslemeResponse.class);
		
		if (!rumuzEslemeleriResponse.getMessage().equals("Success")) {
			throw new RuntimeException("getRumuzEslemeByIslemAdimi başarısız : %d".formatted(response.getResponseCode()));			
		}
		
		//rumuzEslemeleriResponse.print();
		
		return rumuzEslemeleriResponse;

	}
	
	public Rumuz getRumuzEdevletByIslemAdimi(int islemAdimi) {
		RumuzEslemeResponse rumuzEslemeResponse= getRumuzEslemeResponseByIslemAdimi(islemAdimi);
		List<RumuzEsleme> eslemeler = rumuzEslemeResponse.getData();
		
		var opt = eslemeler.stream().filter(
				p-> p.getStatu()==1 
				&& p.getRumuz()!=null
				&& p.getRumuz().getStatu()==1
				&& p.getRumuz().getEdevletrumuz()!=null
				&& p.getRumuz().getTckimlik()!=null
				&& p.getRumuz().getTckimlik().length()==11
				&& p.getRumuz().getSifre()!=null
				)
			.findAny();
		
		if (opt.isEmpty()) {
			throw new RuntimeException("% islemi icin uygun edevlet rumuzu bulunamadi. ".formatted(islemAdimi));
		}
		
		return opt.get().getRumuz();
		
	}
	
	public Rumuz getRumuzKrediKartiByIslemAdimi(int islemAdimi) {
		RumuzEslemeResponse rumuzEslemeResponse= getRumuzEslemeResponseByIslemAdimi(islemAdimi);
		List<RumuzEsleme> eslemeler = rumuzEslemeResponse.getData();
		
		var opt = eslemeler.stream().filter(
				p-> p.getStatu()==1 
				&& p.getRumuz()!=null
				&& p.getRumuz().getStatu()==1
				&& p.getRumuz().getKredikartirumuz()!=null
				&& p.getRumuz().getKredikartino()!=null
				&& p.getRumuz().getKredikartino().trim().replace(" ", "").length()==16
				&& p.getRumuz().getSonkullanimtarihi()!=null
				&& p.getRumuz().getCcv()!=null
				)
			.findAny();
		
		if (opt.isEmpty()) {
			throw new RuntimeException("% islemi icin uygun kredi karti rumuzu bulunamadi. ".formatted(islemAdimi));
		}
		
		return opt.get().getRumuz();
		
	}
	

	
}
