package com.robodo.ui;

import com.robodo.model.User;
import com.robodo.services.ProcessService;
import com.robodo.services.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
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
		login.setTitle("Robo.do");
		login.setDescription("Yet another robotic automation tool");
		add(login);
		login.setOpened(true);
		login.addLoginListener(e->{
			String username = e.getUsername();
			String password = e.getPassword();
			User user=processService.getUserByUsernameAndPassword(username, password);
			if (user==null) {
				login.setError(true);
				return;
			}
			notifyInfo("logged in by %s".formatted(username));
			login.setError(false);
			VaadinSession.getCurrent().setAttribute(User.class,user);
			UI.getCurrent().navigate("/devamke");
		});
		
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
