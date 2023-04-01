package com.robodo.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.robodo.model.ExecutionResultsForInstance;
import com.robodo.model.KeyValue;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessDefinitionStep;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.threads.ThreadForUIUpdating;
import com.robodo.utils.RunnerUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextArea;
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
		
		startUpdaterThread();

		this.processService = processService;
		this.env = env;

		gridProcess = new Grid<>(ProcessDefinition.class, false);
		gridProcess.addColumn(p -> p.getId()).setHeader("#").setWidth("3em");
		gridProcess.addColumn(p -> p.getCode()).setHeader("Code").setAutoWidth(true);
		gridProcess.addColumn(p -> p.getDescription()).setHeader("Description").setAutoWidth(true);
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
		}).setHeader("Active").setWidth("2em");
		gridProcess.addColumn(p -> p.getMaxRetryCount()).setHeader("Retry").setWidth("1em");
		gridProcess.addColumn(p -> p.getMaxThreadCount()).setHeader("Thread").setWidth("1em");
		gridProcess.addColumn(p -> p.getDiscovererClass()).setHeader("Discoverer");

		gridProcess.addComponentColumn(p -> {
			Button btnRun = new Button("Discover", new Icon(VaadinIcon.SEARCH));
			btnRun.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
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
		gridProcessSteps.addColumn(p -> p.getId()).setHeader("#").setWidth("2em");
		gridProcessSteps.addColumn(p -> p.getOrderNo()).setHeader("Order").setWidth("2em");
		gridProcessSteps.addColumn(p -> p.getCode()).setHeader("Code").setAutoWidth(true);
		gridProcessSteps.addColumn(p -> p.getDescription()).setHeader("Description").setAutoWidth(true);
		gridProcessSteps.addColumn(p -> p.getCommands()).setHeader("Command to run").setAutoWidth(true);
		
		
		//--------------------------------------------------------------------
		gridProcessInstance = new Grid<>(ProcessInstance.class, false);
		gridProcessInstance.addColumn(p -> p.getId()).setHeader("#");
		gridProcessInstance.addColumn(p -> p.getCode()).setHeader("Code").setAutoWidth(true);
		gridProcessInstance.addColumn(p -> p.getDescription()).setHeader("Description").setAutoWidth(true);
		gridProcessInstance.addColumn(p -> p.getStatus()).setHeader("Status").setWidth("3em");
		gridProcessInstance.addComponentColumn(p -> {
			ProgressBar progress = new ProgressBar();
			progress.setMax(Double.valueOf(p.getSteps().size()));
			var step=p.getSteps().stream().filter(s->s.getStatus().equals(ProcessInstanceStep.STATUS_COMPLETED)).count();
			progress.setValue(Double.valueOf(step));
			return progress;
		}).setHeader("Progress").setWidth("3em");
		gridProcessInstance.addColumn(p -> p.getCurrentStepCode()).setHeader("Latest Step").setAutoWidth(true);
		gridProcessInstance.addColumn(p -> p.getRetryNo()).setHeader("Retried#").setWidth("2em");
		gridProcessInstance.addColumn(p -> dateFormat(p.getCreated())).setHeader("Created").setAutoWidth(true);
		gridProcessInstance.addColumn(p -> dateFormat(p.getStarted())).setHeader("Started").setAutoWidth(true);
		gridProcessInstance.addColumn(p -> dateFormat(p.getFinished())).setHeader("Finished").setAutoWidth(true);
		gridProcessInstance.addComponentColumn(p -> {
			Button btnShowVars = new Button("", new Icon(VaadinIcon.LIST));
			btnShowVars.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SMALL);
			btnShowVars.setEnabled(!p.getStatus().equals(ProcessInstance.END));
			btnShowVars.setDisableOnClick(true);
			btnShowVars.addClickListener(e -> {
				gridProcessInstance.select(p);
				showVariables("instance variable for %s".formatted(p.getCode()),p.getInstanceVariables());
				btnShowVars.setEnabled(true);
			});
			return btnShowVars;
		}).setHeader("Vars").setWidth("2em");
		gridProcessInstance.addComponentColumn(p -> {
			Button btnShowSteps = new Button("", new Icon(VaadinIcon.OPEN_BOOK));
			btnShowSteps.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SMALL);
			btnShowSteps.setDisableOnClick(true);
			btnShowSteps.addClickListener(e -> {
				gridProcessInstance.select(p);
				showProcessInstanceSteps("instance variable for %s".formatted(p.getCode()),p.getSteps());
				btnShowSteps.setEnabled(true);
			});
			return btnShowSteps;
		}).setHeader("Steps").setWidth("2em");;
		
		
		gridProcessInstance.addComponentColumn(p -> {
			Button btnApprove = new Button("Release", new Icon(VaadinIcon.CHECK));
			btnApprove.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
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
		}).setHeader("Release").setAutoWidth(true);
		
		
		gridProcessInstance.addComponentColumn(p -> {
			Button btnRun = new Button("Run", new Icon(VaadinIcon.PLAY));
			btnRun.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
			btnRun.setDisableOnClick(true);
			btnRun.addClickListener(e -> {
				gridProcessInstance.select(p);
				runProcessInstance(p);
				btnRun.setEnabled(true);
			});
			btnRun.setEnabled(!p.getStatus().equals(ProcessInstance.END));
			return btnRun;
		}).setHeader("Run").setAutoWidth(true);
		

		
		gridProcess.setWidthFull();
		gridProcessSteps.setWidthFull();
		gridProcessInstance.setWidthFull();
		
		gridProcess.getColumns().forEach(col->{col.setResizable(true);});
		gridProcessSteps.getColumns().forEach(col->{col.setResizable(true);});
		gridProcessInstance.getColumns().forEach(col->{col.setResizable(true);});
		
		gridProcess.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);
		gridProcessSteps.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);
		gridProcessInstance.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);

		
		
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

	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");

	private String dateFormat(LocalDateTime local) {
		if (local==null) return null;
		return local.format(formatter);
	}



	private void startUpdaterThread() {
		Thread thread=new Thread(new ThreadForUIUpdating(this));
		thread.start();
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
		grid.addColumn(p -> p.getCommands()).setHeader("Command Executed");
		grid.addColumn(p -> p.getApprovedBy()).setHeader("Approved By");
		grid.addColumn(p -> p.getApprovalDate()).setHeader("Approval Date");
		grid.addColumn(p -> p.getCreated()).setHeader("Created");
		grid.addColumn(p -> p.getStarted()).setHeader("Started");
		grid.addColumn(p -> p.getFinished()).setHeader("Finished");
		
		grid.getColumns().forEach(col->{col.setResizable(true);});
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);


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
		dialogLayout.setSizeFull();
		
		HashMap<String, String> hmVars = RunnerUtil.String2HashMap(data);
		Grid<KeyValue> gridVars=new Grid<>(KeyValue.class, false);
		gridVars.addColumn(p -> p.getKey()).setHeader("Variable Name").setWidth("20em");
		gridVars.addColumn(p -> p.getValue()).setHeader("Value").setWidth("30em");
		
		gridVars.getColumns().forEach(col->{col.setResizable(true);});
		gridVars.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);
		gridVars.setSizeFull();

		List<KeyValue> items=new ArrayList<KeyValue>();
		hmVars.keySet().stream().forEach(key->{
			items.add(new KeyValue(key,(String) hmVars.get(key)));
		});
		gridVars.setItems(items);
		dialogLayout.add(gridVars);
		
		
		dialog.add(dialogLayout);
		Button cancelButton = new Button("Cancel", e -> dialog.close());
		dialog.getFooter().add(cancelButton);
		dialog.setWidth("60%");
		dialog.setHeight("50%");
		dialog.setResizable(true);
		dialog.setCloseOnEsc(true);
		dialog.setCloseOnOutsideClick(true);
		dialog.open();
		
	}


	public boolean refreshProcessDefinitionGrid() {

		if (!isAttached()) {
			return false;
		}
		if (!isVisible()) {
			return false;
		}
		
		var selection = gridProcess.getSelectedItems();
		if (selection.isEmpty()) {
			return true;
		}
		
		setData(selection.iterator().next());

		return true;
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

		List<ProcessInstance> instances = processService.getProcessInstancesByProcessDefinition(processDefinition);
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
		
		RunnerUtil runner = new RunnerUtil(processService);
		
		RunnerSingleton.getInstance().start(processId);
		List<ProcessInstance> discoveredInstances = runner.runProcessDiscovery(processDefinition);
		int discovered = 0;
		for (ProcessInstance discoveredInstance : discoveredInstances) {
			runner.logger("discovered : new instance [%s] of process [%s]".formatted(processDefinition.getCode(),discoveredInstance.getCode()));
			boolean isExists=processService.isProcessInstanceAlreadyExists(discoveredInstance);
			if (isExists) {
				runner.logger("skip process [%s]/%s".formatted(processDefinition.getCode(),discoveredInstance.getCode(), processDefinition.getCode()));
				continue;
			}
			
			processService.saveProcessInstance(discoveredInstance);
			discovered++;
		}
		
		RunnerSingleton.getInstance().stop(processId);
		notifyInfo(discovered==0 ?  "no new instance is discovered " : "%d new instance discovered".formatted(discovered));
		setData(processDefinition);
		fillGrid();
		
	}
	
	
	private void notifySuccess(String content) {
		Notification notification = Notification.show(content);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_END);
	}
	
	private void notifyError(String content) {
		Notification notification = Notification.show(content);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_END);		
	}
	
	private void notifyInfo(String content) {
		Notification notification = Notification.show(content);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notification.setPosition(Notification.Position.TOP_END);		
	}





	private void runProcessInstance(ProcessInstance processInstance) {
		RunnerUtil runner=new RunnerUtil(processService);
		runner.logger("running task : %s".formatted(processInstance.getCode()));
		ExecutionResultsForInstance result = runner.runProcessInstance(processInstance);
		if (result.getStatus().equals(ExecutionResultsForInstance.STATUS_FAILED)) {
			String msg=result.getMessage();
			notifyError("exception at task : %s => %s".formatted(processInstance.getCode(),msg));
			return;
		}
		else  if (result.getStatus().equals(ExecutionResultsForInstance.STATUS_NOT_ELIGIBLE)) {
			notifyError("the instance %s is not eligible for running at the moment, possibbly due to the limitations".formatted(processInstance.getCode()));
		} else {
			processService.saveProcessInstance(result.getProcessInstance());
		}
		fillGrid();
		setData(processInstance.getProcessDefinition());
		
	}



	public long getRefreshInterval() {
		return 1000;
	}

}
