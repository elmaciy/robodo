package com.robodo.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.CorporateParameter;
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
		setTitle("Parameters", VaadinIcon.PACKAGE.create());
		
		 drawScreen();
	}





	Grid<CorporateParameter> grid = new Grid<>(CorporateParameter.class, false);
	Editor<CorporateParameter> editor = null;

	private void drawScreen() {

		removeAll();
		Button btnAddNew = new Button("Add new parameter", new Icon(VaadinIcon.PLUS));
		btnAddNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		btnAddNew.addClickListener(e -> {
			CorporateParameter newParameter=makeNewParameter();
			processService.saveCorporateParameter(newParameter);
			fillGrid(newParameter);
			editor.editItem(grid.getSelectedItems().iterator().next());
		});
		
		add(btnAddNew);
		
		makeGrid();

		grid.setSizeFull();

		add(grid);

	}

	private CorporateParameter makeNewParameter() {
		CorporateParameter parameter=new CorporateParameter();
		String id="%s".formatted(String.valueOf(System.currentTimeMillis()));
		parameter.setCode("PARAM_%s".formatted(id));
		parameter.setValue("value%s".formatted(id));
		return parameter;
	}

	private void makeGrid() {
		
		grid.addColumn(p -> p.getCode()).setKey("code").setHeader("Name").setSortable(true)
				.setAutoWidth(true).setFrozen(true);
		grid.addColumn(p -> p.getValue()).setKey("value").setHeader("Value").setSortable(true).setAutoWidth(true);
		grid.addColumn(p -> getEnvironmentValueIfAny(p.getCode())).setKey("envval").setHeader("application.properties").setSortable(true).setAutoWidth(true);

		grid.addComponentColumn(p -> {
			Button btnRemove = new Button("", new Icon(VaadinIcon.TRASH));
			btnRemove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
			btnRemove.addClickListener(e -> {
				confirmAndRun("Remove", "Sure to remove this parameter : %s".formatted(p.getCode()),
						() -> removeParameter(p));
			});
			return btnRemove;

		}).setHeader("Remove").setWidth("5em").setFrozenToEnd(true);

		grid.setWidthFull();

		grid.setColumnReorderingAllowed(true);

		grid.getColumns().forEach(col -> {
			col.setResizable(true);
		});

		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);

		editor = grid.getEditor();

		Grid.Column<CorporateParameter> editColumn = grid.addComponentColumn(parameter -> {
			Button editButton = new Button("Edit");
			editButton.addClickListener(e -> {
				if (editor.isOpen())
					editor.cancel();
				grid.getEditor().editItem(parameter);
			});
			return editButton;
		}).setWidth("5em").setFlexGrow(0).setFrozenToEnd(true);

		Binder<CorporateParameter> binder = new Binder<>(CorporateParameter.class);
		editor.setBinder(binder);
		editor.setBuffered(true);

		addEditorTextField(grid, binder, "code", CorporateParameter::getCode, CorporateParameter::setCode);
		addEditorTextField(grid, binder, "value", CorporateParameter::getValue, CorporateParameter::setValue);

		// ---------------------------------------------------------
		Button saveButton = new Button("Save", e -> editor.save());
		Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
		HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
		actions.setPadding(false);
		editColumn.setEditorComponent(actions);



		editor.addSaveListener(e -> {

			if (!HelperUtil.isValidCode(e.getItem().getCode())) {
				e.getSource().cancel();
				informAndRun("Error", "parameter name entered is invalid : %s".formatted(e.getItem().getCode()), () -> fillGrid(e.getItem()));
				return;
			}
			
			if (hasSameCode(e.getItem())) {
				e.getSource().cancel();
				informAndRun("Error", "parameter name already exists", () -> fillGrid(e.getItem()));
				return;
				
			}

			CorporateParameter savedParameter = processService.saveCorporateParameter(e.getItem());
			notifyInfo("saved");
			fillGrid(savedParameter);

		});


		fillGrid(null);

	}


	private String getEnvironmentValueIfAny(String code) {
		String value = processService.getEnv().getProperty(code);
		if (value==null || value.isBlank()) {
			return "{null}";
		}
		return "{%s}".formatted(value);
	}


	private void fillGrid(CorporateParameter parameter) {
		List<CorporateParameter> parametersAll = processService.getCorporateParametersAll();
		grid.setItems(parametersAll);
		if (parameter!=null) {
			grid.select(parametersAll.stream().filter(p->p.getCode().equals(parameter.getCode())).findAny().get());
			return;
		} 
	}

	private boolean hasSameCode(CorporateParameter parameter) {
		List<CorporateParameter> parametersAll = processService.getCorporateParametersAll();
		return parametersAll.stream().anyMatch(
					p -> p.getCode().equalsIgnoreCase(parameter.getCode()) 
					&& !p.getId().equals(parameter.getId())
					);
	}

	private void removeParameter(CorporateParameter parameter) {
		processService.removeCorporateParameter(parameter);
		fillGrid(null);
	}

	public void addEditorTextField(Grid<CorporateParameter> grid, Binder<CorporateParameter> binder, String columnId, ValueProvider<CorporateParameter, String> getter, Setter<CorporateParameter, String> setter) {
		TextField tx = new TextField();
		tx.setWidthFull();
		binder.forField(tx).bind(getter, setter);
		grid.getColumnByKey(columnId).setEditorComponent(tx);

	}


}
