package com.robodo.ui;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Splitter;
import com.robodo.model.EmailTemplate;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "/emailtemplates")
@RolesAllowed(UserRole.ROLE_ADMIN)
public class UIEmailTemplates extends UIBase  {

	private static final long serialVersionUID = 1L;

	ProcessInstance processInstance;

	@Autowired
	public UIEmailTemplates(ProcessService processService, SecurityService securityService) {
		super(processService, securityService);
		setTitle("Email Templates");
		
		 drawScreen();
	}



	Grid<EmailTemplate> grid = new Grid<>(EmailTemplate.class, false);
	Editor<EmailTemplate> editor = null;

	private void drawScreen() {

		removeAll();
		Button btnAddNew = new Button("Add new template", new Icon(VaadinIcon.PLUS));
		btnAddNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		btnAddNew.addClickListener(e -> {
			EmailTemplate newTemplate=makeNewTemplate();
			EmailTemplate savedTamplate = processService.saveEmailTemplate(newTemplate);
			fillGrid(savedTamplate);
			editor.editItem(grid.getSelectedItems().iterator().next());
		});
		
		add(btnAddNew);
		
		makeGrid();

		grid.setSizeFull();

		add(grid);

	}

	private EmailTemplate  makeNewTemplate() {
		EmailTemplate template=new EmailTemplate();
		String id="%s".formatted(String.valueOf(System.currentTimeMillis()));
		template.setCode("EMAIL%s".formatted(id));
		template.setSubject("user_%s".formatted(id));
		template.setToAddress("%s@acme.com".formatted(id));
		template.setBody("body of the message goes here");
		return template;
	}

