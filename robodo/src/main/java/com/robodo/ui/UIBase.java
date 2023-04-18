package com.robodo.ui;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.springframework.security.core.userdetails.UserDetails;

import com.robodo.model.KeyValue;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.model.RunningProcess;
import com.robodo.security.SecurityService;
import com.robodo.services.ProcessService;
import com.robodo.singleton.QueueSingleton;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.utils.HelperUtil;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;


@SpringComponent
@Route(value = "/")
@AnonymousAllowed
@UIScope
@PageTitle("Robo.do")
public class UIBase extends AppLayout {
	private static final long serialVersionUID = 1L;

	ProcessService processService;
	SecurityService securityService;
	
	VerticalLayout root = new VerticalLayout();
	
	Span titleSpan = new Span("Robo.do");
	Icon icon=VaadinIcon.RANDOM.create();
	String title="Base Page";


	public UIBase(ProcessService processService, SecurityService securityService) {
		this.processService=processService;
		this.securityService=securityService;
		
		root.removeAll();
		root.setWidth("100%");
		root.setHeight("100%");
		root.setSpacing(false);
		root.setMargin(false);
		setContent(root);
		
		getElement().getStyle().set("height", "100%");
		getElement().getStyle().set("width", "100%");
		
		DrawerToggle toggle = new DrawerToggle();
		
		setTitle(title, icon);

		if (isAuthenticated()) {
	        addToNavbar(toggle, titleSpan);
	        addToDrawer(getRoterLinks());
		}
        
        this.setPrimarySection(Section.NAVBAR);
        
	}


	public void setTitle(String title, Icon icon) {
		this.title=title;
		this.icon=icon;
		titleSpan.removeAll();
		titleSpan.add(icon);
		titleSpan.add(new Span("  "), new Span(" | Robo.do | %s".formatted(this.title)));
		
	}
	
	public boolean isAuthenticated() {
		 UserDetails authenticatedUser = securityService.getAuthenticatedUser();
		 return authenticatedUser!=null;
	}

	public String getAuthenticatedUser() {
		 UserDetails authenticatedUser = securityService.getAuthenticatedUser();
		 if (authenticatedUser==null) {
			 return null;
		 }
		 
		return authenticatedUser.getUsername();
		 
	}
	

	private VerticalLayout  getRoterLinks() {
		
		VerticalLayout lay=new VerticalLayout();
		

		Button linkInstance=  makeMenuOption("Instances",VaadinIcon.FLASH.create(),(p)->UI.getCurrent().navigate(UIInstance.class));
		Button lnkShowThread =  makeMenuOption("Thread Info",VaadinIcon.INFO.create(),(p)->showThreads());
		Button linkProcess =  makeMenuOption("Processes",VaadinIcon.COG.create(),(p)->UI.getCurrent().navigate(UIProcess.class));
		Button lnkEmailTemplate =  makeMenuOption("Email Templates",VaadinIcon.INBOX.create(),(p)->UI.getCurrent().navigate(UIEmailTemplates.class));
		Button linkParameter =  makeMenuOption("Parameters",VaadinIcon.PACKAGE.create(),(p)->UI.getCurrent().navigate(UIParameters.class));
		Button linkUser =  makeMenuOption("Users",VaadinIcon.USER.create(),(p)->UI.getCurrent().navigate(UIUsers.class));
		Button linkDashboard =  makeMenuOption("Dashboard",VaadinIcon.DASHBOARD.create(),(p)->UI.getCurrent().navigate(UIDashboard.class));
		
		var authenticatedUser =  securityService.getAuthenticatedUser();
		String accountUsername=authenticatedUser==null ? "" : authenticatedUser.getUsername();
		Button btLogout =  makeMenuOption("Logout [%s]".formatted(accountUsername),VaadinIcon.EXIT.create(),(p)->confirmAndRun("Logout", "Sure to logout", ()->securityService.logout()));
		
		lay.add(linkInstance);
		lay.add(lnkShowThread);
		lay.add(linkProcess);
		lay.add(lnkEmailTemplate);
		lay.add(linkParameter);
		lay.add(linkUser);
		lay.add(linkDashboard);
		lay.add(btLogout);
		
		lay.setSpacing(false);
		
		return lay;
	}
	
