package com.robodo.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.ProcessInstance;
import com.robodo.model.UserRole;
import com.robodo.security.SecurityService;
import com.robodo.services.ProcessService;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "/dashboard")
@RolesAllowed(UserRole.ROLE_USER)
public class UIDashboard extends UIBase  {

	private static final long serialVersionUID = 1L;

	ProcessInstance processInstance;

	@Autowired
	public UIDashboard(ProcessService processService, SecurityService securityService) {
		super(processService, securityService);
		setTitle("Dashboard", VaadinIcon.DASHBOARD.create());
		
		 drawScreen();
	}



	private void drawScreen() {
		removeAll();
		


		
	}



}
