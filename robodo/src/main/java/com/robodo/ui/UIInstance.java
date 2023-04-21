package com.robodo.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.KeyValue;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.model.UserRole;
import com.robodo.security.SecurityService;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.threads.ThreadForInstanceRunner;
import com.robodo.utils.HelperUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import jakarta.annotation.security.RolesAllowed;

@Route("/instance")
@RolesAllowed(UserRole.ROLE_USER)
public class UIInstance extends UIBase {

	private static final long serialVersionUID = 1L;

	ProcessService processService;

	ComboBox<ProcessDefinition> comboForProcessDefinition;
	Grid<ProcessInstance> gridProcessInstance;

	@Autowired
	public UIInstance(ProcessService processService, SecurityService securityService) {
		super(processService, securityService);
		this.processService = processService;

		setTitle("Instances", VaadinIcon.FLASH.create());

		
		//--------------------------------------------------------------------------
		Label lblProcessDef = new Label("Process definition");
		
		Button btnRunDiscoverer= new Button("Discover", new Icon(VaadinIcon.SEARCH));
		
		comboForProcessDefinition=new ComboBox<ProcessDefinition>();
		comboForProcessDefinition.setItemLabelGenerator(p->p.getDescription());
		comboForProcessDefinition.setWidth("30em");
		var activeProcessDefinitions = processService.getProcessDefinitions().stream().filter(p->p.isActive()).toList();
		comboForProcessDefinition.setItems(activeProcessDefinitions);
		ProcessDefinition selectedProcessDefinition = (ProcessDefinition) UI.getCurrent().getSession().getAttribute("selectedProcessDefinition");
		if (selectedProcessDefinition!=null) {
			comboForProcessDefinition.setValue(selectedProcessDefinition);
		}
		comboForProcessDefinition.addValueChangeListener(e->{
			ProcessDefinition processDefinition = e.getValue();
			fillProcessInstanceGrid(processDefinition);
			btnRunDiscoverer.setEnabled(e.getValue()!=null);
			UI.getCurrent().getSession().setAttribute("selectedProcessDefinition", e.getValue());
		});

		btnRunDiscoverer.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnRunDiscoverer.setDisableOnClick(true);
		btnRunDiscoverer.setEnabled(comboForProcessDefinition.getValue()!=null);
		btnRunDiscoverer.setVisible(isAdmin());
		
		btnRunDiscoverer.addClickListener(e -> {
			
			runProcessDiscoverer(comboForProcessDefinition.getValue());
			btnRunDiscoverer.setEnabled(true);
			fillProcessInstanceGrid();
		});

		
		HorizontalLayout layForProcessDefinitionFilter=new HorizontalLayout(lblProcessDef, comboForProcessDefinition, btnRunDiscoverer);
		layForProcessDefinitionFilter.setDefaultVerticalComponentAlignment(Alignment.CENTER);

		//--------------------------------------------------------------------------

		gridProcessInstance = new Grid<>(ProcessInstance.class, false);
		gridProcessInstance.addColumn(p -> p.getId()).setHeader("#").setAutoWidth(true)
			.setFrozen(true).setTextAlign(ColumnTextAlign.END).setSortable(true);
		gridProcessInstance.addColumn(p -> p.getCode()).setHeader("Code").setWidth("8em")
			.setFrozen(true).setSortable(true);
		gridProcessInstance.addColumn(p -> p.getDescription()).setHeader("Description").setWidth("8em")
			.setFrozen(true).setSortable(true).setVisible(true);
		gridProcessInstance.addColumn(p -> p.getStatus()).setHeader("Status").setWidth("3em").setFrozen(true).setSortable(true);
		gridProcessInstance.addComponentColumn(p -> makeTrueFalseIcon(!p.isFailed()))
			.setHeader("Res.").setWidth("3em").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
		gridProcessInstance.addColumn(p -> p.getError()).setHeader("Error").setWidth("3em").setSortable(true)
			.setTooltipGenerator(p->HelperUtil.limitString(p.getError(), 1000));
		gridProcessInstance.addComponentColumn(p -> {
			ProgressBar progress = new ProgressBar();
			progress.setMax(Double.valueOf(p.getSteps().size()));
			var step = p.getSteps().stream().filter(s -> !s.getStatus().equals(ProcessInstanceStep.STATUS_NEW)
					&& !s.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING)).count();
			progress.setValue(Double.valueOf(step));
			return progress;
		}).setHeader("Progress").setWidth("3em").setSortable(true);
		gridProcessInstance.addColumn(p -> p.getLatestProcessedStep() == null ? ProcessInstanceStep.STEP_NONE
				: p.getLatestProcessedStep().getStepCode()).setHeader("Latest Step").setAutoWidth(true).setSortable(true);
		gridProcessInstance.addColumn(p -> p.getAttemptNo()).setHeader("Attempt#").setWidth("2em")
			.setTextAlign(ColumnTextAlign.END).setSortable(true);
		gridProcessInstance.addColumn(p -> dateFormat(p.getCreated())).setHeader("Created").setWidth("3em")
			.setTextAlign(ColumnTextAlign.END).setVisible(false);
		gridProcessInstance.addColumn(p -> dateFormat(p.getStarted())).setHeader("Started").setWidth("3em")
			.setTextAlign(ColumnTextAlign.END).setVisible(false);
		gridProcessInstance.addColumn(p -> dateFormat(p.getFinished())).setHeader("Finished").setWidth("3em")
				.setTextAlign(ColumnTextAlign.END).setVisible(false);
		gridProcessInstance.addComponentColumn(p -> {
			Button btnShowVars = new Button("", new Icon(VaadinIcon.LIST));
			btnShowVars.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
			btnShowVars.setDisableOnClick(true);
			btnShowVars.addClickListener(e -> {
				showVariables(p);
				btnShowVars.setEnabled(true);
			});
			return btnShowVars;
		}).setHeader("Vars").setWidth("2em").setFrozenToEnd(true).setTextAlign(ColumnTextAlign.CENTER);
		gridProcessInstance.addComponentColumn(p -> {
			Button btnShowSteps = new Button("", new Icon(VaadinIcon.OPEN_BOOK));
			btnShowSteps.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
			btnShowSteps.setDisableOnClick(true);
			btnShowSteps.addClickListener(e -> {
				showProcessInstanceSteps("steps for %s (%s)".formatted(p.getCode(), p.getDescription()), p);
				btnShowSteps.setEnabled(true);
			});
			return btnShowSteps;
		}).setHeader("Steps").setWidth("2em").setFrozenToEnd(true).setTextAlign(ColumnTextAlign.CENTER);
		gridProcessInstance.addComponentColumn(p -> {
			Button btnApprove = new Button("", new Icon(VaadinIcon.CHECK));
			btnApprove.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
			if (p.getStatus().equals(ProcessInstance.STATUS_RUNNING)) {
				Optional<ProcessInstanceStep> instanceStepOpt = p.getSteps().stream()
						.filter(i -> i.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING)).findFirst();
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
				btnRun.setEnabled(true);
			});
			btnRun.setEnabled(!p.getStatus().equals(ProcessInstance.STATUS_COMPLETED));
			return btnRun;
		}).setHeader("Run").setAutoWidth(true).setFrozenToEnd(true).setTextAlign(ColumnTextAlign.CENTER);

		
		
		//------------------------------------------------------------
		gridProcessInstance.setSizeFull();
		gridProcessInstance.setColumnReorderingAllowed(true);
		gridProcessInstance.getColumns().forEach(col -> {
			col.setResizable(true);
		});

		gridProcessInstance.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT,
				GridVariant.LUMO_ROW_STRIPES);

		gridProcessInstance.setSelectionMode(SelectionMode.MULTI);
		
		add(layForProcessDefinitionFilter);
		add(makeHeaderForInstances());
		add(gridProcessInstance);

		fillGrid();
	}

	

	boolean instanceFilterNew = true;
	boolean instanceFilterCompleted = false;
	boolean instanceFilterRunning = true;
	boolean instanceFilterRetry = true;
	boolean instanceFilterFailed = false;
	boolean instanceFilterWaitingApproval = false;
	
	TextField searchField;
	Checkbox chAnyMatch;

	private HorizontalLayout makeHeaderForInstances() {
		
		instanceFilterNew=getBooleanFromSession("instanceFilterNew",instanceFilterNew);
		instanceFilterCompleted=getBooleanFromSession("instanceFilterCompleted",instanceFilterCompleted);
		instanceFilterRunning=getBooleanFromSession("instanceFilterRunning",instanceFilterRunning);
		instanceFilterRetry=getBooleanFromSession("instanceFilterRetry",instanceFilterRetry);
		instanceFilterFailed=getBooleanFromSession("instanceFilterFailed",instanceFilterFailed);
		instanceFilterWaitingApproval=getBooleanFromSession("instanceFilterWaitingApproval",instanceFilterWaitingApproval);

		
		
		HorizontalLayout layoutForProcessInstanceTop = new HorizontalLayout();
		layoutForProcessInstanceTop.setSizeUndefined();
		layoutForProcessInstanceTop.setDefaultVerticalComponentAlignment(Alignment.CENTER);

		Button btnRefresh = new Button("", new Icon(VaadinIcon.REFRESH));
		btnRefresh.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
		btnRefresh.setDisableOnClick(true);
		btnRefresh.addClickListener(e -> {
			ProcessDefinition selectedProcessDefiniton = comboForProcessDefinition.getValue();
			if (selectedProcessDefiniton==null) {
				gridProcessInstance.setItems(Collections.emptyList());
			} else {
				fillProcessInstanceGrid(selectedProcessDefiniton);
			}
			btnRefresh.setEnabled(true);
		});
		
		layoutForProcessInstanceTop.add(btnRefresh);

		Button btnRemoveSelected= new Button("", new Icon(VaadinIcon.TRASH));
		btnRemoveSelected.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
		btnRemoveSelected.addClickListener(e -> {
			if (gridProcessInstance.getSelectedItems().size()>0) {
				confirmAndRun("Delete", "Sure to remove all selected instances?", ()->removeSelectedInstances());	
			}
			btnRemoveSelected.setEnabled(true);
		});
		
		layoutForProcessInstanceTop.add(btnRemoveSelected);

		chAnyMatch = new Checkbox();
		chAnyMatch.setValue(getBooleanFromSession("chAnyMatch",false));
		chAnyMatch.setLabel("Any");
		chAnyMatch.addValueChangeListener(e->{
			fillProcessInstanceGrid();
			setSessionBoolean("chAnyMatch", e.getValue());
		});

		
		searchField = new TextField();
		searchField.setValue(getStringValueFromSession("searchValue",""));
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setSuffixComponent(chAnyMatch);
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.setValueChangeTimeout(1000);
		searchField.setWidth("20em");
		searchField.setPlaceholder("Search for");
		
		searchField.addValueChangeListener(e->{
			fillProcessInstanceGrid();
			setSessionString("searchValue", e.getValue());
		});
		

		
		layoutForProcessInstanceTop.add(searchField);
		
		Checkbox checkboxNew = new Checkbox();
		checkboxNew.setLabel(ProcessInstance.STATUS_NEW);
		checkboxNew.setValue(instanceFilterNew);
		checkboxNew.addValueChangeListener(e -> {
			instanceFilterNew = e.getValue();
			fillProcessInstanceGrid();
			setSessionBoolean("instanceFilterNew", e.getValue());
		});
		layoutForProcessInstanceTop.add(checkboxNew);

		Checkbox checkboxCompleted = new Checkbox();
		checkboxCompleted.setLabel(ProcessInstance.STATUS_COMPLETED);
		checkboxCompleted.setValue(instanceFilterCompleted);
		checkboxCompleted.addValueChangeListener(e -> {
			instanceFilterCompleted = e.getValue();
			fillProcessInstanceGrid();
			setSessionBoolean("instanceFilterCompleted", e.getValue());
		});
		layoutForProcessInstanceTop.add(checkboxCompleted);

		Checkbox checkboxRunning = new Checkbox();
		checkboxRunning.setValue(true);
		checkboxRunning.setLabel(ProcessInstance.STATUS_RUNNING);
		checkboxCompleted.setValue(instanceFilterRunning);
		checkboxRunning.addValueChangeListener(e -> {
			instanceFilterRunning = e.getValue();
			fillProcessInstanceGrid();
			setSessionBoolean("instanceFilterRunning", e.getValue());
		});
		layoutForProcessInstanceTop.add(checkboxRunning);

		Checkbox checkboxRetry = new Checkbox();
		checkboxCompleted.setValue(instanceFilterRetry);
		checkboxRetry.setLabel(ProcessInstance.STATUS_RETRY);
		checkboxRetry.addValueChangeListener(e -> {
			instanceFilterRetry = e.getValue();
			fillProcessInstanceGrid();
			setSessionBoolean("instanceFilterRetry", e.getValue());
		});
		layoutForProcessInstanceTop.add(checkboxRetry);

		Checkbox chError = new Checkbox();
		chError.setLabel("Failed");
		checkboxCompleted.setValue(instanceFilterFailed);
		chError.addValueChangeListener(e -> {
			instanceFilterFailed = e.getValue();
			fillProcessInstanceGrid();
			setSessionBoolean("instanceFilterFailed", e.getValue());
		});

		layoutForProcessInstanceTop.add(chError);

		Checkbox chWaitingApproval = new Checkbox();
		chWaitingApproval.setValue(instanceFilterWaitingApproval);
		chWaitingApproval.setLabel("Waiting approvval");
		chWaitingApproval.addValueChangeListener(e -> {
			instanceFilterWaitingApproval = e.getValue();
			fillProcessInstanceGrid();
			setSessionBoolean("instanceFilterWaitingApproval", e.getValue());
		});

		layoutForProcessInstanceTop.add(chWaitingApproval);



		
		return layoutForProcessInstanceTop;
	}

	private void setSessionBoolean(String key, boolean value) {
		UI.getCurrent().getSession().setAttribute(key, value);
	}

	private void setSessionString(String key, String value) {
		UI.getCurrent().getSession().setAttribute(key, value);
	}

	private String getStringValueFromSession(String key, String initialValue) {
		VaadinSession session = UI.getCurrent().getSession();
		if (session.getAttribute(key)==null) {
			return initialValue;
		}
		
		return (String) session.getAttribute(key);	
		}

	private boolean getBooleanFromSession(String key, boolean initialValue) {
		VaadinSession session = UI.getCurrent().getSession();
		if (session.getAttribute(key)==null) {
			return initialValue;
		}
		
		return (Boolean) session.getAttribute(key);
	}

	private void removeSelectedInstances() {
		Iterator<ProcessInstance> iterator = gridProcessInstance.getSelectedItems().iterator();
		while(iterator.hasNext()) {
			ProcessInstance instance = iterator.next();
			processService.deleteProcessInstance(instance);
		}
		
		fillProcessInstanceGrid();
	}

	private void fillProcessInstanceGrid() {
		ProcessDefinition selectedProcessDefinition = comboForProcessDefinition.getValue();
		if (selectedProcessDefinition==null) {
			gridProcessInstance.setItems(Collections.emptyList());
			return;
		}

		fillProcessInstanceGrid(selectedProcessDefinition);

	}

	private void fillProcessInstanceGrid(ProcessDefinition processDefinition) {
		
		Set<ProcessInstance> selectedItems = gridProcessInstance.getSelectedItems();
		
		
		List<ProcessInstance> filteredInstances = new ArrayList<ProcessInstance>();
		
		String searchString=searchField.getValue().strip();
		boolean anyMatch=chAnyMatch.getValue();

		if (instanceFilterNew) {
			List<ProcessInstance> instances = processService
					.getProcessInstancesByProcessDefinitionAndStatusAndSearchString(processDefinition, ProcessInstance.STATUS_NEW, searchString, anyMatch)
					.stream().toList();
			filteredInstances.addAll(instances);
		}
		if (instanceFilterCompleted) {
			List<ProcessInstance> instances = processService
					.getProcessInstancesByProcessDefinitionAndStatusAndSearchString(processDefinition,
							ProcessInstance.STATUS_COMPLETED, searchString, anyMatch)
					.stream().toList();
			filteredInstances.addAll(instances);
		}
		if (instanceFilterRunning) {
			List<ProcessInstance> instances = processService
					.getProcessInstancesByProcessDefinitionAndStatusAndSearchString(processDefinition, ProcessInstance.STATUS_RUNNING, searchString, anyMatch)
					.stream().toList();
			filteredInstances.addAll(instances);
		}
		if (instanceFilterRetry) {
			List<ProcessInstance> instances = processService
					.getProcessInstancesByProcessDefinitionAndStatusAndSearchString(processDefinition, ProcessInstance.STATUS_RETRY, searchString, anyMatch)
					.stream().toList();
			filteredInstances.addAll(instances);
		}

		if (instanceFilterFailed) {
			filteredInstances.removeIf((p) -> !p.isFailed());
		}

		if (instanceFilterWaitingApproval) {
			filteredInstances.removeIf((p) -> !p.isWaitingApproval());
		}

		gridProcessInstance.setItems(filteredInstances);
		
		for (ProcessInstance instance : filteredInstances) {
			if (selectedItems.stream().anyMatch(p->p.getCode().equals(instance.getCode()))) {
				gridProcessInstance.select(instance);
			}
		}

	}


	private void approveProcessInstance(ProcessInstance instance) {
		Optional<ProcessInstanceStep> stepOpt = instance.getSteps().stream()
				.filter(p -> p.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING)).findFirst();
		if (stepOpt.isEmpty()) {
			return;
		}

		UI.getCurrent()
				.navigate("/approve/%s/VIEW/INTERNAL/%s".formatted(HelperUtil.encrypt(instance.getCode()), "notoken"));
	}

	private void showProcessInstanceSteps(String title, ProcessInstance processInstance) {
		Dialog dialog = new Dialog();
		dialog.setHeaderTitle(title);

		Grid<ProcessInstanceStep> grid = new Grid<>(ProcessInstanceStep.class, false);
		
		Button btnRefresh = new Button("", new Icon(VaadinIcon.REFRESH));
		btnRefresh.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
		btnRefresh.addClickListener(e->fillStepsGrid(grid, processInstance.getCode(), null));

		grid.addColumn(p -> p.getId()).setHeader(btnRefresh).setWidth("2em");
		grid.addColumn(p -> p.getStepCode()).setHeader("Code").setAutoWidth(true);
		grid.addColumn(p -> p.getOrderNo()).setHeader("Order").setWidth("3em").setVisible(false);
		grid.addColumn(p -> p.getStatus()).setHeader("Status").setWidth("5em");
		grid.addColumn(p -> p.getError()).setHeader("Error").setWidth("5em");
		grid.addColumn(p -> p.getCommands()).setHeader("Command Executed").setAutoWidth(true);
		grid.addComponentColumn(p -> {
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
		}).setHeader("Approved").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
		grid.addColumn(p -> p.getApprovedBy()).setHeader("App. By").setAutoWidth(true);
		grid.addColumn(p -> dateFormat(p.getApprovalDate())).setHeader("App. Date");
		grid.addColumn(p -> dateFormat(p.getCreated())).setHeader("Created").setAutoWidth(true).setVisible(false);
		grid.addColumn(p -> dateFormat(p.getStarted())).setHeader("Started").setAutoWidth(true);
		grid.addColumn(p -> dateFormat(p.getFinished())).setHeader("Finished").setAutoWidth(true);
		grid.addComponentColumn(p -> {
			Button btnBackward = new Button("", new Icon(VaadinIcon.BACKWARDS));
			btnBackward.addThemeVariants( ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
			btnBackward.setEnabled(processInstance.isTheLatestStep(p));
			btnBackward.addClickListener(e -> {
				confirmAndRun("Backward", "Sure to backward the step : %s?".formatted(p.getStepCode()), ()->setBackward(grid, p));
			});
			return btnBackward;
		}).setHeader("Rollback").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);

		grid.getColumns().forEach(col -> {
			col.setResizable(true);
		});
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);

		fillStepsGrid(grid, processInstance.getCode(), null);
		ProcessInstanceStep firstStep = processInstance.getSteps().get(0);
		grid.select(firstStep);
		
		grid.setMaxHeight("30%");

		TextArea logMemo = new TextArea();
		logMemo.setReadOnly(true);
		logMemo.setSizeFull();

		Tab tabLogs = new Tab(VaadinIcon.NOTEBOOK.create(), new Span("Logs"));
		Tab tabFiles = new Tab(VaadinIcon.PICTURE.create(), new Span("Files"));
		Tab tabVariables = new Tab(VaadinIcon.PICTURE.create(), new Span("Variables"));

		Tabs tabs = new Tabs(tabLogs, tabFiles, tabVariables);
		tabs.setSelectedTab(tabLogs);
		tabs.setWidth("100%");

		HorizontalLayout tabContent = new HorizontalLayout(logMemo);
		tabContent.setSizeFull();

		tabs.addSelectedChangeListener(event -> {
			tabContent.removeAll();
			if (grid.getSelectedItems().isEmpty()) {
				return;
			}
			
			ProcessInstanceStep step = grid.getSelectedItems().iterator().next();
			
			if (event.getSelectedTab().equals(tabLogs)) {
				tabContent.add(logMemo);
			} else if (event.getSelectedTab().equals(tabFiles)) {
				tabContent.add(generateTabForFiles(step));
			} else if (event.getSelectedTab().equals(tabVariables)) {
				tabContent.add(makeVariableGrid(processInstance, step));
			}
		});

		grid.addSelectionListener(p -> {
			tabContent.removeAll();

			Optional<ProcessInstanceStep> selection = p.getFirstSelectedItem();
			if (!selection.isEmpty()) {
				if (tabs.getSelectedTab().equals(tabLogs)) {
					var logs = selection.get().getLogs();
					logMemo.setValue(logs == null ? "" : logs);
					tabContent.add(logMemo);
				} else if (tabs.getSelectedTab().equals(tabFiles)) {
					tabContent.add(generateTabForFiles(selection.get()));
				} else if (tabs.getSelectedTab().equals(tabVariables)) {
					tabContent.add(makeVariableGrid(processInstance, selection.get()));
				} 

			}
		});

		dialog.add(grid);
		dialog.add(tabs);
		dialog.add(tabContent);


		Button cancelButton = new Button("Close", e -> dialog.close());
		dialog.getFooter().add(cancelButton);
		dialog.setWidth("950%");
		dialog.setHeight("90%");
		dialog.setResizable(true);
		dialog.setCloseOnEsc(true);
		dialog.setCloseOnOutsideClick(true);
		dialog.open();

	}

	

	private void fillStepsGrid(Grid<ProcessInstanceStep> grid, String processInstanceCode, String stepCode) {
		ProcessInstance processRefreshed = processService.getProcessInstanceByCode(processInstanceCode);
		grid.setItems(processRefreshed.getSteps());
		if (stepCode==null) {
			var currentStep = processRefreshed.getCurrentStep();
			if (currentStep!=null) {
				grid.select(currentStep);
				
			}
			return;
		}
		
		grid.select(processRefreshed.getSteps().stream().filter(p->p.getStepCode().equals(stepCode)).findAny().get());
		
	}

	private void setBackward(Grid<ProcessInstanceStep> grid, ProcessInstanceStep stepToBackward) {

		if (stepToBackward.getStatus().equals(ProcessInstanceStep.STATUS_NEW)) {
			return;
		}
		ProcessInstance processInstance = stepToBackward.getProcessInstance();
		List<ProcessInstanceStep> steps = processInstance.getSteps();

		int stepCount = 0;
		int previousStepIndex=-1;
		
		for (int i = steps.size() - 1; i >= 0; i--) {
			ProcessInstanceStep step = steps.get(i);
			if (step.getStepCode().equals(stepToBackward.getStepCode())) {
				previousStepIndex = i -1;
				break;
			}
				

			if (!step.getStatus().equals(ProcessInstanceStep.STATUS_NEW)) {
				stepCount++;
			}
		}

		if (stepCount > 0) {
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
		stepToBackward.setInstanceVariables(null);
		stepToBackward.setStarted(null);
		stepToBackward.setFinished(null);

		long countOfNonNew = steps.stream().filter(p -> !p.getStatus().equals(ProcessInstanceStep.STATUS_NEW)).count();

		processInstance.setStatus(countOfNonNew == 0
				? (processInstance.getAttemptNo() == 0 ? ProcessInstance.STATUS_NEW : ProcessInstance.STATUS_RETRY)
				: ProcessInstance.STATUS_RUNNING);
		processInstance.setFinished(null);
		processInstance.setAttemptNo(Integer.max(processInstance.getAttemptNo() - 1, 0));
		processInstance.setError(null);
		processInstance.setFailed(false);

		//reset initial variables
		processInstance.setInstanceVariables(previousStepIndex==-1 ? processInstance.getInitialInstanceVariables() : steps.get(previousStepIndex).getInstanceVariables());
		ProcessInstance saveProcessInstance = processService.saveProcessInstance(processInstance);

		fillStepsGrid(grid, saveProcessInstance.getCode(), stepToBackward.getStepCode());
		notifySuccess("backwarded");
	}

	private VerticalLayout generateTabForFiles(ProcessInstanceStep step) {
		VerticalLayout lay = new VerticalLayout();
		if (step == null)
			return lay;

		List<ProcessInstanceStepFile> files = processService.getProcessInstanceStepFilesByStepId(step);

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
		String title = "instance variable for %s (%s)".formatted(processInstance.getCode(),
				processInstance.getDescription());
		dialog.setHeaderTitle(title);

		VerticalLayout dialogLayout = new VerticalLayout();
		dialogLayout.setSizeFull();

		dialogLayout.add(makeVariableGrid(processInstance, null));

		dialog.add(dialogLayout);
		Button cancelButton = new Button("Close", e -> dialog.close());
		dialog.getFooter().add(cancelButton);
		dialog.setWidth("80%");
		dialog.setHeight("80%");
		dialog.setResizable(true);
		dialog.setCloseOnEsc(true);
		dialog.setCloseOnOutsideClick(true);
		dialog.open();

	}

	private Grid<KeyValue> makeVariableGrid(ProcessInstance processInstance, ProcessInstanceStep processInstanceStep) {
		
		String variablesStr = processInstanceStep == null ? processInstance.getInstanceVariables() : processInstanceStep.getInstanceVariables(); 
		HashMap<String, String> hmVars = HelperUtil.str2HashMap(variablesStr);
		Grid<KeyValue> gridVars = new Grid<>(KeyValue.class, false);
		gridVars.addColumn(p -> p.getKey()).setHeader("Variable Name").setWidth("30%");
		gridVars.addComponentColumn(p -> {
			boolean isMultiline=p.getValue()!=null && p.getValue().split("\n|\r").length>1;
			if (isMultiline) {
				TextArea textArea=new TextArea();
				textArea.setWidthFull();
				textArea.setValue(p.getValue() == null ? "" : p.getValue());
				textArea.setHeight("10em");
				textArea.addValueChangeListener(e -> {
					p.setValue(e.getValue());
				});
				return textArea;
			} 
			TextField textField = new TextField();
			textField.setWidthFull();
			textField.setValue(p.getValue() == null ? "" : p.getValue());
			textField.addValueChangeListener(e -> {
				p.setValue(e.getValue());
			});
			return textField;
			
			
		}).setHeader("Value").setWidth("55%");

		gridVars.addComponentColumn(p -> {
			Button btnUpdate = new Button("", new Icon(VaadinIcon.DOWNLOAD));
			btnUpdate.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
			btnUpdate.setDisableOnClick(true);
			btnUpdate.addClickListener(e -> {
				hmVars.put(p.getKey(), p.getValue());
				String changedVariables = HelperUtil.hashMap2String(hmVars);
				if (processInstanceStep==null) {
					processInstance.setInstanceVariables(changedVariables);
					
				} else {
					processInstanceStep.setInstanceVariables(changedVariables);
				}
				
				processService.saveProcessInstance(processInstance);
				notifyInfo("variable changed");
				btnUpdate.setEnabled(true);
			});
			return btnUpdate;
		}).setHeader("Update").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);

		gridVars.addComponentColumn(p -> {
			Button btnRemove = new Button("", new Icon(VaadinIcon.TRASH));
			btnRemove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
			btnRemove.addClickListener(e -> {
				
				confirmAndRun("Remove", "Sure to remove this parameter : %s=%s?".formatted(p.getKey(),HelperUtil.limitString(p.getValue(),500)), ()->{
					hmVars.remove(p.getKey());
					String changedVariables = HelperUtil.hashMap2String(hmVars);
					
					if (processInstanceStep==null) {
						processInstance.setInstanceVariables(changedVariables);
						
					} else {
						processInstanceStep.setInstanceVariables(changedVariables);
					}
					
					processService.saveProcessInstance(processInstance);
					notifyInfo("variable removed");
					setVariableGridItems(gridVars, hmVars);
				});
				
			});
			return btnRemove;
		}).setHeader("Remove").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);

		gridVars.getColumns().forEach(col -> {
			col.setResizable(true);
		});
		gridVars.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT,
				GridVariant.LUMO_ROW_STRIPES);
		gridVars.setSizeFull();

		setVariableGridItems(gridVars, hmVars);
		
		return gridVars;
	}

	private void setVariableGridItems(Grid<KeyValue> gridVars, HashMap<String, String> hmVars) {
		List<KeyValue> items = new ArrayList<KeyValue>();
		hmVars.keySet().stream().forEach(key -> {
			items.add(new KeyValue(key, (String) hmVars.get(key)));
		});

		Collections.sort(items, new Comparator<KeyValue>() {

			@Override
			public int compare(KeyValue o1, KeyValue o2) {
				return o1.getKey().compareTo(o2.getKey());
			}

		});

		gridVars.setItems(items);

	}



	private void fillGrid() {
		ProcessDefinition processDefinition=comboForProcessDefinition.getValue();

		if (processDefinition!=null) {
			setData(processDefinition);
		}

	}

	private void setData(ProcessDefinition processDefinition) {
		fillProcessInstanceGrid(processDefinition);
	}


	private void runProcessInstance(ProcessInstance processInstance) {

		int maxThreadCount = Integer.valueOf(processService.getEnvProperty("max.thread"));
		
		int currentThreadCount = RunnerSingleton.getInstance().getRunningProcessCount();
		if (currentThreadCount >= maxThreadCount) {
			notifyError("no thread to run this instance");
			return;
		}

		ProcessDefinition processDefinition = processService.getProcessDefinitionById(processInstance.getProcessDefinitionId());
		
		if (RunnerSingleton.getInstance().hasRunningInstance(processInstance.getCode(), processDefinition.getCode())) {
			notifyError("this instance %s is already running. Please wait.".formatted(processInstance.getCode()));
			return;
		}

		Thread thread = new Thread(new ThreadForInstanceRunner(processService, processInstance));
		thread.start();

		notifySuccess("tread succssfully started for instance %s, thread id : %s ".formatted(processInstance.getCode(),
				String.valueOf(thread.getId())));

	}

}
