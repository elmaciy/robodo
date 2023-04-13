package com.robodo.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.ProcessInstance;
import com.robodo.model.UserRole;
import com.robodo.security.SecurityService;
import com.robodo.services.ProcessService;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "/parameters")
@RolesAllowed(UserRole.ROLE_ADMIN)
public class UIParameters extends UIBase  {

	private static final long serialVersionUID = 1L;

	ProcessInstance processInstance;

	@Autowired
	public UIParameters(ProcessService processService, SecurityService securityService) {
		super(processService, securityService);
		setTitle("Parameters");
		
		 drawScreen();
	}



	private void drawScreen() {
		removeAll();
		


		
	}



}
