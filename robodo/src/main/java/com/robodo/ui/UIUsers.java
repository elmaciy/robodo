package com.robodo.ui;

import java.time.LocalDateTime;
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
public class UIUsers extends UIBase {

	private static final long serialVersionUID = 1L;

	ProcessInstance processInstance;

	@Autowired
	public UIUsers(ProcessService processService, SecurityService securityService) {
		super(processService, securityService);
		setTitle("Users", VaadinIcon.USER.create());

		drawScreen();
	}

	Grid<User> grid = new Grid<>(User.class, false);
	Editor<User> editor = null;

	private void drawScreen() {

		removeAll();
		Button btnAddNew = new Button("Add new user", new Icon(VaadinIcon.PLUS));
		btnAddNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		btnAddNew.addClickListener(e -> {
			User newUser=makeNewUser();
			User saveUser = processService.saveUser(newUser);
			UserRole newUserRole=new UserRole(saveUser.getId(), UserRole.ROLE_USER);
			processService.saveUserRole(newUserRole);
			fillGrid(saveUser);
			editor.editItem(grid.getSelectedItems().iterator().next());
		});
		
		add(btnAddNew);
		
		makeGrid();

		grid.setSizeFull();

		add(grid);

	}

	private User makeNewUser() {
		User user=new User();
		String id="%s".formatted(String.valueOf(System.currentTimeMillis()));
		
		user.setUsername("user_%s".formatted(id));
		user.setEmail("%s@acme.com".formatted(id));
		user.setFullname("Full Name");
		user.setPassword(HelperUtil.encrypt(id));
	
		return user;
	}

	private void makeGrid() {
		grid.addColumn(p -> p.getUsername()).setKey("username").setHeader("Username").setSortable(true)
				.setAutoWidth(true).setFrozen(true);
		grid.addColumn(p -> p.getEmail()).setKey("email").setHeader("Email").setSortable(true).setAutoWidth(true);
		grid.addColumn(p -> p.getFullname()).setKey("fullName").setHeader("Full Name").setSortable(true)
				.setAutoWidth(true);
		grid.addComponentColumn(p -> makeTrueFalseIcon(p.isValid()))
				.setKey("valid").setHeader("Valid").setSortable(true).setAutoWidth(true);
		grid.addComponentColumn(p -> makeRolesEditor(p)).setKey("roles").setHeader("Roles").setAutoWidth(true);
		grid.addComponentColumn(p -> makePasswordEditor(p)).setKey("password").setHeader("Password").setWidth("10em");
		grid.addColumn(p -> dateFormat(p.getLastPasswordChange())).setHeader("Last Password Change").setAutoWidth(true);
	
		grid.addComponentColumn(p -> {
			Button btnRemove = new Button("", new Icon(VaadinIcon.TRASH));
			btnRemove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
			btnRemove.setEnabled(!p.getUsername().equals("admin"));
			btnRemove.addClickListener(e -> {
				confirmAndRun("Remove", "Sure to remove this instance : %s".formatted(p.getUsername()),
						() -> removeUser(p));
			});
			return btnRemove;

		}).setHeader("Remove").setWidth("3em").setFrozenToEnd(true);

		grid.setWidthFull();

		grid.setColumnReorderingAllowed(true);

		grid.getColumns().forEach(col -> {
			col.setResizable(true);
		});

		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);

		editor = grid.getEditor();

		Grid.Column<User> editColumn = grid.addComponentColumn(user -> {
			Button editButton = new Button("Edit");
			editButton.addClickListener(e -> {
				if (editor.isOpen())
					editor.cancel();
				grid.getEditor().editItem(user);
			});
			return editButton;
		}).setWidth("150px").setFrozenToEnd(true);

		Binder<User> binder = new Binder<>(User.class);
		editor.setBinder(binder);
		editor.setBuffered(true);

		addEditorTextField(grid, binder, "username", User::getUsername, User::setUsername);
		addEditorTextField(grid, binder, "email", User::getEmail, User::setEmail);
		addEditorTextField(grid, binder, "fullName", User::getFullname, User::setFullname);
		addEditorCheckbox(grid, binder, "valid", User::isValid, User::setValid);

		// ---------------------------------------------------------
		Button saveButton = new Button("Save", e -> editor.save());
		Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
		HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
		actions.setPadding(false);
		editColumn.setEditorComponent(actions);
		
