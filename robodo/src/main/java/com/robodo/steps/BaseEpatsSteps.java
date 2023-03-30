package com.robodo.steps;

import com.robodo.pages.PageEdevletLogin;
import com.robodo.pages.PageEpatsBenimSayfam;
import com.robodo.pages.PageEpatsHome;
import com.robodo.pages.PageEpatsMenu;
import com.robodo.pages.PageEpatsTahakkuklarim;
import com.robodo.runner.RunnerUtil;

public class BaseEpatsSteps extends BaseSteps {
	
	PageEpatsHome home;
	PageEdevletLogin edevletLogin;
	PageEpatsMenu  epatsMenu;
	PageEpatsBenimSayfam epatsBenimSayfam;
	PageEpatsTahakkuklarim epatsTahakkuklarim;
	
	public BaseEpatsSteps(RunnerUtil runnerUtil) {
		super(runnerUtil);
		selenium.startWebDriver();
		this.home=new PageEpatsHome(selenium);
		this.edevletLogin=new PageEdevletLogin(selenium);
		this.epatsMenu=new PageEpatsMenu(selenium);
		this.epatsBenimSayfam=new PageEpatsBenimSayfam(selenium);
		this.epatsTahakkuklarim=new PageEpatsTahakkuklarim(selenium);
	
	}

	public void sistemeGiris() {
		home.open();
		home.clickEdevlet();
		String tckno=runnerUtil.getEnvironmentParameter("tckno");
		String sifre=runnerUtil.getEnvironmentParameter("sifre");
		edevletLogin.girisEdevlet(tckno, sifre);
		
	}
	
	@Override
	public void run() {
	}



	

}
