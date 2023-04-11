package com.robodo.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.ProcessInstance;
import com.robodo.model.UserRole;
import com.robodo.security.SecurityService;
import com.robodo.services.ProcessService;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "/users")
@SpringComponent
@UIScope
@RolesAllowed(UserRole.ROLE_ADMIN)
public class UIUsers extends UIBase  {

	private static final long serialVersionUID = 1L;

	ProcessInstance processInstance;

	@Autowired
	public UIUsers(ProcessService processService, SecurityService securityService) {
		super(processService, securityService);
		setTitle("Users");
		
		 drawScreen();
	}



	private void drawScreen() {
		removeAll();
		


		
	}



}
