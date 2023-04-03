package com.robodo.ui;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.services.ProcessService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route("/approve/:instanceId/:action")
@SpringComponent
@UIScope
public class UIApprover extends UIBase implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;

	ProcessService processService;
	ProcessInstance processInstance;
	
	String action;
	String instanceId;



	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Iterator<String> it = event.getRouteParameters().getParameterNames().iterator();
		while (it.hasNext()) {
			String param = it.next();
			String value=event.getRouteParameters().get(param).get();
			UI.getCurrent().getSession().setAttribute(param, value);
		}
	}
	

	@Autowired
	public UIApprover(ProcessService processService) {
		super(processService);
		 this.processService=processService;
		 this.action=(String) UI.getCurrent().getSession().getAttribute("action");
		 this.instanceId=(String) UI.getCurrent().getSession().getAttribute("instanceId");
		
		 if (instanceId==null) {
			 notifyError("instance is not given");
			 return;
		 }
		 
		 if (action==null) {
			 notifyError("action is not given");
			 return;
		 }
		 
		 if ("APPROVE,DECLINE,VIEW".indexOf(action)==-1) {
			 notifyError("action is not defined : %s".formatted(action));
			 return;
		 }
		 
		 processInstance = processService.getProcessInstanceByCode(instanceId);
		 if (processInstance==null) {
			 notifyError("no instance found");
			 return;
		 }
		 
		 
		 drawScreen();
	}


	private void drawScreen() {
		Button btApprove = new Button("APPROVE", new Icon(VaadinIcon.CHECK));
		btApprove.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_LARGE);
		btApprove.setDisableOnClick(true);
		btApprove.setEnabled(isApproveable(processInstance));
		btApprove.addClickListener(e -> {
			approve(processInstance);
		});
		
		Button btDecline = new Button("DECLINE", new Icon(VaadinIcon.CLOSE));
		btDecline.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_LARGE);
		btDecline.setDisableOnClick(true);
		btDecline.setEnabled(isApproveable(processInstance));
		btDecline.addClickListener(e -> {
			decline(processInstance);
		});
		
		
		
		VerticalLayout verticalLay = new VerticalLayout();
		verticalLay.setWidthFull();
		verticalLay.setHeightFull();
		HorizontalLayout buttonsLayout=new HorizontalLayout(btApprove,btDecline);
		buttonsLayout.setWidthFull();
		verticalLay.add(buttonsLayout);
		
		

		VerticalLayout instanceLay= makeInstanceLayout(processInstance);
		verticalLay.add(new H3("PROCESS INSTANCE %s (%s)".formatted(processInstance.getCode(),processInstance.getDescription())));
		verticalLay.add(instanceLay);

		add(verticalLay);
		setSizeFull();
		
		getElement().getStyle().set("height", "100%");
		
		boolean toApprove=List.of("APPROVE","DECLINE").indexOf(action)>-1;
		if (toApprove) {
			if (action.equals("APPROVE")) {
				btApprove.click();
			}
			
			if (action.equals("DECLINE")) {
				btDecline.click();
			}
		}
		
	}

	private boolean isApproveable(ProcessInstance processInstance) {
		Optional<ProcessInstanceStep> opt = processInstance.getSteps().stream()
				.filter(p-> p.getCommands().startsWith("waitHumanInteraction"))
				.filter(p-> p.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING))
				.filter(p-> !p.isApproved())
				.findFirst();
		return opt.isPresent();
	}
	
	private void approve(ProcessInstance processInstance) {
		if (!isApproveable(processInstance)) {
			notifyError("not approveable anymore");
			return;
		}
		
		confirmAndRun("Confirm","Are you sure to APPROVE this instance?",()->doApproval(processInstance, true));
	}

	


	private void decline(ProcessInstance processInstance) {
		if (!isApproveable(processInstance)) {
			notifyError("not approveable anymore");
			return;
		}
		confirmAndRun("Confirm","Are you sure to DECLINE this instance?",()->doApproval(processInstance, false));

		
	}
	
	private VerticalLayout makeInstanceLayout(ProcessInstance processInstance2) {
		VerticalLayout layout=new VerticalLayout();
		
		return layout;
	}
	
	

	private ProcessInstanceStep getStepToApprove(ProcessInstance processInstance2) {
		Optional<ProcessInstanceStep> opt = processInstance.getSteps().stream()
				.filter(p-> p.getCommands().startsWith("waitHumanInteraction"))
				.filter(p-> p.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING))
				.filter(p-> !p.isApproved())
				.findFirst();
		return opt.isEmpty() ? null : opt.get();
	}


	
	
	private void doApproval(ProcessInstance processInstance, boolean approved) {
		ProcessInstanceStep stepForApproval = getStepToApprove(processInstance);
		if (stepForApproval==null) {
			notifyError("no step to approve");
			return;
		}
		
		stepForApproval.setStatus(ProcessInstanceStep.STATUS_COMPLETED);
		stepForApproval.setApproved(approved);
		stepForApproval.setApprovalDate(LocalDateTime.now());
		stepForApproval.setApprovedBy("TBD");
		
		if (!approved) {
			processInstance.setFinished(LocalDateTime.now());
			processInstance.setStatus(ProcessInstance.STATUS_COMPLETED);
			processInstance.setError(approved ? null : "declined by user");
		} 
		
		
		processService.saveProcessInstance(processInstance);
		
		UI.getCurrent().getPage().reload();
	}
	

	

}
