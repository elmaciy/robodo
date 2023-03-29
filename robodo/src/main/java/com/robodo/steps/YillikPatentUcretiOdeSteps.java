package com.robodo.steps;

import com.robodo.pages.PageEdevletLogin;
import com.robodo.pages.PageEpatsBenimSayfam;
import com.robodo.pages.PageEpatsHome;
import com.robodo.pages.PageEpatsMenu;
import com.robodo.runner.RunnerUtil;

public class YillikPatentUcretiOdeSteps extends BaseSteps {
	
	PageEpatsHome home;
	PageEdevletLogin edevletLogin;
	PageEpatsMenu  epatsMenu;
	PageEpatsBenimSayfam epatsBenimSayfam;
	
	public YillikPatentUcretiOdeSteps(RunnerUtil runnerUtil) {
		super(runnerUtil);
	
	}

	private void sistemeGiris() {
		home.open();
		home.clickEdevlet();
		edevletLogin.girisEdevlet("18878152684", "hanife123");
		
	}
	
	@Override
	public void run() {
		selenium.startWebDriver();
		this.home=new PageEpatsHome(selenium);
		this.edevletLogin=new PageEdevletLogin(selenium);
		this.epatsMenu=new PageEpatsMenu(selenium);
		this.epatsBenimSayfam=new PageEpatsBenimSayfam(selenium);
		
		sistemeGiris();

		epatsMenu.gotoBenimSayfam();
		epatsBenimSayfam.basvuruArama("123ssaad");
		selenium.sleep(10L);
		epatsMenu.cikis();
		selenium.sleep(10L);
		
		selenium.stopDriver();
	}



	

}
