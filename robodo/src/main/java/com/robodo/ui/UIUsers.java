package com.robodo.ui;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.robodo.model.ProcessInstance;
import com.robodo.model.User;
import com.robodo.model.UserRole;
import com.robodo.security.SecurityService;
import com.robodo.services.ProcessService;
import com.robodo.utils.HelperUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "/users")
@RolesAllowed(UserRole.ROLE_ADMIN)
public class UIUsers extends UIBase  {

	private static final long serialVersionUID = 1L;

	ProcessInstance processInstance;

	@Autowired
	public UIUsers(ProcessService processService, SecurityService securityService) {
		super(processService, securityService);
		setTitle("Users");
		
		 drawScreen();
	}

	Grid<User> grid = new Grid<>(User.class, false);

	private void drawScreen() {
		
		removeAll();
		makeGrid();
		
		grid.setSizeFull();
		
		add(grid);

	}

	private void makeGrid() {
		grid.addColumn(p->p.getUsername()).setKey("username").setHeader("Username").setSortable(true).setAutoWidth(true);
		grid.addColumn(p->HelperUtil.decrypt(p.getPassword())).setKey("password").setHeader("Password").setAutoWidth(true);
		grid.addColumn(p->p.getEmail()).setKey("email").setHeader("Email").setSortable(true).setAutoWidth(true);
		grid.addColumn(p->p.getFullname()).setKey("fullName").setHeader("Full Name").setSortable(true).setAutoWidth(true);
		grid.addComponentColumn(p->makeTrueFalseIcon(p.isValid(), VaadinIcon.CHECK.create(), VaadinIcon.CLOSE.create())).setKey("valid").setHeader("Valid").setSortable(true).setAutoWidth(true);
		grid.addComponentColumn(p->{
			return makeRolesEditor(p);
		}).setKey("roles").setHeader("Roles").setAutoWidth(true);
		grid.addColumn(p->dateFormat(p.getLastLogin())).setHeader("Last Login").setAutoWidth(true);
		grid.addColumn(p->dateFormat(p.getLastPasswordChange())).setHeader("Last Password Change").setAutoWidth(true);
		grid.addColumn(p->dateFormat(p.getCreated())).setHeader("Created").setAutoWidth(true);
		grid.addColumn(p->dateFormat(p.getUpdated())).setHeader("Updated").setAutoWidth(true);
		grid.addComponentColumn(p->{
			Button btnRemove = new Button("", new Icon(VaadinIcon.PASSWORD));
			btnRemove.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
			btnRemove.addClickListener(e -> {
				//change passowrd
			});
			return btnRemove;

		}).setHeader("Password").setAutoWidth(true);
		
		grid.addComponentColumn(p->{
			Button btnRemove = new Button("", new Icon(VaadinIcon.TRASH));
			btnRemove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
			btnRemove.addClickListener(e -> {
				confirmAndRun("Remove", "Sure to remove this instance : %s".formatted(p.getUsername()), ()-> removeUser(p));
			});
			return btnRemove;

		}).setHeader("Remove").setAutoWidth(true);

		grid.setWidthFull();

		grid.setColumnReorderingAllowed(true);
		
		
		grid.getColumns().forEach(col -> {
			col.setResizable(true);
		});

		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT,
				GridVariant.LUMO_ROW_STRIPES);
		
		
		
		
		Editor<User> editor = grid.getEditor();
		
		Grid.Column<User> editColumn = grid.addComponentColumn(user -> {
		    Button editButton = new Button("Edit");
		    editButton.addClickListener(e -> {
		        if (editor.isOpen())
		            editor.cancel();
		        grid.getEditor().editItem(user);
		    });
		    return editButton;
		}).setWidth("150px").setFlexGrow(0);
		
		Binder<User> binder = new Binder<>(User.class);
		editor.setBinder(binder);
		editor.setBuffered(true);
		
		addEditorTextField(grid, binder, "username" ,User::getUsername, User::setUsername);
		addEditorTextField(grid, binder, "email" ,User::getEmail, User::setEmail);
		addEditorTextField(grid, binder, "fullName" ,User::getFullname, User::setFullname);
		addEditorCheckbox(grid, binder, "valid" ,User::isValid, User::setValid);
		
		//---------------------------------------------------------
		Button saveButton = new Button("Save", e -> editor.save());
        Button cancelButton = new Button(VaadinIcon.CLOSE.create(),
                e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR);
        HorizontalLayout actions = new HorizontalLayout(saveButton,
                cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);
		
