package com.robodo.steps;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.pages.PageEdevletLogin;
import com.robodo.pages.PageEpatsBasvuruYapan;
import com.robodo.pages.PageEpatsBenimSayfam;
import com.robodo.pages.PageEpatsDosyaBilgisi;
import com.robodo.pages.PageEpatsHizmetDokumu;
import com.robodo.pages.PageEpatsHome;
import com.robodo.pages.PageEpatsIslemSonucu;
import com.robodo.pages.PageEpatsItirazGerekceleri;
import com.robodo.pages.PageEpatsItirazSahibiBilgisi;
import com.robodo.pages.PageEpatsItirazaIliskinBilgiler;
import com.robodo.pages.PageEpatsItirazaIliskinEkler;
import com.robodo.pages.PageEpatsMenu;
import com.robodo.pages.PageEpatsOnIzleme;
import com.robodo.pages.PageEpatsTahakkukOde;
import com.robodo.pages.PageEpatsTahakkuklarim;
import com.robodo.pages.PageEpatsTalepTuru;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;

public class BaseEpatsSteps extends BaseSteps {
	
	PageEpatsHome home;
	PageEdevletLogin edevletLogin;
	PageEpatsMenu  epatsMenu;
	PageEpatsBenimSayfam epatsBenimSayfam;
	PageEpatsTahakkuklarim epatsTahakkuklarim;
	PageEpatsBasvuruYapan epatsBasvuruYapan;
	PageEpatsDosyaBilgisi epatsDosyaBilgisi;
	PageEpatsHizmetDokumu epatsHizmetDokumu;
	PageEpatsOnIzleme epatsOnIzleme;
	PageEpatsIslemSonucu epatsIslemSonucu;
	PageEpatsTahakkukOde epatsTahakkukOde;
	PageEpatsTalepTuru epatsTalepTuru;
	PageEpatsItirazSahibiBilgisi epatsItirazSahibiBilgisi;
	PageEpatsItirazGerekceleri epatsItirazGerekceleri;
	PageEpatsItirazaIliskinBilgiler epatsItirazaIliskinBilgiler;
	PageEpatsItirazaIliskinEkler epatsItirazaIliskinEkler;
	
