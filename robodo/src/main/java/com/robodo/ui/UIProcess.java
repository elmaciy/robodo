package com.robodo.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessDefinitionStep;
import com.robodo.model.UserRole;
import com.robodo.security.SecurityService;
import com.robodo.services.ProcessService;
import com.robodo.utils.HelperUtil;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route("/process")
@RolesAllowed(UserRole.ROLE_ADMIN)
public class UIProcess extends UIBase {

	private static final long serialVersionUID = 1L;


	ProcessService processService;

	Grid<ProcessDefinition> gridProcessDefinition;
	Grid<ProcessDefinitionStep> gridProcessDefinitionSteps;

	@Autowired
	public UIProcess(ProcessService processService, SecurityService securityService) {
		super(processService, securityService);
		this.processService = processService;

		setTitle("Processes", VaadinIcon.COG.create());

		gridProcessDefinition = new Grid<>(ProcessDefinition.class, false);
		gridProcessDefinition.addColumn(p -> p.getId()).setKey("id").setHeader("#").setWidth("1em");
		gridProcessDefinition.addComponentColumn(p ->{
			var editor = makeEditorTextField(
						p.getCode(),
							(c)->{
								p.setCode(c);
								processService.saveProcessDefinition(p);
								notifySuccess("process definition saved");
						}, 
						(e)-> HelperUtil.isValidCode(e),
						(e)-> !hasSameCode(p,e));
			editor.setReadOnly(hasAnyInstance(p));
			return editor;
		}).setHeader("Code").setAutoWidth(true);
		
		gridProcessDefinition.addComponentColumn(p ->{
			return makeEditorTextField(
					p.getDescription(),
						(c)->{
								p.setDescription(c);
								processService.saveProcessDefinition(p);
								notifySuccess("process definition saved");
					}, 
					(e)-> HelperUtil.isValidDescription(e));
		}).setHeader("Description").setAutoWidth(true);
		
		gridProcessDefinition.addComponentColumn(p ->{
			return makeEditorTextField(
					p.getDiscovererClass(),
					(c)->{
						p.setDiscovererClass(c);
						processService.saveProcessDefinition(p);
						notifySuccess("process definition saved");
					}, 
					(e)-> HelperUtil.isValidForFileName(e));
		}).setHeader("Discoverer");

		gridProcessDefinition.addComponentColumn(p -> {
			var fld = makeIntegerMinMaxField(p.getMaxAttemptCount(), 0, 100);
			fld.addValueChangeListener(e -> {
				Integer value = e.getValue();
				p.setMaxAttemptCount(value);
				processService.saveProcessDefinition(p);
				notifyInfo("maximum attempt count changed");
			});
			return fld;
		}).setHeader("Attempt").setWidth("2em");
		
		gridProcessDefinition.addComponentColumn(p -> {
			Checkbox chActive = new Checkbox(p.isActive());

			chActive.addValueChangeListener(e -> {
				boolean newVal = e.getValue();
				if (newVal && p.getSteps().isEmpty()) {
					chActive.setValue(false);
					notifyError("Can not be activeted. Has not step yet.");
				} else {
					p.setActive(newVal);
					processService.saveProcessDefinition(p);					
				}
			});
			return chActive;
		}).setHeader("Active").setWidth("2em").setTextAlign(ColumnTextAlign.CENTER);
		

		gridProcessDefinition.addComponentColumn(p -> {
			var fld = makeIntegerMinMaxField(p.getMaxThreadCount(), 0, 10);
			fld.addValueChangeListener(e -> {
				Integer value = e.getValue();
				p.setMaxThreadCount(value);
				processService.saveProcessDefinition(p);
				notifyInfo("maximum thread count changed");
			});
			return fld;
		}).setHeader("Thread").setWidth("2em");
		
		gridProcessDefinition.addColumn(p->dateFormat(p.getStarted()))
		.setHeader("Started").setWidth("2em");

		gridProcessDefinition.addColumn(p->dateFormat(p.getFinished()))
		.setHeader("Finished").setWidth("2em");

		gridProcessDefinition.addColumn(p->p.getStatus())
		.setHeader("Status").setWidth("2em");
		
		gridProcessDefinition.addComponentColumn(p-> {
			Button btnShowError=new Button("", new Icon(VaadinIcon.LIST));
			btnShowError.addThemeVariants(ButtonVariant.LUMO_SMALL);
			btnShowError.addClickListener(e->{
				showLogs("Logs", p.getLogs());
			});
			return btnShowError;
		})
		.setHeader("Logs").setWidth("2em").setTooltipGenerator(p-> HelperUtil.limitString(p.getLogs(), 1000));
		


		gridProcessDefinition.addComponentColumn(p -> {
			Button btnRun = new Button("", new Icon(VaadinIcon.SEARCH));
			btnRun.addThemeVariants(ButtonVariant.LUMO_SMALL);
			btnRun.setDisableOnClick(true);
			btnRun.setEnabled(!p.getSteps().isEmpty());
			btnRun.addClickListener(e -> {
				runProcessDiscoverer(p);
				gridProcessDefinition.select(p);
				btnRun.setEnabled(true);
			});
			return btnRun;
		}).setHeader("Discover").setWidth("3em").setTextAlign(ColumnTextAlign.CENTER).setFrozenToEnd(true);
		
		gridProcessDefinition.addComponentColumn(p -> {
			Button btnDelete = new Button("", new Icon(VaadinIcon.TRASH));
			btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
			btnDelete.addClickListener(e -> {
				confirmAndRun("Delete", "Sure to remove process definition :%s".formatted(p.getCode()), ()->removeProcessDefinition(p));
			});
			btnDelete.setEnabled(!hasAnyInstance(p));
			return btnDelete;
		}).setHeader("Remove").setWidth("2em").setFrozenToEnd(true).setTextAlign(ColumnTextAlign.CENTER);
		
		
		
		gridProcessDefinition.addSelectionListener(e->{
			ProcessDefinition selection = e.getAllSelectedItems().stream().findFirst().orElse(null);
			selectProcessDefinition(selection);
		});
		
		//------------------------------------------------------------
		gridProcessDefinitionSteps = new Grid<>(ProcessDefinitionStep.class, false);
		
		gridProcessDefinitionSteps.addColumn(p -> p.getId()).setKey("id").setHeader("#").setWidth("1em");
		
		gridProcessDefinitionSteps.addComponentColumn(p->makeReorderingComponent(p.getProcessDefinition(), p))
			.setHeader("Order").setWidth("2em").setTextAlign(ColumnTextAlign.CENTER);
		
		gridProcessDefinitionSteps.addComponentColumn(p ->{
			return makeEditorTextField(
					p.getCode(),
					(c)->{
						p.setCode(c);
						processService.saveProcessDefinition(p.getProcessDefinition());
						notifySuccess("step saved");
					}, 
					(e)-> HelperUtil.isValidCode(e),
					(e)-> p.getProcessDefinition().getSteps().stream().noneMatch(s->s.getCode().equals(e) && !s.getId().equals(p.getId()))
					);
		}).setHeader("Code").setAutoWidth(true);
		
		gridProcessDefinitionSteps.addComponentColumn(p ->{
			return makeEditorTextField(
					p.getDescription(),
					(c)->{
						p.setDescription(c);
						processService.saveProcessDefinition(p.getProcessDefinition());
						notifySuccess("step saved");
					}, 
					(e)-> HelperUtil.isValidDescription(e));
		}).setHeader("Description").setAutoWidth(true);
		
		gridProcessDefinitionSteps.addComponentColumn(p ->{
			return makeEditorTextField(
					p.getCommands(),
					(c)->{
						p.setCommands(c);
						processService.saveProcessDefinition(p.getProcessDefinition());
						notifySuccess("step saved");
					}, 
					(e)-> HelperUtil.isValidCommand(e));
		}).setHeader("Command to run").setAutoWidth(true);
		
		gridProcessDefinitionSteps.addComponentColumn(p -> {
			Checkbox chSingleAtATime = new Checkbox(p.isSingleAtATime());
			chSingleAtATime.addValueChangeListener(e -> {
				boolean newVal = e.getValue();
				p.setSingleAtATime(newVal);
				processService.saveProcessDefinition(p.getProcessDefinition());
				notifySuccess("singleton status changed");
			});
			return chSingleAtATime;
		}).setHeader("Single").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
		
		gridProcessDefinitionSteps.addComponentColumn(p -> {
			Button btnDelete = new Button("", new Icon(VaadinIcon.TRASH));
			btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
			btnDelete.addClickListener(e -> {
				confirmAndRun("Delete", "Sure to remove process step :%s".formatted(p.getCode()), ()->removeProcessDefinitionStep(p));
			});	
			btnDelete.setEnabled(!hasAnyInstance(p.getProcessDefinition()));
			return btnDelete;
		}).setHeader("Steps").setWidth("2em").setFrozenToEnd(true).setTextAlign(ColumnTextAlign.CENTER);
		
		
		
		gridProcessDefinitionSteps.setWidthFull();
		gridProcessDefinitionSteps.setHeightFull();
		gridProcessDefinitionSteps.setMaxHeight(200, Unit.EM);
		gridProcessDefinitionSteps.getColumns().forEach(col -> {
			col.setResizable(true);
		});
		gridProcessDefinitionSteps.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT,
				GridVariant.LUMO_ROW_STRIPES);