		editor.addOpenListener(e -> {
			boolean isAdmin = e.getItem().getUsername().equals("admin");
			if (isAdmin) {
				notifyError("not alloved to edit admin");
				e.getSource().cancel();
			}
		});

		editor.addSaveListener(e -> {

			if (!HelperUtil.isValidCode(e.getItem().getUsername())) {
				e.getSource().cancel();
				runAndInform("Error", "Username entered is invalid", () -> fillGrid(e.getItem()));
				return;
			}

			if (!HelperUtil.isValidEmailAddress(e.getItem().getEmail())) {
				e.getSource().cancel();
				runAndInform("Error", "Email entered is invalid", () -> fillGrid(e.getItem()));
				return;
			}

			if (e.getItem().getFullname().strip().isEmpty()) {
				e.getSource().cancel();
				runAndInform("Error", "Fullname is empty", () -> fillGrid(e.getItem()));
				return;
			}

			if (hasSameUsername(e.getItem())) {
				e.getSource().cancel();
				runAndInform("Error", "This username is already exists", () -> fillGrid(e.getItem()));
				return;
			}

			if (hasSameEmail(e.getItem())) {
				e.getSource().cancel();
				runAndInform("Error", "This email is already exists", () -> fillGrid(e.getItem()));
				return;
			}
			processService.saveUser(e.getItem());
			notifyInfo("saved");

		});