	public BaseEpatsSteps(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
		selenium.startWebDriver();
		this.home=new PageEpatsHome(selenium);
		this.edevletLogin=new PageEdevletLogin(selenium);
		this.epatsMenu=new PageEpatsMenu(selenium);
		this.epatsBenimSayfam=new PageEpatsBenimSayfam(selenium);
		this.epatsTahakkuklarim=new PageEpatsTahakkuklarim(selenium);
		this.epatsBasvuruYapan=new PageEpatsBasvuruYapan(selenium);
		this.epatsDosyaBilgisi=new PageEpatsDosyaBilgisi(selenium);
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

	public void sistemeGiris() {
		home.open();
		home.clickEdevlet();
		String tckno=runnerUtil.getEnvironmentParameter("tckno");
		String sifre=runnerUtil.getEnvironmentParameter("sifre");
		edevletLogin.girisEdevlet(tckno, sifre);
		takeStepScreenShot(processInstanceStep, "Sisteme giriş yapıldı", false);
	}
	
	
	public void dosyaAra() {
		epatsMenu.gotoVekillikSayfam();
		String dosyaNo=getVariable("dosyaNumarasi");
		String basvuruTuru=getVariable("basvuruTuru");
		epatsBenimSayfam.dosyaArama(dosyaNo,basvuruTuru);
		takeStepScreenShot(this.processInstanceStep, "Dosya arama sonucu", false);
		
	}
	
	public void islemSec() {
		String islemGrubu=getVariable("islemGrubu");
		String islemAdi=getVariable("islemAdi");
		epatsBenimSayfam.islemSec(islemGrubu,islemAdi);
		takeStepScreenShot(this.processInstanceStep, "Işlem Secimi", false);
	}
	
	
	
	public void basvuruYap() {
		String eposta=getVariable("eposta");;
		String cepTel=runnerUtil.getEnvironmentParameter("ceptel");
		String referansNo=getVariable("takipNumarasi");
		epatsBasvuruYapan.basvuruBilgileriniDoldur(eposta, cepTel, referansNo);
		takeStepScreenShot(this.processInstanceStep, "Başvuru bilgileri", false);
		epatsBasvuruYapan.devamEt();
	}
	
	boolean isBulus() {
		return "PATENT".contains(getVariable("basvuruTuru"));
	}
	
	boolean isMarka() {
		return "MARKA".contains(getVariable("basvuruTuru"));
	}
	
	public void dosyaBilgisiAra() {
		epatsDosyaBilgisi.basvuruNumarasiAra(getVariable("dosyaNumarasi"));
	}
	
	public void dosyaBilgisiDogrulaDevamEt() {
		setVariable("dosyabilgisi.dosyabilgisi.basvuruNumarasi", epatsDosyaBilgisi.getBasvuruNumarasi());
		setVariable("dosyabilgisi.dosyabilgisi.basvuruTarihi", epatsDosyaBilgisi.getBasvuruTarihi());
		
		if (isBulus()) {
			setVariable("dosyabilgisi.dosyabilgisi.bulusBasligi", epatsDosyaBilgisi.getBulusBasligi());
		}
		if (isMarka()) {
			setVariable("dosyabilgisi.dosyabilgisi.markaAdi", epatsDosyaBilgisi.getmarkaAdi());
		}		
		
		karsilastir(getVariable("dosyabilgisi.dosyabilgisi.basvuruNumarasi"), getVariable("dosyaNumarasi"), "dosya numarası karşılaştırılıyor");
		
		if (isBulus()) {
			karsilastir(getVariable("dosyabilgisi.dosyabilgisi.bulusBasligi"), getVariable("bulusAdi"), "buluş adı karşılaştırılıyor");
		}
		
		if (isMarka()) {
			karsilastir(getVariable("dosyabilgisi.dosyabilgisi.markaAdi"), getVariable("markaAdi"), "marka adı karşılaştırılıyor");
		}

		
		takeStepScreenShot(this.processInstanceStep, "Dosya bilgisi", false);
		epatsDosyaBilgisi.devamEt();
	}
	
	public void itirazSahibiEkleDevamEt() {
		String itirazSahibiAdi= getVariable("itirazSahibiAdi");
		String itirazSahibiKimlikNo = getVariable("itirazSahibiKimlikNo");
		
		epatsItirazSahibiBilgisi.itirazSahibiEkle(itirazSahibiAdi, itirazSahibiKimlikNo);
		

		epatsItirazSahibiBilgisi.devamEt();
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
	}
	
	public void itirazaGerekceDosyalariEkleDevamEt() {
		String itirazaGerekceDosyaNumaralari=getVariable("itirazaGerekceDosyaNumaralari");
		if (itirazaGerekceDosyaNumaralari==null || itirazaGerekceDosyaNumaralari.strip().isBlank()) {
			runnerUtil.logger("itiraza gerekce dosya belirtilmediğinden bu adım geçiliyor. ");
			return;
		}
		List<String> dosyaNumaralari = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(itirazaGerekceDosyaNumaralari);
		dosyaNumaralari.forEach(dosya->{
			epatsItirazGerekceleri.itirazaGerekceDosyaEkle(dosya);
		});
		
		epatsItirazGerekceleri.devamEt();
	}
	
	public void itirazaIliskibBilgileriEkleDevamEt() {
		String itirazaIliskinDosya=getVariable("itirazaIliskinDosya");
		epatsItirazaIliskinBilgiler.itirazaIliskinEvrakYukle(itirazaIliskinDosya);
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
		takeStepScreenShot(this.processInstanceStep, "Hizmet dökümü", true);
		selenium.sleep(3L);
		epatsHizmetDokumu.devamEt();
	}

	public void onizlemeKontrolveTahakkukOlustur() {
		
		setVariable("onizleme.odenecek.dosyaNumarasi", epatsOnIzleme.getDosyaNumarasi());
		setVariable("onizleme.odenecek.referansNumarasi", epatsOnIzleme.getRefeansTakipNumarasi());
		
		if (isBulus()) {			
			setVariable("onizleme.odenecek.bulusBasligi", epatsOnIzleme.getBulusBasligi());
		}

		if (isMarka()) {
			setVariable("onizleme.odenecek.markaAdi", epatsOnIzleme.getMarkaAdi());
		}
		
		setVariable("onizleme.odenecek.genelToplam", epatsOnIzleme.getGenelToplamTutari());
		
		
		karsilastir(getVariable("onizleme.odenecek.dosyaNumarasi"), getVariable("dosyaNumarasi"), "dosya numarası karşılaştırılıyor");
		karsilastir(getVariable("onizleme.odenecek.referansNumarasi"), getVariable("takipNumarasi"), "başvuru takip/referans numarası karşılaştırılıyor");
		
		if (isBulus()) {			
			karsilastir(getVariable("onizleme.odenecek.bulusBasligi"), getVariable("bulusAdi"), "buluş adı karşılaştırılıyor");
		}
		
		if (isMarka()) {			
			karsilastir(getVariable("onizleme.odenecek.markaAdi"), getVariable("markaAdi"), "marka adı karşılaştırılıyor");
		}
		
		karsilastir(HelperUtil.normalizeAmount(getVariable("onizleme.odenecek.genelToplam")), HelperUtil.normalizeAmount(getVariable("odemeTutari")), "ödenecek tutar karşılaştırılıyor");
		
		takeStepScreenShot(this.processInstanceStep, "Önizleme", true);
		epatsOnIzleme.tahakkukOlustur();
		
	}
	
	public void  tahakkukNumarasiAl() {
		String sonuc = epatsIslemSonucu.sonucAl();
		setVariable("islemsonucu.sonuc", sonuc);
		takeStepScreenShot(this.processInstanceStep, "Tahakkuk numarası oluşturuldu", true);
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

	
	@Override
	public void run() {
	}



	

}
