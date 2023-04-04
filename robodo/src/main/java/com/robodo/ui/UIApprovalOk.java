package com.robodo.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.ProcessInstance;
import com.robodo.services.ProcessService;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route(value = "/approvalresult")
@PageTitle("Robodo - Approval Result")
@SpringComponent
@UIScope
public class UIApprovalOk extends UIBase {

	private static final long serialVersionUID = 1L;

	ProcessService processService;
	ProcessInstance processInstance;

	@Autowired
	public UIApprovalOk(ProcessService processService) {
		super(processService);
		
		this.processService=processService;
		 
		 
		drawScreen();
	}


	private void drawScreen() {
		Label result = new Label("Thank you!");
		add(result);
	}



}