		fillGrid(null);

	}

	VerticalLayout makeRolesEditor(User user) {
		VerticalLayout lay = new VerticalLayout();
		lay.setSpacing(false);
		lay.setMargin(false);
		
		lay.setEnabled(!user.getUsername().equals("admin"));

		MultiSelectListBox<String> mcb = new MultiSelectListBox<String>();
		mcb.setItems(processService.getRoles());
		mcb.setItemLabelGenerator(p -> p);
		mcb.setVisible(false);
		mcb.setWidthFull();
		List<UserRole> userRoles = processService.getUserRoles(user);
		userRoles.forEach(p -> {
			mcb.select(p.getRole());
		});

		Button btSave = new Button("Save", VaadinIcon.UPLOAD.create());
		Button btCancel = new Button("Cancel", VaadinIcon.CLOSE.create());
		HorizontalLayout layButtons=new HorizontalLayout(btCancel,btSave);
		layButtons.setMargin(false);
		layButtons.setVisible(false);
		
		
		Button btEdit = new Button(userRoles.toString());

		btSave.setWidth("50%");
		btSave.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
		btSave.addClickListener(e -> {
			setRoles(user, mcb.getSelectedItems());
			processService.saveUser(user);
			fillGrid(user);
			
			notifyInfo("roles changed");
			mcb.setVisible(false);
			layButtons.setVisible(false);
			btEdit.setVisible(true);
		});
		
		btCancel.setWidth("50%");
		btCancel.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
		btCancel.addClickListener(e -> {
			mcb.setVisible(false);
			layButtons.setVisible(false);
			btEdit.setVisible(true);
		});

		btEdit.setWidthFull();
		btEdit.addClickListener(e -> {
			mcb.setVisible(true);
			layButtons.setVisible(true);
			btEdit.setVisible(false);
		});

		lay.add(mcb);
		lay.add(btEdit);
		lay.add(layButtons);

		lay.setWidthFull();

		return lay;
	}

	private void setRoles(User user, Set<String> roleNames) {

		processService.removeRolesByUser(user);

		Iterator<String> it = roleNames.iterator();
		while (it.hasNext()) {
			String roleName = it.next();
			UserRole userRole = new UserRole();
			userRole.setUserId(user.getId());
			userRole.setRole(roleName);

			processService.saveUserRole(userRole);
		}

	}
	
	
	VerticalLayout makePasswordEditor(User user) {
		
		
		VerticalLayout lay = new VerticalLayout();
		lay.setSpacing(false);
		lay.setMargin(false);
		
		PasswordField pass1=new PasswordField();
		PasswordField pass2=new PasswordField();
		
		pass1.setValue(HelperUtil.decrypt(user.getPassword()));
		pass2.setValue(pass1.getValue());
		
		pass1.setWidthFull();
		pass2.setWidthFull();
	
		pass1.setAutoselect(true);
		pass2.setAutoselect(true);
		
		pass1.setPlaceholder("Enter new wassword");
		pass2.setPlaceholder("Re-enter new wassword");
		
		VerticalLayout layPassword=new VerticalLayout(pass1,pass2);
		layPassword.setVisible(false);
		layPassword.setMargin(false);
		layPassword.setSpacing(false);
		
		Button btEdit = new Button("", VaadinIcon.PASSWORD.create());


		Button btSave = new Button("", VaadinIcon.UPLOAD.create());
		Button btCancel = new Button("", VaadinIcon.CLOSE.create());
		
		
		HorizontalLayout layButtons=new HorizontalLayout(btCancel,btSave);
		
		layButtons.setMargin(true);
		layButtons.setVisible(false);
		
	

		btSave.setWidth("50%");
		btSave.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
		btSave.addClickListener(e -> {
			boolean isValidPasswords=checkPasswordsAndInform(pass1.getValue(), pass2.getValue());
			if (!isValidPasswords) {
				return;
			}
			
			user.setPassword(HelperUtil.encrypt(pass1.getValue()));
			user.setLastPasswordChange(LocalDateTime.now());
			processService.saveUser(user);
			
			fillGrid(user);
			
			notifySuccess("password changed");
			btEdit.setVisible(true);
			layPassword.setVisible(false);
			layButtons.setVisible(false);
		});
		
		btCancel.setWidth("50%");
		btCancel.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
		btCancel.addClickListener(e -> {
			btEdit.setVisible(true);
			layPassword.setVisible(false);
			layButtons.setVisible(false);
		});
		
		btEdit.setWidthFull();
		btEdit.addClickListener(e->{
			
			grid.getColumnByKey("password").setWidth("20em");
			
			btEdit.setVisible(false);
			layPassword.setVisible(true);
			layButtons.setVisible(true);
			
			pass1.focus();
		});


		lay.add(btEdit);
		lay.add(layPassword);
		lay.add(layButtons);

		lay.setWidthFull();

		return lay;
	}

	private boolean checkPasswordsAndInform(String pass1, String pass2) {
		int maxChar=6;
		
		String msg=null;
		if (pass1==null || pass1.isBlank()) {
			msg="Password 1 is empty";
		} else if (pass2==null || pass2.isBlank()) {
			msg="Password 2 is empty";
		} else if (!pass1.equals(pass2)) {
			msg="Passwords entered are different";
		} else if (pass1.length()<6) {
			msg="Length of the password should be at least %d characters".formatted(maxChar);
		}
		
		if (msg!=null) {
			notifyError(msg);
			return false;
		}
		
		return true;
	}


	private void fillGrid(User user) {
		List<User> usersAll = processService.getUsersAll();
		grid.setItems(usersAll);
		if (user!=null) {
			grid.select(usersAll.stream().filter(p->p.getId().equals(user.getId())).findAny().get());
			return;
		} 
		
	}

	private boolean hasSameEmail(User changedUser) {
		List<User> users = processService.getUsersAll();
		return users.stream().anyMatch(
				p -> p.getEmail().equalsIgnoreCase(changedUser.getEmail()) && !p.getId().equals(changedUser.getId()));
	}

	private boolean hasSameUsername(User changedUser) {
		List<User> users = processService.getUsersAll();
		return users.stream().anyMatch(p -> p.getUsername().equalsIgnoreCase(changedUser.getUsername())
				&& !p.getId().equals(changedUser.getId()));
	}

	private void removeUser(User user) {
		processService.removeUser(user);
		fillGrid(null);
	}

	public void addEditorTextField(Grid<User> grid, Binder<User> binder, String columnId,
			ValueProvider<User, String> getter, Setter<User, String> setter) {
		TextField tx = new TextField();
		tx.setWidthFull();
		binder.forField(tx).bind(getter, setter);
		grid.getColumnByKey(columnId).setEditorComponent(tx);

	}

	public void addEditorCheckbox(Grid<User> grid, Binder<User> binder, String columnId,
			ValueProvider<User, Boolean> getter, Setter<User, Boolean> setter) {
		Checkbox ch = new Checkbox();
		binder.forField(ch).bind(getter, setter);
		grid.getColumnByKey(columnId).setEditorComponent(ch);

	}

}
