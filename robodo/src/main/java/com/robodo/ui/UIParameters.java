package com.robodo.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.Parameter;
import com.robodo.model.ProcessInstance;
import com.robodo.model.UserRole;
import com.robodo.security.SecurityService;
import com.robodo.services.ProcessService;
import com.robodo.utils.HelperUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "/parameters")
@RolesAllowed(UserRole.ROLE_ADMIN)
public class UIParameters extends UIBase  {

	private static final long serialVersionUID = 1L;

	ProcessInstance processInstance;

	@Autowired
	public UIParameters(ProcessService processService, SecurityService securityService) {
		super(processService, securityService);
		setTitle("Parameters");
		
		 drawScreen();
	}





	Grid<Parameter> grid = new Grid<>(Parameter.class, false);
	Editor<Parameter> editor = null;

	private void drawScreen() {

		removeAll();
		Button btnAddNew = new Button("Add new parameter", new Icon(VaadinIcon.PLUS));
		btnAddNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		btnAddNew.addClickListener(e -> {
			Parameter newParameter=makeNewParameter();
			processService.saveParameter(newParameter);
			fillGrid(newParameter);
			editor.editItem(grid.getSelectedItems().iterator().next());
		});
		
		add(btnAddNew);
		
		makeGrid();

		grid.setSizeFull();

		add(grid);

	}

	private Parameter makeNewParameter() {
		Parameter parameter=new Parameter();
		String id="%s".formatted(String.valueOf(System.currentTimeMillis()));
		parameter.setCode("PARAM_%s".formatted(id));
		parameter.setValue("value%s".formatted(id));
		return parameter;
	}

	private void makeGrid() {
		
		grid.addColumn(p -> p.getCode()).setKey("code").setHeader("Name").setSortable(true)
				.setAutoWidth(true).setFrozen(true);
		grid.addColumn(p -> p.getValue()).setKey("value").setHeader("Value").setSortable(true).setAutoWidth(true);

		grid.addComponentColumn(p -> {
			Button btnRemove = new Button("", new Icon(VaadinIcon.TRASH));
			btnRemove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
			btnRemove.addClickListener(e -> {
				confirmAndRun("Remove", "Sure to remove this parameter : %s".formatted(p.getCode()),
						() -> removeParameter(p));
			});
			return btnRemove;

		}).setHeader("Remove").setAutoWidth(true).setFrozenToEnd(true);

		grid.setWidthFull();

		grid.setColumnReorderingAllowed(true);

		grid.getColumns().forEach(col -> {
			col.setResizable(true);
		});

		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);

		editor = grid.getEditor();

		Grid.Column<Parameter> editColumn = grid.addComponentColumn(parameter -> {
			Button editButton = new Button("Edit");
			editButton.addClickListener(e -> {
				if (editor.isOpen())
					editor.cancel();
				grid.getEditor().editItem(parameter);
			});
			return editButton;
		}).setWidth("150px").setFlexGrow(0).setFrozenToEnd(true);

		Binder<Parameter> binder = new Binder<>(Parameter.class);
		editor.setBinder(binder);
		editor.setBuffered(true);

		addEditorTextField(grid, binder, "code", Parameter::getCode, Parameter::setCode);
		addEditorTextField(grid, binder, "value", Parameter::getValue, Parameter::setValue);

		// ---------------------------------------------------------
		Button saveButton = new Button("Save", e -> editor.save());
		Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
		HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
		actions.setPadding(false);
		editColumn.setEditorComponent(actions);



		editor.addSaveListener(e -> {

			if (!isValidCode(e.getItem().getCode())) {
				e.getSource().cancel();
				runAndInform("Error", "parameter name entered is invalid", () -> fillGrid(e.getItem()));
				return;
			}
			
			if (hasSameCode(e.getItem())) {
				e.getSource().cancel();
				runAndInform("Error", "parameter name already exists", () -> fillGrid(e.getItem()));
				return;
				
			}

			processService.saveParameter(e.getItem());
			notifyInfo("saved");
			fillGrid(e.getItem());

		});


		fillGrid(null);

	}


	private boolean isValidCode(String code) {
		return HelperUtil.patternMatches(code, "^[A-Za-z]\\w{5,100}$");
	}



	private void fillGrid(Parameter parameter) {
		List<Parameter> parametersAll = processService.getParametersAll();
		grid.setItems(parametersAll);
		if (parameter!=null) {
			grid.select(parametersAll.stream().filter(p->p.getCode().equals(parameter.getCode())).findAny().get());
			return;
		} 
	}

	private boolean hasSameCode(Parameter parameter) {
		List<Parameter> parametersAll = processService.getParametersAll();
		return parametersAll.stream().anyMatch(
					p -> p.getCode().equalsIgnoreCase(parameter.getCode()) 
					&& !p.getId().equals(parameter.getId())
					);
	}

	private void removeParameter(Parameter parameter) {
		processService.removeParameter(parameter);
		fillGrid(null);
	}

	public void addEditorTextField(Grid<Parameter> grid, Binder<Parameter> binder, String columnId, ValueProvider<Parameter, String> getter, Setter<Parameter, String> setter) {
		TextField tx = new TextField();
		tx.setWidthFull();
		binder.forField(tx).bind(getter, setter);
		grid.getColumnByKey(columnId).setEditorComponent(tx);

	}


}