        editor.addSaveListener(e->{
        	
        	if (!isValidUsername(e.getItem().getUsername())) {
        		e.getSource().cancel();
        		runAndInform("Error", "Username entered is invalid", ()->fillGrid());
        		return;
        	}

        	
        	if (!isValidEmailAddress(e.getItem().getEmail())) {
        		e.getSource().cancel();
        		runAndInform("Error", "Email entered is invalid", ()->fillGrid());
        		return;
        	}

        	if (e.getItem().getFullname().strip().isEmpty()) {
        		e.getSource().cancel();
        		runAndInform("Error", "Fullname is empty", ()->fillGrid());
        		return;
        	}

        	if (hasSameUsername(e.getItem())) {
        		e.getSource().cancel();
        		runAndInform("Error", "This username is already exists", ()->fillGrid());
        		return;
        	}
        	
        	if (hasSameEmail(e.getItem())) {
        		e.getSource().cancel();
        		runAndInform("Error", "This email is already exists", ()->fillGrid());
        		return;
        	}
         	processService.saveUser(e.getItem());
        	notifyInfo("saved");
        	
        });
        

        editor.addCancelListener(e->{
        	notifyInfo("cancel");
        });



		fillGrid();
		
	}

	VerticalLayout makeRolesEditor(User user) {
		VerticalLayout lay=new VerticalLayout();
		lay.setSpacing(false);
		lay.setMargin(false);
		
		
		
		MultiSelectListBox<String> mcb=new MultiSelectListBox<String>();
		mcb.setItems(processService.getRoles());
		mcb.setItemLabelGenerator(p->p);
		mcb.setVisible(false);
		mcb.setWidthFull();
		user.getRoles().forEach(p->{
			mcb.select(p.getRole());
		});
		
		Button btSave=new Button("Save", VaadinIcon.UPLOAD.create());
		Button btEdit=new Button(user.getRoles().toString());
		
		btSave.setWidthFull();
		btSave.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
		btSave.setVisible(false);
		btSave.addClickListener(e->{
			setRoles(user,mcb.getSelectedItems());
			processService.saveUser(user);
			notifyInfo("roles changed");
			mcb.setVisible(false);
			btSave.setVisible(false);
			btEdit.setVisible(true);
		});
		
		
		btEdit.setWidthFull();
		btEdit.addClickListener(e->{
			mcb.setVisible(true);
			btSave.setVisible(true);
			btEdit.setVisible(false);
		});

		
		
		
		
		lay.add(mcb);
		lay.add(btEdit);
		lay.add(btSave);
		
		lay.setWidthFull();
		
		return lay;
	}



	private void setRoles(User user, Set<String> roleNames) {
		user.getRoles().clear();
		Iterator<String> it = roleNames.iterator();
		while(it.hasNext()) {
			UserRole userRole=new UserRole();
			userRole.setUser(user);
			userRole.setRole(title);
			user.getRoles().add(userRole);
		}
		
	}

	private boolean isValidUsername(String username) {
		return HelperUtil.patternMatches(username, "^[A-Za-z]\\w{5,29}$");
	}

	private boolean isValidEmailAddress(String email) {
		return HelperUtil.patternMatches(email, "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
		        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
	}

	private void fillGrid() {
		grid.setItems(processService.getUsersAll());
	}

	private boolean hasSameEmail(User changedUser) {
		List<User> users=processService.getUsersAll();
		return users.stream().anyMatch(
				p->p.getEmail().equalsIgnoreCase(changedUser.getEmail()) && !p.getId().equals(changedUser.getId())
				);
	}

	private boolean hasSameUsername(User changedUser) {
		List<User> users=processService.getUsersAll();
		return users.stream().anyMatch(
				p->p.getUsername().equalsIgnoreCase(changedUser.getUsername()) && !p.getId().equals(changedUser.getId())
				);
	}

	private void removeUser(User user) {
		processService.removeUser(user);
		fillGrid();
	}

	
	

	
	public void addEditorTextField(
			Grid<User> grid, 
			Binder<User> binder, 
			String columnId, 
			ValueProvider<User, String> getter, 
			Setter<User, String> setter) {
		TextField tx = new TextField();
		tx.setWidthFull();
		binder.forField(tx)
		        .bind(getter, setter);
		grid.getColumnByKey(columnId).setEditorComponent(tx);
		
	}
	

	public void addEditorCheckbox(
			Grid<User> grid, 
			Binder<User> binder, 
			String columnId, 
			ValueProvider<User, Boolean> getter, 
			Setter<User, Boolean> setter) {
		Checkbox ch= new Checkbox();
		binder.forField(ch)
		        .bind(getter, setter);
		grid.getColumnByKey(columnId).setEditorComponent(ch);
		
	}

	

}
