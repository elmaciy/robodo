package com.robodo.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessDefinitionStep;
import com.robodo.model.ProcessInstance;
import com.robodo.model.UserRole;
import com.robodo.security.SecurityService;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.utils.RunnerUtil;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route("/process")
@RolesAllowed(UserRole.ROLE_ADMIN)
public class UIProcess extends UIBase {

	private static final long serialVersionUID = 1L;


	ProcessService processService;

	Grid<ProcessDefinition> gridProcessDefinition;

	@Autowired
	public UIProcess(ProcessService processService, SecurityService securityService) {
		super(processService, securityService);
		this.processService = processService;

		setTitle("Processes", VaadinIcon.COG.create());

		gridProcessDefinition = new Grid<>(ProcessDefinition.class, false);
		gridProcessDefinition.addColumn(p -> p.getId()).setHeader("#").setWidth("3em");
		gridProcessDefinition.addColumn(p -> p.getCode()).setHeader("Code").setAutoWidth(true);
		gridProcessDefinition.addColumn(p -> p.getDescription()).setHeader("Description").setAutoWidth(true);
		gridProcessDefinition.addComponentColumn(p -> {
			Checkbox chActive = new Checkbox(p.isActive());
			chActive.addValueChangeListener(e -> {
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
		gridProcessDefinition.addComponentColumn(p -> {
			var fld = makeIntegerMinMaxField(p.getMaxAttemptCount(), 1, 100);
			fld.addValueChangeListener(e -> {
				Integer value = e.getValue();
				p.setMaxAttemptCount(value);
				processService.saveProcessDefinition(p);
				notifyInfo("maximum attempt count changed");
			});
			return fld;
		}).setHeader("Attempt").setWidth("2em");
		gridProcessDefinition.addComponentColumn(p -> {
			var fld = makeIntegerMinMaxField(p.getMaxThreadCount(), 1, 10);
			fld.addValueChangeListener(e -> {
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
			btnRun.addThemeVariants(ButtonVariant.LUMO_SMALL);
			btnRun.setDisableOnClick(true);
			btnRun.addClickListener(e -> {
				runProcessDiscoverer(p);
				gridProcessDefinition.select(p);
				btnRun.setEnabled(true);
			});
			return btnRun;
		}).setHeader("Discover").setWidth("3em").setTextAlign(ColumnTextAlign.CENTER);
		
		
		gridProcessDefinition.addComponentColumn(p -> {
			Button btnShowSteps = new Button("", new Icon(VaadinIcon.LINES_LIST));
			btnShowSteps.addThemeVariants(ButtonVariant.LUMO_SMALL);
			btnShowSteps.setDisableOnClick(true);
			btnShowSteps.addClickListener(e -> {
				showProcessDefinitionSteps(p);
				btnShowSteps.setEnabled(true);
			});
			return btnShowSteps;
		}).setHeader("Steps").setWidth("3em").setTextAlign(ColumnTextAlign.CENTER);
		;
		//------------------------------------------------------------
		
		gridProcessDefinition.setSizeFull();
		gridProcessDefinition.setColumnReorderingAllowed(true);
		
		gridProcessDefinition.getColumns().forEach(col -> {
			col.setResizable(true);
		});

		gridProcessDefinition.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT,
				GridVariant.LUMO_ROW_STRIPES);
		
		add(gridProcessDefinition);

		fillGrid();
	}

	private void showProcessDefinitionSteps(ProcessDefinition processDefinition) {
		Dialog dialog = new Dialog();
		String title = "steps for %s (%s)".formatted(processDefinition.getCode(), processDefinition.getDescription());
		dialog.setHeaderTitle(title);

		VerticalLayout dialogLayout = new VerticalLayout();
		dialogLayout.setSizeFull();

		// --------------------------------------------------------------------
		Grid<ProcessDefinitionStep> gridProcessDefinitionSteps = new Grid<>(ProcessDefinitionStep.class, false);
		gridProcessDefinitionSteps.addColumn(p -> p.getId()).setHeader("#").setWidth("2em");
		gridProcessDefinitionSteps.addColumn(p -> p.getOrderNo()).setHeader("Order").setWidth("2em");
		gridProcessDefinitionSteps.addColumn(p -> p.getCode()).setHeader("Code").setAutoWidth(true);
		gridProcessDefinitionSteps.addColumn(p -> p.getDescription()).setHeader("Description").setAutoWidth(true);
		gridProcessDefinitionSteps.addColumn(p -> p.getCommands()).setHeader("Command to run").setAutoWidth(true);
		gridProcessDefinitionSteps.addComponentColumn(p -> makeTrueFalseIcon(p.isSingleAtATime())).setHeader("Single").setAutoWidth(true);
		
		gridProcessDefinitionSteps.setWidthFull();
		gridProcessDefinitionSteps.setHeightFull();
		gridProcessDefinitionSteps.setMaxHeight(200, Unit.EM);
		gridProcessDefinitionSteps.getColumns().forEach(col -> {
			col.setResizable(true);
		});
		gridProcessDefinitionSteps.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT,
				GridVariant.LUMO_ROW_STRIPES);

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
	}

	private void runProcessDiscoverer(ProcessDefinition processDefinition) {

		String processId = "DISCOVERY.%s".formatted(processDefinition.getCode());
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
			runner.logger("discovered : new instance [%s] of process [%s]".formatted(processDefinition.getCode(),
					discoveredInstance.getCode()));
			boolean isExists = processService.isProcessInstanceAlreadyExists(discoveredInstance);
			if (isExists) {
				runner.logger("skip process [%s]/%s".formatted(processDefinition.getCode(),
						discoveredInstance.getCode(), processDefinition.getCode()));
				continue;
			}

			processService.saveProcessInstance(discoveredInstance);
			discovered++;
		}

		RunnerSingleton.getInstance().stop(processId);
		notifyInfo(discovered == 0 ? "no new instance is discovered "
				: "%d new instance discovered".formatted(discovered));

	}


}