	private void makeGrid() {
		
		grid.addColumn(p -> p.getCode()).setKey("code").setHeader("Code").setSortable(true)
				.setAutoWidth(true).setFrozen(true);
		grid.addColumn(p -> p.getSubject()).setKey("subject").setHeader("Subject").setSortable(true).setAutoWidth(true);
		grid.addColumn(p -> p.getToAddress()).setKey("to").setHeader("To").setSortable(true).setAutoWidth(true);
		grid.addColumn(p -> p.getCc()).setKey("cc").setHeader("CC").setSortable(true).setAutoWidth(true);
		grid.addColumn(p -> p.getBcc()).setKey("bcc").setHeader("BCC").setSortable(true).setAutoWidth(true);
		grid.addComponentColumn(p -> makeBodyEditor(p))
				.setTooltipGenerator(p->HelperUtil.limitString(p.getBody(), 1000))
				.setKey("body").setHeader("Body").setAutoWidth(true);

		grid.addComponentColumn(p -> {
			Button btnRemove = new Button("", new Icon(VaadinIcon.TRASH));
			btnRemove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
			btnRemove.addClickListener(e -> {
				confirmAndRun("Remove", "Sure to remove this template : %s".formatted(p.getCode()),
						() -> removeEmailTemplate(p));
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

		Grid.Column<EmailTemplate> editColumn = grid.addComponentColumn(template -> {
			Button editButton = new Button("Edit");
			editButton.addClickListener(e -> {
				if (editor.isOpen())
					editor.cancel();
				grid.getEditor().editItem(template);
			});
			return editButton;
		}).setWidth("150px").setFlexGrow(0).setFrozenToEnd(true);

		Binder<EmailTemplate> binder = new Binder<>(EmailTemplate.class);
		editor.setBinder(binder);
		editor.setBuffered(true);

		addEditorTextField(grid, binder, "code", EmailTemplate::getCode, EmailTemplate::setCode);
		addEditorTextField(grid, binder, "subject", EmailTemplate::getSubject, EmailTemplate::setSubject);
		addEditorTextField(grid, binder, "to", EmailTemplate::getToAddress, EmailTemplate::setToAddress);
		addEditorTextField(grid, binder, "cc", EmailTemplate::getCc, EmailTemplate::setCc);
		addEditorTextField(grid, binder, "bcc", EmailTemplate::getBcc, EmailTemplate::setBcc);

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
				runAndInform("Error", "Template code entered is invalid", () -> fillGrid(e.getItem()));
				return;
			}

			if (!isValidEmailAddresses(e.getItem().getToAddress())) {
				e.getSource().cancel();
				runAndInform("Error", "To addresses entered are invalid", () -> fillGrid(e.getItem()));
				return;
			}
			
			if (!isValidEmailAddresses(e.getItem().getCc())) {
				e.getSource().cancel();
				runAndInform("Error", "CC addresses entered are invalid", () -> fillGrid(e.getItem()));
				return;
			}
			
			if (!isValidEmailAddresses(e.getItem().getBcc())) {
				e.getSource().cancel();
				runAndInform("Error", "BCC addresses entered are invalid", () -> fillGrid(e.getItem()));
				return;
			}

			if (e.getItem().getSubject().strip().isEmpty()) {
				e.getSource().cancel();
				runAndInform("Error", "Subject is empty", () -> fillGrid(e.getItem()));
				return;
			}

			if (hasSameCode(e.getItem())) {
				e.getSource().cancel();
				runAndInform("Error", "This code is already exists", () -> fillGrid(e.getItem()));
				return;
			}

			processService.saveEmailTemplate(e.getItem());
			notifyInfo("saved");
			fillGrid(e.getItem());

		});


		fillGrid(null);

	}

	VerticalLayout makeBodyEditor(EmailTemplate template) {
		VerticalLayout lay = new VerticalLayout();
		lay.setSpacing(false);
		lay.setMargin(false);


		TextArea ta = new TextArea();
		ta.setVisible(false);
		ta.setWidthFull();
		ta.setValue(template.getBody());
		ta.setWidthFull();
		ta.setMinHeight("20em");
		ta.setMaxHeight("50em");


		Button btSave = new Button("Save", VaadinIcon.UPLOAD.create());
		Button btCancel = new Button("Cancel", VaadinIcon.CLOSE.create());
		HorizontalLayout layButtons=new HorizontalLayout(btCancel,btSave);
		layButtons.setMargin(false);
		layButtons.setVisible(false);
		
		
		Button btEdit = new Button(HelperUtil.limitString(template.getBody(), 20));

		btSave.setWidth("50%");
		btSave.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
		btSave.addClickListener(e -> {
			template.setBody(ta.getValue());
			processService.saveEmailTemplate(template);
			fillGrid(template);
			
			notifyInfo("template changed");
			ta.setVisible(false);
			layButtons.setVisible(false);
			btEdit.setVisible(true);
		});
		
		btCancel.setWidth("50%");
		btCancel.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
		btCancel.addClickListener(e -> {
			ta.setVisible(false);
			layButtons.setVisible(false);
			btEdit.setVisible(true);
		});

		btEdit.setWidthFull();
		btEdit.addClickListener(e -> {
			grid.getColumnByKey("body").setWidth("%50");
			ta.setVisible(true);
			layButtons.setVisible(true);
			btEdit.setVisible(false);
		});

		lay.add(ta);
		lay.add(btEdit);
		lay.add(layButtons);

		lay.setWidthFull();

		return lay;
	}


	private boolean isValidCode(String username) {
		return HelperUtil.patternMatches(username, "^[A-Za-z]\\w{5,100}$");
	}

	private boolean isValidEmailAddresses(String emailList) {
		List<String> emails = Splitter.on(",").omitEmptyStrings().splitToList(emailList);
		return emails.stream().allMatch(p->HelperUtil.isValidEmailAddress(p));
	}

	private void fillGrid(EmailTemplate emailTemplate) {
		List<EmailTemplate> templatesAll = processService.getEmailTemplateAll();
		grid.setItems(templatesAll);
		if (emailTemplate!=null) {
			grid.select(templatesAll.stream().filter(p->p.getId().equals(emailTemplate.getId())).findAny().get());
			return;
		} 
		
	}

	private boolean hasSameCode(EmailTemplate emailTemplate) {
		List<EmailTemplate> templatesAll = processService.getEmailTemplateAll();
		return templatesAll.stream().anyMatch(p -> p.getCode().equalsIgnoreCase(emailTemplate.getCode())
				&& !p.getId().equals(emailTemplate.getId()));
	}

	private void removeEmailTemplate(EmailTemplate emailTemplate) {
		processService.removeEmailTemplate(emailTemplate);
		fillGrid(null);
	}

	public void addEditorTextField(Grid<EmailTemplate> grid, Binder<EmailTemplate> binder, String columnId,
			ValueProvider<EmailTemplate, String> getter, Setter<EmailTemplate, String> setter) {
		TextField tx = new TextField();
		tx.setWidthFull();
		binder.forField(tx).bind(getter, setter);
		grid.getColumnByKey(columnId).setEditorComponent(tx);

	}

}
