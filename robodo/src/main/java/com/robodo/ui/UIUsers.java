package com.robodo.ui;

import java.text.DateFormat;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.ProcessInstance;
import com.robodo.model.User;
import com.robodo.model.UserRole;
import com.robodo.security.SecurityService;
import com.robodo.services.ProcessService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "/users")
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

	Grid<User> grid = new Grid<>(User.class, false);

	private void drawScreen() {
		
		removeAll();
		makeGrid();
		
		grid.setSizeFull();
		
		add(grid);

	}

	private void makeGrid() {
		grid.addColumn(p->p.getUsername()).setHeader("Username").setSortable(true).setAutoWidth(true);
		grid.addColumn(p->p.getEmail()).setHeader("Email").setSortable(true).setAutoWidth(true);
		grid.addColumn(p->p.getFullname()).setHeader("Full Name").setSortable(true).setAutoWidth(true);
		grid.addColumn(p->makeTrueFalseIcon(p.isValid(), VaadinIcon.CHECK.create(), VaadinIcon.CLOSE.create())).setHeader("Username").setSortable(true).setAutoWidth(true);
		grid.addColumn(p->p.getRoles()).setHeader("Roles").setAutoWidth(true);
		grid.addColumn(p->dateFormat(p.getLastLogin())).setHeader("Last Login").setAutoWidth(true);
		grid.addColumn(p->dateFormat(p.getLastPasswordChange())).setHeader("Last Password Change").setAutoWidth(true);
		grid.addColumn(p->dateFormat(p.getCreated())).setHeader("Created").setAutoWidth(true);
		grid.addColumn(p->dateFormat(p.getUpdated())).setHeader("Updated").setAutoWidth(true);
		
		
		grid.setItems(processService.getUsersAll());
		
	}



}
