package com.robodo.ui;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessDefinitionStep;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.runner.RunnerUtil;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route("/process")
@SpringComponent
@UIScope
public class UIProcessor extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	ProcessService processService;
	Environment env;

	Grid<ProcessDefinition> gridProcess;
	Grid<ProcessDefinitionStep> gridProcessSteps;
	Grid<ProcessInstance> gridProcessInstance;

	@Autowired
	public UIProcessor(ProcessService processService, Environment env) {

		this.processService = processService;
		this.env = env;

		gridProcess = new Grid<>(ProcessDefinition.class, false);
		gridProcess.addColumn(p -> p.getId()).setHeader("#");
		gridProcess.addColumn(p -> p.getCode()).setHeader("Code");
		gridProcess.addColumn(p -> p.getDescription()).setHeader("Description");
		gridProcess.addComponentColumn(p -> {
			Checkbox chActive=new Checkbox(p.isActive());
			chActive.addValueChangeListener(e->{
				boolean newVal = e.getValue();
				p.setActive(newVal);
				boolean isOk = processService.saveProcessDefinition(p);
				if (!isOk) {
					notifyError("Error saving");
				} else {
					notifySuccess("process is %s".formatted(newVal ? "active" : "pasive"));
				}
			});
			return chActive;
		}).setHeader("Active");
		gridProcess.addColumn(p -> p.getMaxRetryCount()).setHeader("Max Retry");
		gridProcess.addColumn(p -> p.getMaxThreadCount()).setHeader("Max Thread");
		gridProcess.addColumn(p -> p.getDiscovererClass()).setHeader("Discoverer");

		gridProcess.addComponentColumn(p -> {
			Button btnRun = new Button("Discover", new Icon(VaadinIcon.SEARCH));
			btnRun.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
			btnRun.setDisableOnClick(true);
			btnRun.addClickListener(e -> {
				gridProcess.select(p);
				runProcessDiscoverer(p);
				btnRun.setEnabled(true);
			});
			return btnRun;
		}).setHeader("Actions");
		

		gridProcess.addSelectionListener(p -> {
			Optional<ProcessDefinition> selection = p.getFirstSelectedItem();
			if (selection.isEmpty()) {
				gridProcessSteps.setItems(Collections.emptyList());
				gridProcessInstance.setItems(Collections.emptyList());
			} else {
				/*
				gridProcessSteps.setItems(selection.get().getSteps());
				gridProcessInstance.setItems(selection.get().getInstances());
				*/
				setData(selection.get());
			}
		});
		
		
		//--------------------------------------------------------------------

		gridProcessSteps = new Grid<>(ProcessDefinitionStep.class, false);
		gridProcessSteps.addColumn(p -> p.getId()).setHeader("#");
		gridProcessSteps.addColumn(p -> p.getCode()).setHeader("Code");
		gridProcessSteps.addColumn(p -> p.getOrderNo()).setHeader("Order");
		gridProcessSteps.addColumn(p -> p.getDescription()).setHeader("Description");
		gridProcessSteps.addColumn(p -> p.getCommands()).setHeader("Command to run");
		
		
		//--------------------------------------------------------------------

		gridProcessInstance = new Grid<>(ProcessInstance.class, false);
		gridProcessInstance.addColumn(p -> p.getId()).setHeader("#");
		gridProcessInstance.addColumn(p -> p.getCode()).setHeader("Code");
		gridProcessInstance.addColumn(p -> p.getDescription()).setHeader("Description");
		gridProcessInstance.addColumn(p -> p.getStatus()).setHeader("Status");
		gridProcessInstance.addColumn(p -> p.getCurrentStepCode()).setHeader("Latest Step");
		gridProcessInstance.addColumn(p -> p.getRetryNo()).setHeader("Retried#");
		gridProcessInstance.addColumn(p -> p.getCreated()).setHeader("Created");
		gridProcessInstance.addColumn(p -> p.getStarted()).setHeader("Started");
		gridProcessInstance.addColumn(p -> p.getFinished()).setHeader("Finished");
		gridProcessInstance.addComponentColumn(p -> {
			Button btnShowVars = new Button("", new Icon(VaadinIcon.LIST));
			btnShowVars.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
			btnShowVars.setEnabled(!p.getStatus().equals(ProcessInstance.END));
			btnShowVars.setDisableOnClick(true);
			btnShowVars.addClickListener(e -> {
				gridProcessInstance.select(p);
				showVariables("instance variable for %s".formatted(p.getCode()),p.getInstanceVariables());
				btnShowVars.setEnabled(true);
			});
			return btnShowVars;
		}).setHeader("Vars");
		gridProcessInstance.addComponentColumn(p -> {
			Button btnShowSteps = new Button("", new Icon(VaadinIcon.OPEN_BOOK));
			btnShowSteps.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
			btnShowSteps.setDisableOnClick(true);
			btnShowSteps.addClickListener(e -> {
				gridProcessInstance.select(p);
				showProcessInstanceSteps("instance variable for %s".formatted(p.getCode()),p.getSteps());
				btnShowSteps.setEnabled(true);
			});
			return btnShowSteps;
		}).setHeader("Steps");
		
		
		gridProcessInstance.addComponentColumn(p -> {
			Button btnApprove = new Button("Release", new Icon(VaadinIcon.CHECK));
			btnApprove.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
			if (p.getStatus().equals(ProcessInstance.STATUS_RUNNING)) {
				Optional<ProcessInstanceStep> instanceStepOpt =  p.getSteps().stream().filter(i->i.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING)).findFirst();
				if (instanceStepOpt.isEmpty()) {
					btnApprove.setEnabled(false);
				} else {
					ProcessInstanceStep instanceStep = instanceStepOpt.get();
					boolean enabled = instanceStepOpt.get().getCommands().contains("waitHumanInteraction") && !instanceStep.isApproved();
					btnApprove.setEnabled(enabled);
				}
				
			} else {
				btnApprove.setEnabled(false);
			}
			
			
			btnApprove.setDisableOnClick(true);
			btnApprove.addClickListener(e -> {
				gridProcessInstance.select(p);
				approveProcessInstance(p);
				btnApprove.setEnabled(true);
			});
			return btnApprove;
		}).setHeader("Release");
		
		
		gridProcessInstance.addComponentColumn(p -> {
			Button btnRun = new Button("Run Instance", new Icon(VaadinIcon.PLAY));
			btnRun.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
			btnRun.setDisableOnClick(true);
			btnRun.addClickListener(e -> {
				gridProcessInstance.select(p);
				runProcessInstance(p);
				btnRun.setEnabled(true);
			});
			btnRun.setEnabled(!p.getStatus().equals(ProcessInstance.END));
			return btnRun;
		}).setHeader("Run");

		
		gridProcess.setWidthFull();
		gridProcessSteps.setWidthFull();
		gridProcessInstance.setWidthFull();
		
		
		HorizontalLayout horizontalLay = new HorizontalLayout(gridProcess,gridProcessSteps);
		horizontalLay.setWidthFull();
		horizontalLay.setFlexGrow(.7, gridProcess);
		


		VerticalLayout verticalLay = new VerticalLayout();
		verticalLay.setWidthFull();
		verticalLay.setHeightFull();
		verticalLay.add(new H3("Processes"));
		verticalLay.add(horizontalLay);
		verticalLay.add(new H4("Instances"));
		verticalLay.add(gridProcessInstance);
		add(verticalLay);
		setSizeFull();

		fillGrid();

	}

	

	private void approveProcessInstance(ProcessInstance instance) {
		Optional<ProcessInstanceStep> stepOpt = instance.getSteps().stream().filter(p->p.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING)).findFirst();
		if (stepOpt.isEmpty()) {
			return;
		}
		
		ProcessInstanceStep step = stepOpt.get();
		step.setApproved(true);
		step.setApprovalDate(LocalDateTime.now());
		step.setApprovedBy("TBD");
		processService.saveProcessInstance(instance);
		
	}



	private void showProcessInstanceSteps(String title, List<ProcessInstanceStep> steps) {
		Dialog dialog = new Dialog();
		dialog.setHeaderTitle(title);

		Grid<ProcessInstanceStep> grid=new Grid<>(ProcessInstanceStep.class, false);
		grid.addColumn(p -> p.getId()).setHeader("#").setFlexGrow(0);
		grid.addColumn(p -> p.getStepCode()).setHeader("Code").setFlexGrow(1);
		grid.addColumn(p -> p.getOrderNo()).setHeader("Order");
		grid.addColumn(p -> p.getStatus()).setHeader("Status");
		grid.addColumn(p -> p.getApprovedBy()).setHeader("Approved By");
		grid.addColumn(p -> p.getApprovalDate()).setHeader("Approval Date");
		grid.addColumn(p -> p.getCreated()).setHeader("Created");
		grid.addColumn(p -> p.getStarted()).setHeader("Started");
		grid.addColumn(p -> p.getFinished()).setHeader("Finished");
		grid.addColumn(p -> p.getCommands()).setHeader("Command");

		grid.setItems(steps);
		dialog.add(grid);
		
		TextArea content=new TextArea();
		content.setSizeFull();
		dialog.add(content);
		
		
		grid.addSelectionListener(p -> {
			Optional<ProcessInstanceStep> selection = p.getFirstSelectedItem();
			if (selection.isEmpty()) {
				content.setValue("");
			} else {
				var logs=selection.get().getLogs();
				content.setValue(logs==null ? "" : logs);
			}
		});
		
		
		Button cancelButton = new Button("Cancel", e -> dialog.close());
		dialog.getFooter().add(cancelButton);
		dialog.setSizeFull();
		dialog.open();
		
	}



	private void showVariables(String title, String data) {
		Dialog dialog = new Dialog();
		dialog.setHeaderTitle(title);
		VerticalLayout dialogLayout = new VerticalLayout();
		Label content=new Label(data==null ? ""  : data);
		content.setSizeFull();
		dialogLayout.add(content);
		dialog.add(dialogLayout);
		Button cancelButton = new Button("Cancel", e -> dialog.close());
		dialog.getFooter().add(cancelButton);
		dialog.setSizeFull();
		dialog.open();
		
	}






	private void fillGrid() {
		List<ProcessDefinition> processDefinitions = processService.getProcessDefinitions();
		gridProcess.setItems(processDefinitions);

		if (!processDefinitions.isEmpty()) {
			setData(processDefinitions.get(0));
		}

	}

	private void setData(ProcessDefinition processDefinition) {
		gridProcess.select(processDefinition);
		List<ProcessDefinitionStep> definitionSteps = processDefinition.getSteps();
		Collections.sort(definitionSteps, new Comparator<ProcessDefinitionStep>() {

			@Override
			public int compare(ProcessDefinitionStep o1, ProcessDefinitionStep o2) {
				return o1.getOrderNo().compareTo(o2.getOrderNo());
			}
		});
		gridProcessSteps.setItems(definitionSteps);

		List<ProcessInstance> instances = processDefinition.getInstances();
		Collections.sort(instances, new Comparator<ProcessInstance>() {
			@Override
			public int compare(ProcessInstance o1, ProcessInstance o2) {
				return o1.getCreated().compareTo(o2.getCreated());
			}
		});

		gridProcessInstance.setItems(instances);
	}

	private void runProcessDiscoverer(ProcessDefinition processDefinition) {
		
		String processId="DISCOVERY.%s".formatted(processDefinition.getCode());
		boolean isRunning = RunnerSingleton.getInstance().hasRunningInstance(processId);
		if (isRunning) {
			notifyError("Discovery is already running");
            return;
		}
		
		RunnerUtil runner = new RunnerUtil(processService, env);
		
		RunnerSingleton.getInstance().start(processId);
		List<ProcessInstance> discoveredInstances = runner.runProcessDiscovery(processDefinition);
		for (ProcessInstance discoveredInstance : discoveredInstances) {
			processService.saveProcessInstance(discoveredInstance);
		}
		
		RunnerSingleton.getInstance().stop(processId);
		
		fillGrid();
		setData(processDefinition);
	}
	
	
	private void notifySuccess(String content) {
		Notification notification = Notification.show(content);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_START);
	}
	
	private void notifyError(String content) {
		Notification notification = Notification.show(content);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);		
	}





	private void runProcessInstance(ProcessInstance processInstance) {
		RunnerUtil runner = new RunnerUtil(processService, env);
		ProcessInstance processInstanceAfterRun = runner.runProcessInstance(processInstance);
		if (processInstanceAfterRun == null) {
			notifySuccess("Failed to start");
		}
		
		processService.saveProcessInstance(processInstanceAfterRun);
		fillGrid();
		setData(processInstance.getProcessDefinition());
		
	}

}