		//------------------------------------------------------------
		
		gridProcessDefinition.setSizeFull();
		gridProcessDefinitionSteps.setSizeFull();
		
		gridProcessDefinition.getColumns().forEach(col -> {
			col.setResizable(true);
		});
		gridProcessDefinitionSteps.getColumns().forEach(col -> {
			col.setResizable(true);
		});

		gridProcessDefinition.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT,
				GridVariant.LUMO_ROW_STRIPES);
		gridProcessDefinitionSteps.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT,
				GridVariant.LUMO_ROW_STRIPES);
		
		
		
		
		Button btnAddNewProcessDefinition = new Button("Add", new Icon(VaadinIcon.PLUS));
		btnAddNewProcessDefinition.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		btnAddNewProcessDefinition.setWidthFull();
		btnAddNewProcessDefinition.addClickListener(e -> {
			String id=String.valueOf(System.currentTimeMillis());
			
			ProcessDefinition newProcessDefinition = new ProcessDefinition();
			newProcessDefinition.setActive(false);
			newProcessDefinition.setCode("%s".formatted(id));
			newProcessDefinition.setDescription("Description of %s".formatted(id));
			newProcessDefinition.setDiscovererClass("ClassOf%s".formatted(id));
			newProcessDefinition.setMaxAttemptCount(0);
			newProcessDefinition.setMaxThreadCount(0);
			newProcessDefinition.setSteps(new ArrayList<ProcessDefinitionStep>());
			
			ProcessDefinition saveProcessDefinition = processService.saveProcessDefinition(newProcessDefinition);
			refreshProcessDefinitionGrid();
			selectProcessDefinition(saveProcessDefinition);
			
		});
		
		Button btnAddNewStep = new Button("Add", new Icon(VaadinIcon.PLUS));
		btnAddNewStep.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		btnAddNewStep.setWidthFull();
		btnAddNewStep.addClickListener(e -> {
			
			var it = gridProcessDefinition.getSelectedItems().iterator();
			if (!it.hasNext()) {
				notifyInfo("Choose a process definition to which add a step");
				return;
			}
			
			ProcessDefinition processDefinition = it.next();
			
			boolean hasAnyInstance = hasAnyInstance(processDefinition);
			if (hasAnyInstance) {
				notifyInfo("This process can't be changed since it has instances.");
				return;
			}
			
			String id=String.valueOf(System.currentTimeMillis());
			
			ProcessDefinitionStep newStep = new ProcessDefinitionStep();
			newStep.setProcessDefinition(processDefinition);
			newStep.setCode("%s".formatted(id));
			newStep.setDescription("Description of %s".formatted(id));
			newStep.setCommands("runStepClass  StepClass%s".formatted(id));
			newStep.setSingleAtATime(false);
			newStep.setOrderNo(processDefinition.getNextStepOrder());
			
			processDefinition.getSteps().add(newStep);
			
			ProcessDefinition saveProcessDefinition = processService.saveProcessDefinition(processDefinition);
			refreshProcessDefinitionGrid();
			selectProcessDefinition(saveProcessDefinition);
			selectProcessDefinitionStep(newStep);
			
		});
		
		gridProcessDefinition.getColumnByKey("id").setHeader(btnAddNewProcessDefinition);
		gridProcessDefinitionSteps.getColumnByKey("id").setHeader(btnAddNewStep);
		
		SplitLayout splitter=new SplitLayout(gridProcessDefinition, gridProcessDefinitionSteps);
		splitter.setSizeFull();
		splitter.setSplitterPosition(40);
		splitter.setOrientation(Orientation.VERTICAL);
		
		add(splitter);

		
		refreshProcessDefinitionGrid();
		
	}
	

	

	private HorizontalLayout makeReorderingComponent(ProcessDefinition processDefinition,  ProcessDefinitionStep step) {
		HorizontalLayout lay=new HorizontalLayout();
		lay.setMargin(false);
		lay.setSpacing(false);
		lay.setWidthFull();
		lay.setAlignItems(Alignment.CENTER);
		
		Button btUp = new Button("", new Icon(VaadinIcon.ARROW_UP));
		btUp.addThemeVariants(ButtonVariant.LUMO_ICON);
		btUp.setWidth("1em");
		btUp.setEnabled(!isFirsStep(step));

		btUp.addClickListener(e -> {
			reorderStep(processDefinition, step,"UP");
		});
		
		
		Button btDown = new Button("", new Icon(VaadinIcon.ARROW_DOWN));
		btDown.addThemeVariants(ButtonVariant.LUMO_ICON);
		btDown.setWidth("1em");
		btDown.setEnabled(!isLastStep(step));
		btDown.addClickListener(e -> {
			reorderStep(processDefinition, step,"DOWN");
		});
		
		lay.add(btUp, btDown);
		
		return lay;
	}




	private boolean isFirsStep(ProcessDefinitionStep stepToCheck) {
		return getIndexOfStep(stepToCheck.getProcessDefinition(), stepToCheck)==0;
	}
	
	private boolean isLastStep(ProcessDefinitionStep stepToCheck) {
		return getIndexOfStep(stepToCheck.getProcessDefinition(), stepToCheck)==stepToCheck.getProcessDefinition().getSteps().size()-1;
	}
	
	private int getIndexOfStep(ProcessDefinition processDefinition, ProcessDefinitionStep stepToCheck) {
		return processDefinition.getSteps().indexOf(stepToCheck);
	}




	private void reorderStep(ProcessDefinition  processDefinition, ProcessDefinitionStep processDefinitionStep, String direction) {
		var steps = processDefinition.getSteps();
		
		int a = steps.indexOf(processDefinitionStep);
		int b=direction.equals("UP") ? a - 1  : a + 1;
		
		String tmpOrderNo=steps.get(a).getOrderNo();
		steps.get(a).setOrderNo(steps.get(b).getOrderNo());
		steps.get(b).setOrderNo(tmpOrderNo);
		
		Collections.swap(steps, a, b);
		
		processService.saveProcessDefinition(processDefinitionStep.getProcessDefinition());
		refreshProcessDefinitionGrid();
		selectProcessDefinition(processDefinition);
		selectProcessDefinitionStep(steps.get(b));

	}




	private void removeProcessDefinitionStep(ProcessDefinitionStep step) {
		Iterator<ProcessDefinition> it=gridProcessDefinition.getSelectedItems().iterator();
		
		if (!it.hasNext()) {
			return;
		}
		
		ProcessDefinition processDefinition =it.next();
		processDefinition.setDescription(processDefinition.getDescription()+"+");
		
		if (hasAnyInstance(processDefinition)) {
			runAndInform("Error", "Process step can't be removed since it has inherited instances.", ()->{});
			return;
		}
		
		processService.removeProcessDefinitionStep(step);
		
		refreshProcessDefinitionGrid();
		selectProcessDefinition(processService.getProcessDefinitionById(processDefinition.getId()));
	}




	private boolean hasAnyInstance(ProcessDefinition processDefinition) {
		return processService.hasAnyInstanceByProcessDefinition(processDefinition);
	}




	private void removeProcessDefinition(ProcessDefinition processDefinition) {
		if (hasAnyInstance(processDefinition)) {
			runAndInform("Error", "Process definition can't be removed since it has inherited instances.", ()->{});
			return;
		}
		processService.deleteProcessDefinition(processDefinition);
		refreshProcessDefinitionGrid();
	}




	private boolean hasSameCode(ProcessDefinition processDefinition,  String newCode) {
		return processService.getProcessDefinitions().stream().anyMatch(p->p.getCode().equals(newCode) && !p.getId().equals(processDefinition.getId()));
	}




	public void refreshProcessDefinitionGrid() {
		List<ProcessDefinition> processDefinitions = processService.getProcessDefinitions();
		gridProcessDefinition.setItems(processDefinitions);
		ProcessDefinition selection = processDefinitions.stream().findAny().orElse(null);
	
		selectProcessDefinition(selection);
	}


	private void selectProcessDefinition(ProcessDefinition processDefinition) {
		if (processDefinition==null) {
			return;
		}
		
		gridProcessDefinition.select(processDefinition);
		gridProcessDefinitionSteps.setItems(processDefinition.getSteps());
	}

	private void selectProcessDefinitionStep(ProcessDefinitionStep processDefinitionStep) {
		if (processDefinitionStep==null) {
			return;
		}

		gridProcessDefinitionSteps.select(processDefinitionStep);
	}


}
