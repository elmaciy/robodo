package com.robodo.ui;

import com.robodo.security.SecurityService;
import com.robodo.services.ProcessService;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login") 
@PageTitle("Login | Vaadin CRM")
@AnonymousAllowed
public class UILogin extends UIBase implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;
	LoginOverlay login;
	
	public UILogin(ProcessService processService, SecurityService securityService) {
		super(processService, securityService);
		
		addClassName("login-view");

		login = new LoginOverlay();

		addClassName("login-view");
		login.setAction("login");


		login.setTitle("Welcome to Robo.do");
		login.setDescription("Yet another robotic automation tool");
		add(login);
		login.setOpened(true);		
	}
	
	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		// inform the user about an authentication error
		if(beforeEnterEvent.getLocation()  
        .getQueryParameters()
        .getParameters()
        .containsKey("error")) {
            login.setError(true);
        }
	}
	

}
