package com.robodo.ui;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.services.ProcessService;
import com.robodo.singleton.QueueSingleton;
import com.robodo.utils.HelperUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route(value = "/approve/:instanceId/:action/:source")
@PageTitle("Robodo - Approval")
@SpringComponent
@UIScope
public class UIApprover extends UIBase   implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;

	ProcessService processService;
	ProcessInstance processInstance;
	
	String instanceId;
	String action;
	String source;

	@Autowired
	public UIApprover(ProcessService processService) {
		super(processService);
		
		this.processService=processService;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		RouteParameters routeParameters = event.getRouteParameters();
		Iterator<String> it = routeParameters.getParameterNames().iterator();

		while (it.hasNext()) {
			String parameterName=it.next();
			String value=routeParameters.get(parameterName).get();
			if (parameterName.equals("action")) action=value;
			if (parameterName.equals("instanceId")) instanceId=value;
			if (parameterName.equals("source")) source=value;
		}
				
		boolean isOk = checkParams();

		if (isOk) {
			 drawScreen();
		 }

	}

	private boolean checkParams() {

		if (instanceId==null) {
			notifyError("instance is not given");
			return false;
		} else {
			instanceId=HelperUtil.decrypt(instanceId);
		}
		 
		if (action==null) {
			notifyError("action is not given");
			return false;
		}
		 
		if ("APPROVE,DECLINE,VIEW".indexOf(action)==-1) {
			notifyError("invalid action : %s".formatted(action));
			return false;
		}
		
		if ("EMAIL,SCREEN".indexOf(source)==-1) {
			notifyError("invalid source: %s".formatted(source));
			return false;
		}
		 
		processInstance = processService.getProcessInstanceByCode(instanceId);
		if (processInstance==null) {
			notifyError("no instance found : %s".formatted(instanceId));
			return false;
		}

		return true;
	}

	private void drawScreen() {
		removeAll();
		
		Button btApprove = new Button("APPROVE", new Icon(VaadinIcon.CHECK));
		btApprove.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_LARGE);
		btApprove.setVisible(isApproveable(processInstance));
		btApprove.addClickListener(e -> {
			approve(processInstance);
		});
		
		Button btDecline = new Button("DECLINE", new Icon(VaadinIcon.CLOSE));
		btDecline.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_LARGE);
		btDecline.setVisible(isApproveable(processInstance));
		btDecline.addClickListener(e -> {
			decline(processInstance);
		});
		
		HorizontalLayout buttonsLayout=new HorizontalLayout(makeBackButton(), btApprove, btDecline);
		buttonsLayout.setWidthFull();
		buttonsLayout.setAlignItems(Alignment.CENTER);
		
		VerticalLayout instanceLay= makeInstanceLayout(processInstance);

		Scroller scroller=new Scroller(instanceLay);
		scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
		scroller.setWidth("80%");
		
		VerticalLayout main=new VerticalLayout();
		main.setSizeFull();
		main.setAlignItems(Alignment.CENTER);
		
		
		main.add(buttonsLayout);
		main.add(new H3("%s (%s)".formatted(processInstance.getCode(),processInstance.getDescription())));
		main.add(scroller);
		
		add(main);
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

	private Button makeBackButton() {
		Button btBack = new Button("<< Back", new Icon(VaadinIcon.BACKWARDS));
		btBack.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btBack.addClickListener(e -> {
			UI.getCurrent().navigate("/process");
		});
		btBack.setVisible(source.equals("SCREEN"));
		return btBack;
	}

	private boolean isApproveable(ProcessInstance processInstance) {
		Optional<ProcessInstanceStep> opt = processInstance.getSteps().stream()
				.filter(p-> p.isHumanInteractionStep())
				.filter(p-> p.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING))
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
	
	private VerticalLayout makeInstanceLayout(ProcessInstance processInstance) {
		VerticalLayout layout=new VerticalLayout();
		processInstance.getSteps().forEach(step->{
			List<ProcessInstanceStepFile> files= processService.getProcessInstanceStepFilesByStepId(step);
			
			if (!files.isEmpty()) {
				layout.add(new H1(step.getStepCode()));
				files.forEach(file->{
					Span title = new Span(file.getDescription());
					title.getElement().getThemeList().add("badge");
					title.setWidthFull();
					layout.add(title);
					layout.add(getImage(step, file));
				});
			}
		});
		
		return layout;
	}
	
	

	private ProcessInstanceStep getStepToApprove(ProcessInstance processInstance2) {
		Optional<ProcessInstanceStep> opt = processInstance.getSteps().stream()
				.filter(p-> p.isHumanInteractionStep())
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
		stepForApproval.setFinished(LocalDateTime.now());
		
		if (!approved) {
			processInstance.setStatus(ProcessInstance.STATUS_COMPLETED);
			processInstance.setError(approved ? null : "declined by user");
			processInstance.setFailed(true);
		} 
		
		
		processService.saveProcessInstance(processInstance);
		
		if (approved && !QueueSingleton.getInstance().inQueue(processInstance)) {
			QueueSingleton.getInstance().add(processInstance);
		}
		
		/*
		informAndRun("Completed", "%s successfully.".formatted(approved ? "Approved" : "Declined"), ()->{
			UI.getCurrent().navigate("/approve/%s/%s/SCREEN".formatted(HelperUtil.encrypt(instanceId),action));
		});
		*/
		this.action="VIEW";
		drawScreen();

	}

}
