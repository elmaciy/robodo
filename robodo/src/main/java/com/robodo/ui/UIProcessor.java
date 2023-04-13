package com.robodo.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.KeyValue;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessDefinitionStep;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.model.UserRole;
import com.robodo.security.SecurityService;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.singleton.ThreadGroupSingleton;
import com.robodo.threads.ThreadForInstanceRunner;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
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

import jakarta.annotation.security.RolesAllowed;


@Route("/process")
@RolesAllowed(UserRole.ROLE_USER)
public class UIProcessor extends UIBase {

	private static final long serialVersionUID = 1L;
	
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	ProcessService processService;

	Grid<ProcessDefinition> gridProcessDefinition;
	Grid<ProcessInstance> gridProcessInstance;
	


	@Autowired
	public UIProcessor(ProcessService processService, SecurityService securityService) {
		super(processService, securityService);
		this.processService = processService;
		
		setTitle("Processes");
		
		
		gridProcessDefinition = new Grid<>(ProcessDefinition.class, false);
		gridProcessDefinition.addColumn(p -> p.getId()).setHeader("#").setWidth("3em");
		gridProcessDefinition.addColumn(p -> p.getCode()).setHeader("Code").setAutoWidth(true);
		gridProcessDefinition.addColumn(p -> p.getDescription()).setHeader("Description").setAutoWidth(true);
		gridProcessDefinition.addComponentColumn(p -> {
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
		gridProcessDefinition.addComponentColumn(p->{
			var fld= makeIntegerMinMaxField(p.getMaxAttemptCount(),1,100);
			fld.addValueChangeListener(e->{
				Integer value = e.getValue();
				p.setMaxAttemptCount(value);
				processService.saveProcessDefinition(p);
				notifyInfo("maximum attempt count changed");
			});
			return fld;
		}).setHeader("Attempt").setWidth("2em");
		gridProcessDefinition.addComponentColumn(p->{
			var fld= makeIntegerMinMaxField(p.getMaxThreadCount(),1,10);
			fld.addValueChangeListener(e->{
				Integer value = e.getValue();
				p.setMaxThreadCount(value);
				processService.saveProcessDefinition(p);
				notifyInfo("maximum thread count changed");
			});
			return fld;
		}).setHeader("Thread").setWidth("2em");
		gridProcessDefinition.addColumn(p -> p.getDiscovererClass()).setHeader("Discoverer");

		gridProcessDefinition.addComponentColumn(p -> {
			Button btnRun = new Button("", new Icon(VaadinIcon.SEARCH));
			btnRun.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
			btnRun.setDisableOnClick(true);
			btnRun.addClickListener(e -> {
				runProcessDiscoverer(p);
				gridProcessDefinition.select(p);
				fillProcessInstanceGrid(p);
				btnRun.setEnabled(true);
			});
			return btnRun;
		}).setHeader("Discover").setWidth("3em");
		gridProcessDefinition.addComponentColumn(p -> {
			Button btnShowSteps = new Button("", new Icon(VaadinIcon.LINES_LIST));
			btnShowSteps.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
			btnShowSteps.setDisableOnClick(true);
			btnShowSteps.addClickListener(e -> {
				showProcessDefinitionSteps(p);
				btnShowSteps.setEnabled(true);
			});
			return btnShowSteps;
		}).setHeader("Steps").setWidth("3em");;
		
		gridProcessDefinition.addSelectionListener(e -> {
			Optional<ProcessDefinition> selection = e.getFirstSelectedItem();
			if (selection.isEmpty()) {
				gridProcessInstance.setItems(Collections.emptyList());
			} else {
				fillProcessInstanceGrid(selection.get());
			}
		});
		
		
		
		
		//--------------------------------------------------------------------
		gridProcessInstance = new Grid<>(ProcessInstance.class, false);
		gridProcessInstance.addColumn(p -> p.getId()).setHeader(makeInstaceGridRefresherButton()).setAutoWidth(true).setFrozen(true).setTextAlign(ColumnTextAlign.END);
		gridProcessInstance.addColumn(p -> p.getCode()).setHeader("Code").setAutoWidth(true).setFrozen(true);
		gridProcessInstance.addColumn(p -> p.getDescription()).setHeader("Description").setAutoWidth(true).setFrozen(true);
		gridProcessInstance.addColumn(p -> p.getStatus()).setHeader("Status").setWidth("3em").setFrozen(true);
		gridProcessInstance.addColumn(p -> p.getError()).setHeader("Error").setWidth("10em");
		gridProcessInstance.addComponentColumn(p -> {
			ProgressBar progress = new ProgressBar();
			progress.setMax(Double.valueOf(p.getSteps().size()));
			var step=p.getSteps().stream()
					.filter(
							s->!s.getStatus().equals(ProcessInstanceStep.STATUS_NEW) 
							&& !s.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING)
							)
					.count();
			progress.setValue(Double.valueOf(step));
			return progress;
		}).setHeader("Progress").setWidth("3em");
		gridProcessInstance.addColumn(p -> p.getLatestProcessedStep()==null ? ProcessInstanceStep.STEP_NONE : p.getLatestProcessedStep().getStepCode()).setHeader("Latest Step").setAutoWidth(true);
		gridProcessInstance.addColumn(p -> p.getAttemptNo()).setHeader("Attempt#").setWidth("2em").setTextAlign(ColumnTextAlign.END);
		gridProcessInstance.addColumn(p -> dateFormat(p.getCreated())).setHeader("Created").setWidth("3em").setTextAlign(ColumnTextAlign.END);
		gridProcessInstance.addColumn(p -> dateFormat(p.getStarted())).setHeader("Started").setWidth("3em").setTextAlign(ColumnTextAlign.END);
		gridProcessInstance.addColumn(p -> dateFormat(p.getFinished())).setHeader("Finished").setWidth("3em").setTextAlign(ColumnTextAlign.END);
		gridProcessInstance.addComponentColumn(p -> {
			Button btnShowVars = new Button("", new Icon(VaadinIcon.LIST));
			btnShowVars.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SMALL);
			btnShowVars.setDisableOnClick(true);
			btnShowVars.addClickListener(e -> {
				showVariables(p);
				btnShowVars.setEnabled(true);
			});
			return btnShowVars;
		}).setHeader("Vars").setWidth("2em").setFrozenToEnd(true).setTextAlign(ColumnTextAlign.CENTER);
		gridProcessInstance.addComponentColumn(p -> {
			Button btnShowSteps = new Button("", new Icon(VaadinIcon.OPEN_BOOK));
			btnShowSteps.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SMALL);
			btnShowSteps.setDisableOnClick(true);
			btnShowSteps.addClickListener(e -> {
				showProcessInstanceSteps("steps for %s (%s)".formatted(p.getCode(), p.getDescription()),p.getSteps());
				btnShowSteps.setEnabled(true);
			});
			return btnShowSteps;
		}).setHeader("Steps").setWidth("2em").setFrozenToEnd(true).setTextAlign(ColumnTextAlign.CENTER);
		
		
		gridProcessInstance.addComponentColumn(p -> {
			Button btnApprove = new Button("", new Icon(VaadinIcon.CHECK));
			btnApprove.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
			if (p.getStatus().equals(ProcessInstance.STATUS_RUNNING)) {
				Optional<ProcessInstanceStep> instanceStepOpt =  p.getSteps().stream().filter(i->i.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING)).findFirst();
				if (instanceStepOpt.isEmpty()) {
					btnApprove.setEnabled(false);
				} else {
					ProcessInstanceStep instanceStep = instanceStepOpt.get();
					boolean enabled = instanceStepOpt.get().isHumanInteractionStep() && !instanceStep.isApproved();
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
		}).setHeader("Approve").setAutoWidth(true).setFrozenToEnd(true).setTextAlign(ColumnTextAlign.CENTER);
		
		
		gridProcessInstance.addComponentColumn(p -> {
			Button btnRun = new Button("", new Icon(VaadinIcon.PLAY));
			btnRun.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
			btnRun.setDisableOnClick(true);
			btnRun.addClickListener(e -> {
				gridProcessInstance.select(p);
				runProcessInstance(p);
				try {Thread.sleep(1000);} catch(Exception ex) {}
				btnRun.setEnabled(true);
			});
			btnRun.setEnabled(!p.getStatus().equals(ProcessInstance.STATUS_COMPLETED));
			return btnRun;
		}).setHeader("Run").setAutoWidth(true).setFrozenToEnd(true).setTextAlign(ColumnTextAlign.CENTER);

		gridProcessDefinition.setWidthFull();
		gridProcessInstance.setWidthFull();
		
		
		gridProcessDefinition.getColumns().forEach(col->{col.setResizable(true);});
		gridProcessInstance.getColumns().forEach(col->{col.setResizable(true);});
		
		gridProcessDefinition.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);
		gridProcessInstance.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);

		
		add(gridProcessDefinition);
		add(headerOfInstancesLayout());
		add(gridProcessInstance);

		

		fillGrid();
	}



	private Component makeInstaceGridRefresherButton() {
		Button btnRefresh = new Button("", new Icon(VaadinIcon.REFRESH));
		btnRefresh.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
		btnRefresh.setDisableOnClick(true);
		btnRefresh.addClickListener(e -> {
			Set<ProcessDefinition> selectedProcessDefiniton = gridProcessDefinition.getSelectedItems();
			if (selectedProcessDefiniton.isEmpty()) {
				gridProcessInstance.setItems(Collections.emptyList());
			} else {
				fillProcessInstanceGrid(selectedProcessDefiniton.iterator().next());
			}
			btnRefresh.setEnabled(true);
		});
		
		return btnRefresh;
	}

	boolean instanceFilterNew=true;
	boolean instanceFilterCompleted=false;
	boolean instanceFilterRunning=true;
	boolean instanceFilterRetry=true;
	boolean instanceFilterFailed=false;
	boolean instanceFilterWaitingApproval=false;


	private HorizontalLayout headerOfInstancesLayout() {
		HorizontalLayout layoutForProcessInstanceTop=new HorizontalLayout();

		layoutForProcessInstanceTop.setSizeUndefined();
		Label title = new Label("Instances");
		layoutForProcessInstanceTop.add(title);
		
		Checkbox checkboxNew = new Checkbox();
		checkboxNew.setValue(true);
		checkboxNew.setLabel(ProcessInstance.STATUS_NEW);
		checkboxNew.setValue(instanceFilterNew);
		checkboxNew.addValueChangeListener(e->{
			instanceFilterNew=e.getValue();
			fillProcessInstanceGrid();
		});
		layoutForProcessInstanceTop.add(checkboxNew);

		
		
		Checkbox checkboxCompleted= new Checkbox();
		checkboxCompleted.setValue(true);
		checkboxCompleted.setLabel(ProcessInstance.STATUS_COMPLETED);
		checkboxCompleted.setValue(instanceFilterCompleted);
		checkboxCompleted.addValueChangeListener(e->{
			instanceFilterCompleted=e.getValue();
			fillProcessInstanceGrid();
		});	
		layoutForProcessInstanceTop.add(checkboxCompleted);

		
		Checkbox checkboxRunning = new Checkbox();
		checkboxRunning.setValue(true);
		checkboxRunning.setLabel(ProcessInstance.STATUS_RUNNING);
		checkboxCompleted.setValue(instanceFilterRunning);
		checkboxRunning.addValueChangeListener(e->{
			instanceFilterRunning=e.getValue();
			fillProcessInstanceGrid();
		});	
		layoutForProcessInstanceTop.add(checkboxRunning);

				
		Checkbox checkboxRetry = new Checkbox();
		checkboxRetry.setValue(true);
		checkboxCompleted.setValue(instanceFilterRetry);
		checkboxRetry.setLabel(ProcessInstance.STATUS_RETRY);
		checkboxRetry.addValueChangeListener(e->{
			instanceFilterRetry=e.getValue();
			fillProcessInstanceGrid();
		});	
		layoutForProcessInstanceTop.add(checkboxRetry);

				
		
		Checkbox chError = new Checkbox();
		chError.setLabel("Failed");
		checkboxCompleted.setValue(instanceFilterFailed);
		chError.addValueChangeListener(e->{
			instanceFilterFailed=e.getValue();
			fillProcessInstanceGrid();
		});
		
		layoutForProcessInstanceTop.add(chError);


		Checkbox chWaitingApproval = new Checkbox();
		chWaitingApproval.setValue(instanceFilterWaitingApproval);
		chWaitingApproval.setLabel("Waiting approvval");
		chWaitingApproval.addValueChangeListener(e->{
			instanceFilterWaitingApproval=e.getValue();
			fillProcessInstanceGrid();
		});
		
		layoutForProcessInstanceTop.add(chWaitingApproval);
	
		
		return layoutForProcessInstanceTop;
	}



	
	private void fillProcessInstanceGrid() {
		Set<ProcessDefinition> selectedItems = gridProcessDefinition.getSelectedItems();
		if (selectedItems.isEmpty()) {
			gridProcessInstance.setItems(Collections.emptyList());
			return;
		}
		
		fillProcessInstanceGrid(selectedItems.iterator().next());
		
	}
	

	private void fillProcessInstanceGrid(ProcessDefinition processDefinition) {
		List<ProcessInstance> filteredInstances = new ArrayList<ProcessInstance>();
		
		if (instanceFilterNew) {
			List<ProcessInstance> instances = processService.getProcessInstancesByProcessDefinitionAndStatus(processDefinition, ProcessInstance.STATUS_NEW).stream().toList();
			filteredInstances.addAll(instances);
		}
		if (instanceFilterCompleted) {
			List<ProcessInstance> instances = processService.getProcessInstancesByProcessDefinitionAndStatus(processDefinition, ProcessInstance.STATUS_COMPLETED).stream().toList();
			filteredInstances.addAll(instances);
		}
		if (instanceFilterRunning) {
			List<ProcessInstance> instances = processService.getProcessInstancesByProcessDefinitionAndStatus(processDefinition, ProcessInstance.STATUS_RUNNING).stream().toList();
			filteredInstances.addAll(instances);
		}
		if (instanceFilterRetry) {
			List<ProcessInstance> instances = processService.getProcessInstancesByProcessDefinitionAndStatus(processDefinition, ProcessInstance.STATUS_RETRY).stream().toList();
			filteredInstances.addAll(instances);
		}
		
		if (instanceFilterFailed) {
			filteredInstances.removeIf((p)->!p.isFailed());
		}
		
		
		if (instanceFilterWaitingApproval) {
			filteredInstances.removeIf((p)->!p.isWaitingApproval());
		}

	
		gridProcessInstance.setItems(filteredInstances);
		
	}


	private String dateFormat(LocalDateTime local) {
		if (local==null) return null;
		return local.format(formatter);
	}


	private void approveProcessInstance(ProcessInstance instance) {
		Optional<ProcessInstanceStep> stepOpt = instance.getSteps().stream()
				.filter(p->p.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING))
				.findFirst();
		if (stepOpt.isEmpty()) {
			return;
		}

		UI.getCurrent().navigate("/approve/%s/VIEW/SCREEN/%s".formatted(HelperUtil.encrypt(instance.getCode()), "notoken"));
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
		grid.addComponentColumn(p->{
			Button btnIcon = new Button();
			btnIcon.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
			
			if (!p.isHumanInteractionStep()) {
				return btnIcon;
			}
			
			if (p.getStatus().equals(ProcessInstanceStep.STATUS_COMPLETED)) {
				if (p.isApproved()) {
					btnIcon.setIcon(VaadinIcon.CHECK.create());
					btnIcon.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
					
				} else {
					btnIcon.setIcon(VaadinIcon.CLOSE.create());
					btnIcon.addThemeVariants(ButtonVariant.LUMO_ERROR);
					
				}
			} else if (p.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING)) {
				btnIcon.setIcon(VaadinIcon.CLOCK.create());
				btnIcon.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
				
			}
			
			return btnIcon;
		}).setHeader("Approved").setAutoWidth(true);
		grid.addColumn(p -> p.getApprovedBy()).setHeader("App. By").setAutoWidth(true);
		grid.addColumn(p -> dateFormat(p.getApprovalDate())).setHeader("App. Date");
		grid.addColumn(p -> dateFormat(p.getCreated())).setHeader("Created").setAutoWidth(true);
		grid.addColumn(p -> dateFormat(p.getStarted())).setHeader("Started").setAutoWidth(true);
		grid.addColumn(p -> dateFormat(p.getFinished())).setHeader("Finished").setAutoWidth(true);
		grid.addComponentColumn(p->{
			Button btnBackward = new Button("", new Icon(VaadinIcon.BACKWARDS));
			btnBackward.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
			btnBackward.setDisableOnClick(true);
			btnBackward.addClickListener(e -> {
				setBackward(p);
				btnBackward.setEnabled(true);
			});
			btnBackward.setEnabled(!p.getStatus().equals(ProcessInstanceStep.STATUS_NEW));
			return btnBackward; 
		}).setHeader("Rollback").setAutoWidth(true);
		
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
			tabContent.removeAll();
			
			Optional<ProcessInstanceStep> selection = p.getFirstSelectedItem();
			if (selection.isEmpty()) {
				if (tabs.getSelectedTab().equals(tabLogs)) {
					logMemo.setValue("");
					tabContent.add(logMemo);
				} else {
					tabContent.add(generateTabForFiles(null));
				}
				
			} else {
				
				if (tabs.getSelectedTab().equals(tabLogs)) {
					var logs=selection.get().getLogs();
					logMemo.setValue(logs==null ? "" : logs);
					tabContent.add(logMemo);
				} else {
					tabContent.add(generateTabForFiles(selection.get()));
				}
				
				
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

		int stepCount=0;
		for (int i=steps.size()-1;i>=0;i--) {
			ProcessInstanceStep step=steps.get(i);
			if (step.getStepCode().equals(stepToBackward.getStepCode())) break;
			
			if (!step.getStatus().equals(ProcessInstanceStep.STATUS_NEW)) {
				stepCount++;
			}
		}
		
		if (stepCount>0) {
			notifyError("backward next steps first");
			return;
		}
		
		stepToBackward.setStatus(ProcessInstanceStep.STATUS_NEW);
		stepToBackward.setApprovalDate(null);
		stepToBackward.setApproved(false);
		stepToBackward.setApprovedBy(null);
		stepToBackward.setNotificationSent(false);
		stepToBackward.setError(null);
		stepToBackward.setLogs(null);
		
		long countOfNonNew=steps.stream().filter(p->!p.getStatus().equals(ProcessInstanceStep.STATUS_NEW)).count();
		
		processInstance.setStatus(countOfNonNew==0 ? (processInstance.getAttemptNo()==0 ? ProcessInstance.STATUS_NEW : ProcessInstance.STATUS_RETRY) : ProcessInstance.STATUS_RUNNING);
		processInstance.setFinished(null);
		processInstance.setAttemptNo(Integer.max(processInstance.getAttemptNo()-1, 0));

		processService.saveProcessInstance(processInstance);
		
		notifySuccess("backwarded");
	}

	private VerticalLayout generateTabForFiles(ProcessInstanceStep step) {
		VerticalLayout lay=new VerticalLayout();
		if (step==null) return lay;
		
		List<ProcessInstanceStepFile> files= processService.getProcessInstanceStepFilesByStepId(step);
		
		files.forEach(file -> {
			Span title = new Span(file.getDescription());
			title.setWidthFull();
			title.getElement().getThemeList().add("badge");
			lay.add(title);
			lay.add(getImage(step, file));
		});	
		 
		lay.setSizeFull();
		
		return lay;
	}






	private void showVariables(ProcessInstance processInstance) {
		Dialog dialog = new Dialog();
		String title="instance variable for %s (%s)".formatted(processInstance.getCode(), processInstance.getDescription());
		dialog.setHeaderTitle(title);
		
		VerticalLayout dialogLayout = new VerticalLayout();
		dialogLayout.setSizeFull();
		
		HashMap<String, String> hmVars = HelperUtil.String2HashMap(processInstance.getInstanceVariables());
		Grid<KeyValue> gridVars=new Grid<>(KeyValue.class, false);
		gridVars.addColumn(p -> p.getKey()).setHeader("Variable Name").setWidth("20em");
		gridVars.addComponentColumn(p -> {
			TextField textField=new TextField();
			textField.setWidthFull();
			textField.setValue(p.getValue());
			textField.addValueChangeListener(e->{
				p.setValue(e.getValue());
			});
			return textField;
		}).setHeader("Value").setWidth("30em");
		
		gridVars.addComponentColumn(p -> {
			Button btnUpdate = new Button("Update", new Icon(VaadinIcon.DOWNLOAD));
			btnUpdate.addThemeVariants( ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
			btnUpdate.setDisableOnClick(true);
			btnUpdate.addClickListener(e -> {
				hmVars.put(p.getKey(), p.getValue());
				String changedVariables = HelperUtil.hashMap2String(hmVars);
				processInstance.setInstanceVariables(changedVariables);
				processService.saveProcessInstance(processInstance);
				notifyInfo("variable changed");
				btnUpdate.setEnabled(true);
			});
			return btnUpdate;
		}).setHeader("Update").setAutoWidth(true);
		
		gridVars.addComponentColumn(p -> {
			Button btnRemove = new Button("Remove", new Icon(VaadinIcon.DOWNLOAD));
			btnRemove.addThemeVariants( ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
			btnRemove.setDisableOnClick(true);
			btnRemove.addClickListener(e -> {
				hmVars.remove(p.getKey());
				String changedVariables = HelperUtil.hashMap2String(hmVars);
				processInstance.setInstanceVariables(changedVariables);
				processService.saveProcessInstance(processInstance);
				notifyInfo("variable removed");
				setVariableGridItems(gridVars, hmVars);
				btnRemove.setEnabled(true);
			});
			return btnRemove;
		}).setHeader("Remove").setAutoWidth(true);
		
		gridVars.getColumns().forEach(col->{col.setResizable(true);});
		gridVars.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);
		gridVars.setSizeFull();

		setVariableGridItems(gridVars,hmVars);
		dialogLayout.add(gridVars);
		
		
		dialog.add(dialogLayout);
		Button cancelButton = new Button("Close", e -> dialog.close());
		dialog.getFooter().add(cancelButton);
		dialog.setWidth("60%");
		dialog.setHeight("80%");
		dialog.setResizable(true);
		dialog.setCloseOnEsc(true);
		dialog.setCloseOnOutsideClick(true);
		dialog.open();
		
	}

	private void showProcessDefinitionSteps(ProcessDefinition processDefinition) {
		Dialog dialog = new Dialog();
		String title="steps for %s (%s)".formatted(processDefinition.getCode(), processDefinition.getDescription());
		dialog.setHeaderTitle(title);
		
		VerticalLayout dialogLayout = new VerticalLayout();
		dialogLayout.setSizeFull();

		//--------------------------------------------------------------------
		Grid<ProcessDefinitionStep> gridProcessDefinitionSteps = new Grid<>(ProcessDefinitionStep.class, false);
		gridProcessDefinitionSteps.addColumn(p -> p.getId()).setHeader("#").setWidth("2em");
		gridProcessDefinitionSteps.addColumn(p -> p.getOrderNo()).setHeader("Order").setWidth("2em");
		gridProcessDefinitionSteps.addColumn(p -> p.getCode()).setHeader("Code").setAutoWidth(true);
		gridProcessDefinitionSteps.addColumn(p -> p.getDescription()).setHeader("Description").setAutoWidth(true);
		gridProcessDefinitionSteps.addColumn(p -> p.getCommands()).setHeader("Command to run").setAutoWidth(true);
		gridProcessDefinitionSteps.addColumn(p -> p.isSingleAtATime()).setHeader("Single").setAutoWidth(true);

		gridProcessDefinitionSteps.setWidthFull();
		gridProcessDefinitionSteps.setHeightFull();
		gridProcessDefinitionSteps.setMaxHeight(200, Unit.EM);
		gridProcessDefinitionSteps.getColumns().forEach(col->{col.setResizable(true);});
		gridProcessDefinitionSteps.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);

		gridProcessDefinitionSteps.setItems(processDefinition.getSteps());
		
		dialogLayout.add(gridProcessDefinitionSteps);
	
		dialog.add(dialogLayout);
		Button cancelButton = new Button("Close", e -> dialog.close());
		dialog.getFooter().add(cancelButton);
		dialog.setWidth("80%");
		dialog.setHeight("60%");
		dialog.setResizable(true);
		dialog.setCloseOnEsc(true);
		dialog.setCloseOnOutsideClick(true);
		dialog.open();
		
	}

	

	private void setVariableGridItems(Grid<KeyValue> gridVars, HashMap<String, String> hmVars) {
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
		
	}

	public boolean refreshProcessDefinitionGrid() {

		if (!isAttached()) {
			return false;
		}
		if (!isVisible()) {
			return false;
		}
		
		var selection = gridProcessDefinition.getSelectedItems();
		if (selection.isEmpty()) {
			return true;
		}
		
		setData(selection.iterator().next());

		return true;
	}



	private void fillGrid() {
		List<ProcessDefinition> processDefinitions = processService.getProcessDefinitions();
		gridProcessDefinition.setItems(processDefinitions);

		if (!processDefinitions.isEmpty()) {
			setData(processDefinitions.get(0));
		}

	}

	private void setData(ProcessDefinition processDefinition) {
		gridProcessDefinition.select(processDefinition);
		fillProcessInstanceGrid(processDefinition);
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
		
	}
	
	
	





	private void runProcessInstance(ProcessInstance processInstance) {
		
		int maxThreadCount=Integer.valueOf(processService.getEnv().getProperty("max.thread"));
		int currentThreadCount=ThreadGroupSingleton.getInstance().getActiveThreadCount();
		if (currentThreadCount>=maxThreadCount) {
			notifyError("no thread to run this instance");
			return;
		}
		
		if (RunnerSingleton.getInstance().hasRunningInstance(processInstance.getCode())) {
			notifyError("this instance %s is already running. Please wait.".formatted(processInstance.getCode()));
			return;
		}
		
		
		Thread thread=new Thread(new ThreadForInstanceRunner(processService, processInstance));
		thread.start();
		
		notifySuccess("tread succssfully started for instance %s, thread id : %s ".formatted(processInstance.getCode(), String.valueOf(thread.getId())));
		
	}

}
