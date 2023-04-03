package com.robodo.ui;

import java.io.ByteArrayInputStream;
import java.io.File;
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

import com.robodo.model.KeyValue;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessDefinitionStep;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.singleton.ThreadGroupSingleton;
import com.robodo.threads.ThreadForInstanceRunner;
import com.robodo.threads.ThreadForUIUpdating;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;
import com.robodo.utils.UIUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route("/process")
@SpringComponent
@UIScope
public class UIProcessor extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	


	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

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
					UIUtils.notifyError("Error saving");
				} else {
					UIUtils.notifySuccess("process is %s".formatted(newVal ? "active" : "pasive"));
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
		gridProcessInstance.addColumn(p -> p.getError()).setHeader("Error").setWidth("10em");
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
				//gridProcessInstance.select(p);
				showVariables("instance variable for %s (%s)".formatted(p.getCode(), p.getDescription()),p.getInstanceVariables());
				btnShowVars.setEnabled(true);
			});
			return btnShowVars;
		}).setHeader("Vars").setWidth("2em");
		gridProcessInstance.addComponentColumn(p -> {
			Button btnShowSteps = new Button("", new Icon(VaadinIcon.OPEN_BOOK));
			btnShowSteps.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SMALL);
			btnShowSteps.setDisableOnClick(true);
			btnShowSteps.addClickListener(e -> {
				//gridProcessInstance.select(p);
				showProcessInstanceSteps("steps for %s (%s)".formatted(p.getCode(), p.getDescription()),p.getSteps());
				btnShowSteps.setEnabled(true);
			});
			return btnShowSteps;
		}).setHeader("Steps").setWidth("2em");;
		
		
		gridProcessInstance.addComponentColumn(p -> {
			Button btnApprove = new Button("Approval", new Icon(VaadinIcon.CHECK));
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
				//gridProcessInstance.select(p);
				approveProcessInstance(p);
				btnApprove.setEnabled(true);
			});
			return btnApprove;
		}).setHeader("Approval").setAutoWidth(true);
		
		
		gridProcessInstance.addComponentColumn(p -> {
			Button btnRun = new Button("Run", new Icon(VaadinIcon.PLAY));
			btnRun.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
			btnRun.setDisableOnClick(true);
			btnRun.addClickListener(e -> {
				gridProcessInstance.select(p);
				runProcessInstance(p);
				try {Thread.sleep(1000);} catch(Exception ex) {}
				btnRun.setEnabled(true);
			});
			btnRun.setEnabled(!p.getStatus().equals(ProcessInstance.END));
			return btnRun;
		}).setHeader("Run").setAutoWidth(true);
		

		
		gridProcess.setWidthFull();
		gridProcessSteps.setWidthFull();
		gridProcessInstance.setWidthFull();
		
		gridProcess.setMaxHeight(200, Unit.EM);
		gridProcessSteps.setMaxHeight(200, Unit.EM);
		
		gridProcess.getColumns().forEach(col->{col.setResizable(true);});
		gridProcessSteps.getColumns().forEach(col->{col.setResizable(true);});
		gridProcessInstance.getColumns().forEach(col->{col.setResizable(true);});
		
		gridProcess.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);
		gridProcessSteps.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);
		gridProcessInstance.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);

		
		
		HorizontalLayout horizontalLay = new HorizontalLayout(gridProcess,gridProcessSteps);
		horizontalLay.setFlexGrow(5, gridProcess);
		horizontalLay.setFlexGrow(2, gridProcessSteps);
		horizontalLay.setWidthFull();
		


		VerticalLayout verticalLay = new VerticalLayout();
		verticalLay.setWidthFull();
		verticalLay.setHeightFull();
		verticalLay.add(new H3("Processes"));
		verticalLay.add(horizontalLay);
		verticalLay.add(new H4("Instances"));
		verticalLay.add(gridProcessInstance);
		add(verticalLay);
		setSizeFull();
		
		getElement().getStyle().set("height", "100%");

		fillGrid();

	}

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
		
		UI.getCurrent().navigate("/approve/%s/VIEW".formatted(instance.getCode()));
		
		
	}



	private void showProcessInstanceSteps(String title, List<ProcessInstanceStep> steps) {
		Dialog dialog = new Dialog();
		dialog.setHeaderTitle(title);
		
		

		Grid<ProcessInstanceStep> grid=new Grid<>(ProcessInstanceStep.class, false);
		grid.addColumn(p -> p.getId()).setHeader("#").setWidth("2em");
		grid.addColumn(p -> p.getStepCode()).setHeader("Code").setAutoWidth(true);
		grid.addColumn(p -> p.getOrderNo()).setHeader("Order").setWidth("3em");
		grid.addColumn(p -> p.getStatus()).setHeader("Status").setWidth("5em");
		grid.addColumn(p -> p.getError()).setHeader("Error").setWidth("10em");
		grid.addColumn(p -> p.getCommands()).setHeader("Command Executed").setAutoWidth(true);
		grid.addColumn(p -> p.getApprovedBy()).setHeader("Approved By").setAutoWidth(true);
		grid.addColumn(p -> dateFormat(p.getApprovalDate())).setHeader("Approval Date");
		grid.addColumn(p -> dateFormat(p.getCreated())).setHeader("Created").setAutoWidth(true);
		grid.addColumn(p -> dateFormat(p.getStarted())).setHeader("Started").setAutoWidth(true);
		grid.addColumn(p -> dateFormat(p.getFinished())).setHeader("Finished").setAutoWidth(true);
		grid.addComponentColumn(p->{
			Button btnBackward = new Button("Backwards", new Icon(VaadinIcon.BACKWARDS));
			btnBackward.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
			btnBackward.setDisableOnClick(true);
			btnBackward.addClickListener(e -> {
				setBackward(p);
				btnBackward.setEnabled(true);
			});
			btnBackward.setEnabled(!p.getStatus().equals(ProcessInstanceStep.STATUS_NEW));
			return btnBackward; 
		}).setHeader("Backwards").setAutoWidth(true);
		
		grid.getColumns().forEach(col->{col.setResizable(true);});
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);


		grid.setItems(steps);
		
		TextArea logMemo=new TextArea();
		logMemo.setReadOnly(true);
		logMemo.setSizeFull();


		
		Tab tabLogs = new Tab(VaadinIcon.NOTEBOOK.create(), new Span("Logs"));
		Tab tabFiles = new Tab(VaadinIcon.PICTURE.create(), new Span("Files"));
		

		Tabs tabs = new Tabs(tabLogs, tabFiles);
		tabs.setSelectedTab(tabLogs);
		tabs.setWidth("100%");
		
		HorizontalLayout tabContent=new HorizontalLayout(logMemo);
		tabContent.setSizeFull();
		
		tabs.addSelectedChangeListener(event -> {
			tabContent.removeAll();
			if (grid.getSelectedItems().isEmpty()) {
				return;
			}
			if (event.getSelectedTab().equals(tabLogs)) {
				tabContent.add(logMemo);
			} else if (event.getSelectedTab().equals(tabFiles)) {
				ProcessInstanceStep step = grid.getSelectedItems().iterator().next();
				tabContent.add(generateTabForFiles(step));
			}
        });
		
		VerticalLayout lay=new VerticalLayout();
		
		lay.add(grid);
		lay.add(tabs);
		lay.add(tabContent);

		
		
		
		grid.addSelectionListener(p -> {
			Optional<ProcessInstanceStep> selection = p.getFirstSelectedItem();
			if (selection.isEmpty()) {
				logMemo.setValue("");
			} else {
				var logs=selection.get().getLogs();
				logMemo.setValue(logs==null ? "" : logs);
			}
		});
		
		
		dialog.add(lay);
		
		Button cancelButton = new Button("Close", e -> dialog.close());
		dialog.getFooter().add(cancelButton);
		dialog.setWidth("80%");
		dialog.setHeight("80%");
		dialog.setResizable(true);
		dialog.setCloseOnEsc(true);
		dialog.setCloseOnOutsideClick(true);
		dialog.open();
		
	}


	private void setBackward(ProcessInstanceStep stepToBackward) {
		
		if (stepToBackward.getStatus().equals(ProcessInstanceStep.STATUS_NEW)) {
			return;
		}
		ProcessInstance processInstance = stepToBackward.getProcessInstance();
		List<ProcessInstanceStep> steps = processInstance.getSteps();
		
		Collections.sort(steps, new Comparator<ProcessInstanceStep>() {
			@Override
			public int compare(ProcessInstanceStep o1, ProcessInstanceStep o2) {
				return o1.getOrderNo().compareTo(o2.getOrderNo());
			}
		});
		
		int stepCount=0;
		for (int i=steps.size()-1;i>=0;i--) {
			ProcessInstanceStep step=steps.get(i);
			if (step.getStepCode().equals(stepToBackward.getStepCode())) break;
			
			if (!step.getStatus().equals(ProcessInstanceStep.STATUS_NEW)) {
				stepCount++;
			}
		}
		
		if (stepCount>0) {
			UIUtils.notifyError("backward next steps first");
			return;
		}
		
		stepToBackward.setStatus(ProcessInstanceStep.STATUS_NEW);
		stepToBackward.setApprovalDate(null);
		stepToBackward.setApproved(false);
		stepToBackward.setApprovedBy(null);
		stepToBackward.setNotificationSent(false);
		stepToBackward.setError(null);
		stepToBackward.setFiles(new ArrayList<ProcessInstanceStepFile>());
		stepToBackward.setLogs(null);
		
		long countOfNonNew=steps.stream().filter(p->!p.getStatus().equals(ProcessInstanceStep.STATUS_NEW)).count();
		
		processInstance.setStatus(countOfNonNew==0 ? ProcessInstance.STATUS_NEW : ProcessInstance.STATUS_RUNNING);
		processInstance.setFinished(null);
		processInstance.setCurrentStepCode(countOfNonNew==0 ? ProcessInstance.BEGIN : "BACKWARDED");
		processInstance.setRetryNo(Integer.max(processInstance.getRetryNo()-1, 0));

		processService.saveProcessInstance(processInstance);
		
		UIUtils.notifySuccess("backwarded");
	}

	private HorizontalLayout generateTabForFiles(ProcessInstanceStep step) {
		HorizontalLayout lay=new HorizontalLayout();
		
		VerticalLayout content=new VerticalLayout();
		content.setSizeFull();
		
		Tabs tabs=new Tabs();
		tabs.setOrientation(Tabs.Orientation.VERTICAL);
		tabs.setHeightFull();
		step.getFiles().forEach(file -> {
			Tab tab = new Tab(VaadinIcon.FILE_PICTURE.create(), new Span(file.getDescription()));
			tabs.add(tab);
			tabs.addSelectedChangeListener(event -> {
				content.removeAll();
				content.add(getImage(step, file));
			});
		});	
		
		if (step.getFiles().size()>0) {
			tabs.setSelectedIndex(0);
			content.add(getImage(step, step.getFiles().get(0)));
		}
		
		lay.setSizeFull();
		lay.add(tabs, content);
		
		return lay;
	}




	private Image getImage(ProcessInstanceStep step, ProcessInstanceStepFile file) {
		String imageFileName=file.getFileName();
		RunnerUtil runnerUtil = new RunnerUtil(processService);
		String targetDir=runnerUtil.getTargetPath(step.getProcessInstance());
		
		String imagePath=targetDir+File.separator+imageFileName;
		System.err.println(imagePath);
		byte[] imageBytes=HelperUtil.getFileAsByteArray(imagePath);
		runnerUtil.logger("image file [%s] loaded, (%s) bytes".formatted(imagePath,String.valueOf(imageBytes.length)));
		StreamResource resource = new StreamResource(imageFileName, () -> new ByteArrayInputStream(imageBytes));
		Image image = new Image(resource, file.getDescription());

		add(image);
		image.setSizeFull();
		return image;
	}

	private void showVariables(String title, String data) {
		Dialog dialog = new Dialog();
		dialog.setHeaderTitle(title);
		
		VerticalLayout dialogLayout = new VerticalLayout();
		dialogLayout.setSizeFull();
		
		HashMap<String, String> hmVars = HelperUtil.String2HashMap(data);
		Grid<KeyValue> gridVars=new Grid<>(KeyValue.class, false);
		gridVars.addColumn(p -> p.getKey()).setHeader("Variable Name").setWidth("20em");
		gridVars.addComponentColumn(p -> {
			TextField textField=new TextField();
			textField.setWidthFull();
			textField.setValue(p.getValue());
			return textField;
		}).setHeader("Value").setWidth("30em");
		
		gridVars.getColumns().forEach(col->{col.setResizable(true);});
		gridVars.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);
		gridVars.setSizeFull();

		List<KeyValue> items=new ArrayList<KeyValue>();
		hmVars.keySet().stream().forEach(key->{
			items.add(new KeyValue(key,(String) hmVars.get(key)));
		});
		
		Collections.sort(items, new Comparator<KeyValue>() {

			@Override
			public int compare(KeyValue o1, KeyValue o2) {
				return o1.getKey().compareTo(o2.getKey());
			}

		});
		
		gridVars.setItems(items);
		dialogLayout.add(gridVars);
		
		
		dialog.add(dialogLayout);
		Button cancelButton = new Button("Close", e -> dialog.close());
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
			UIUtils.notifyError("Discovery is already running");
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
		UIUtils.notifyInfo(discovered==0 ?  "no new instance is discovered " : "%d new instance discovered".formatted(discovered));
		setData(processDefinition);
		fillGrid();
		
	}
	
	
	





	private void runProcessInstance(ProcessInstance processInstance) {
		
		int maxThreadCount=Integer.valueOf(processService.getEnv().getProperty("max.thread"));
		int currentThreadCount=ThreadGroupSingleton.getInstance().getActiveThreadCount();
		if (currentThreadCount>=maxThreadCount) {
			UIUtils.notifyError("no thread to run this instance");
			return;
		}
		
		if (RunnerSingleton.getInstance().hasRunningInstance(processInstance.getCode())) {
			UIUtils.notifyError("this instance %s is already running. Please wait.".formatted(processInstance.getCode()));
			return;
		}
		
		
		Thread thread=new Thread(new ThreadForInstanceRunner(processService, processInstance));
		thread.start();
		
		UIUtils.notifySuccess("tread succssfully started for instance %s, thread id : %s ".formatted(processInstance.getCode(), String.valueOf(thread.getId())));
		
	}



	public long getRefreshInterval() {
		return 1000;
	}

}