	private Button makeMenuOption(String title, Icon icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		
		Button btOption = new Button(title, icon);
		btOption.setWidthFull();
		btOption.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_SMALL);

		btOption.addClickListener(clickListener);
		return btOption;
	}

	public void removeAll() {
		root.removeAll();
	}
	
	public void add(Component component) {
		root.add(component);
	}
	
	
	
	public void notifySuccess(String content) {
		Notification notification = Notification.show(content);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.BOTTOM_END);
	}
	
	public void notifyError(String content) {
		Notification notification = Notification.show(content);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.BOTTOM_END);		
	}
	
	public void notifyInfo(String content) {
		Notification notification = Notification.show(content);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notification.setPosition(Notification.Position.BOTTOM_END);		
	}
	
	public void confirmAndRun(String header, String message, Runnable actionWhenYes) {
		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setHeader(header);
		dialog.setText(message);
		dialog.setCancelable(false);
		
		dialog.setRejectable(true);
		dialog.setConfirmText("YES");
		dialog.addConfirmListener(event -> {
			actionWhenYes.run();
		});
		
		dialog.setRejectable(true);
		dialog.setRejectText("NO");
		
	
		dialog.open();
	}


	public void informAndRun(String header, String message, Runnable actionAfterInform) {
		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setHeader(header);
		dialog.setText(message);
		dialog.setCancelable(false);
		
		dialog.setRejectable(false);
		dialog.setConfirmText("OK");
		dialog.addConfirmListener(event -> {
			actionAfterInform.run();
		});
		dialog.open();
	}	
	
	public void runAndInform(String header, String message, Runnable actionAfterInform) {
		actionAfterInform.run();
		
		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setHeader(header);
		dialog.setText(message);
		dialog.setCancelable(false);
		
		dialog.setRejectable(false);
		dialog.setConfirmText("OK");
		dialog.open();
	}	
	
	public Image getImage(ProcessInstanceStep step, ProcessInstanceStepFile file) {
		byte[] imageBytes=HelperUtil.byteArr2Blob(file.getBinarycontent());
		StreamResource resource = new StreamResource("%s.png".formatted(String.valueOf(file.getId())), () -> new ByteArrayInputStream(imageBytes));
		Image image = new Image(resource, file.getDescription());

		image.setSizeFull();
		return image;
	}
	
	public IntegerField makeIntegerMinMaxField(int current, int min, int max) {
		IntegerField integerField = new IntegerField();
		integerField.setWidth("10em");
		integerField.setMin(min);
		integerField.setMax(max);
		integerField.setWidthFull();
		integerField.setValue(current);
		integerField.setStepButtonsVisible(true);
		integerField.setAutofocus(false);
		integerField.setAutoselect(false);
		return integerField;
	}
	
	
	public void showThreads() {
		Dialog dialog = new Dialog();
		String title="Threads";
		dialog.setHeaderTitle(title);
		
		VerticalLayout dialogLayout = new VerticalLayout();
		dialogLayout.setSizeFull();
		
		//--------------------------------------------------------------------
		Grid<RunningProcess> gridRunningProcessKeys = new Grid<>(RunningProcess.class, false);
		gridRunningProcessKeys.addColumn(p -> p.getName()).setHeader("Name").setAutoWidth(true);
		gridRunningProcessKeys.addColumn(p -> p.getGroup()).setHeader("Group").setAutoWidth(true);
		gridRunningProcessKeys.addColumn(p -> dateFormat(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.getStartTs()), ZoneId.systemDefault()))).setHeader("Started").setAutoWidth(true);

		gridRunningProcessKeys.setWidthFull();
		gridRunningProcessKeys.getColumns().forEach(col->{col.setResizable(true);});
		gridRunningProcessKeys.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);

		gridRunningProcessKeys.setItems(RunnerSingleton.getInstance().getProcesses());
		
	
		//--------------------------------------------------------------------
		Grid<KeyValue> gridRunningThreadGroup = new Grid<>(KeyValue.class, false);
		gridRunningThreadGroup.addColumn(p -> p.getKey()).setHeader("Thread group").setAutoWidth(true);
		gridRunningThreadGroup.addColumn(p -> p.getValue()).setHeader("Active Thread Count").setAutoWidth(true);

		gridRunningThreadGroup.setWidthFull();
		gridRunningThreadGroup.getColumns().forEach(col->{col.setResizable(true);});
		gridRunningThreadGroup.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);

		gridRunningThreadGroup.setItems(RunnerSingleton.getInstance().getThreadGroupsAsKeyValue());

		//--------------------------------------------------------------------
		Grid<KeyValue> gridQueue = new Grid<>(KeyValue.class, false);
		gridQueue.addColumn(p -> p.getKey()).setHeader("Queue Instance Code").setAutoWidth(true);
		gridQueue.addColumn(p -> p.getValue()).setHeader("Status").setAutoWidth(true);

		gridQueue.setWidthFull();
		gridQueue.getColumns().forEach(col->{col.setResizable(true);});
		gridQueue.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);

		gridQueue.setItems(QueueSingleton.getInstance().getAllAsKeyValue());

		//-----------------------------------------------------------
		Button btRefresh = new Button("Refresh all", new Icon(VaadinIcon.REFRESH));
		btRefresh.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
		btRefresh.addClickListener(e -> {
			gridRunningProcessKeys.setItems(RunnerSingleton.getInstance().getProcesses());
			gridRunningThreadGroup.setItems(RunnerSingleton.getInstance().getThreadGroupsAsKeyValue());
			gridQueue.setItems(QueueSingleton.getInstance().getAllAsKeyValue());
		});
		
		gridQueue.setMaxWidth("30%");
		gridRunningProcessKeys.setMaxWidth("40%");
		gridRunningThreadGroup.setMaxWidth("30%");
		
		gridQueue.setHeightFull();
		gridRunningProcessKeys.setHeightFull();
		gridRunningThreadGroup.setHeightFull();
		

		
		dialogLayout.add(btRefresh);
		HorizontalLayout horizontalLayout = new HorizontalLayout(gridQueue, gridRunningProcessKeys,gridRunningThreadGroup);
		horizontalLayout.setSizeFull();
		
		dialogLayout.add(horizontalLayout);
		
		
		dialog.add(dialogLayout);
		Button cancelButton = new Button("Close", e -> dialog.close());
		dialog.getFooter().add(cancelButton);
		dialog.setWidth("90%");
		dialog.setHeight("80%");
		dialog.setResizable(true);
		dialog.setCloseOnEsc(true);
		dialog.setCloseOnOutsideClick(true);
		dialog.open();
		
	}
	
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


	public String dateFormat(LocalDateTime local) {
		if (local == null)
			return null;
		return local.format(formatter);
	}
	
	
	public Button makeTrueFalseIcon(boolean isSuccess) {
		Button btn = new Button("");
		btn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
		if (isSuccess) {
			btn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
			btn.setIcon(VaadinIcon.CHECK.create());
		}  else {
			btn.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btn.setIcon(VaadinIcon.MINUS.create());
		}
		
		return btn;
	}
	
	public TextField makeEditorTextField(String initialValue, Consumer<String> consumer, Predicate<String>...validators) {
		TextField tf=new TextField();
		tf.setValue(initialValue);
		tf.setWidthFull();
		tf.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		tf.setValueChangeMode(ValueChangeMode.LAZY);
		tf.addValueChangeListener(e->{
			String value=e.getValue();
			
			boolean isValid =validators ==null || List.of(validators).stream().allMatch(v->v.test(value));
			
			if (!isValid) {
				consumer.accept(value);
			} 
			else {
				e.getSource().setValue(initialValue);
				e.getSource().focus();
				notifyError("invalid entry");
			}
			
		});

		return tf;
	}

	

}
