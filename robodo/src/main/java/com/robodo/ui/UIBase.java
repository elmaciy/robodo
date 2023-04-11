package com.robodo.ui;

import java.io.ByteArrayInputStream;

import org.springframework.security.core.userdetails.UserDetails;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.security.SecurityService;
import com.robodo.services.ProcessService;
import com.robodo.utils.HelperUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.SelectedChangeEvent;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;


@SpringComponent
@UIScope
@PageTitle("Robo.do")
public class UIBase extends AppLayout {
	private static final long serialVersionUID = 1L;

	ProcessService processService;
	SecurityService securityService;
	
	VerticalLayout root = new VerticalLayout();
	
	H1 titleH1 = new H1("Robo.do");
	
	public UIBase(ProcessService processService, SecurityService securityService) {
		this.processService=processService;
		this.securityService=securityService;
		
		root.removeAll();
		root.setWidth("100%");
		root.setHeight("100%");
		root.setSpacing(false);
		setContent(root);
		
		getElement().getStyle().set("height", "100%");
		getElement().getStyle().set("width", "100%");
		
		DrawerToggle toggle = new DrawerToggle();
		
		titleH1.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "var(--lumo-space-m)");

		if (isAuthenticated()) {
	        addToNavbar(toggle, titleH1);
	        addToDrawer(getTabs());
		}
        
        this.setPrimarySection(Section.NAVBAR);
	}


	public boolean isAuthenticated() {
		 UserDetails authenticatedUser = securityService.getAuthenticatedUser();
		 return authenticatedUser!=null;
	}


	public void setTitle(String title) {
		titleH1.setText("Robo.do %s".formatted(title));
	}
	
	private Tabs  getTabs() {
		Tabs tabs = new Tabs();
		
		Tab tabProcess=createTab(VaadinIcon.COG, "Proces");
		Tab tabParameter=createTab(VaadinIcon.COG, "Parameter");
		Tab tabUsers=createTab(VaadinIcon.COG, "User Management");
		Tab tabDashboard=createTab(VaadinIcon.COG, "Dashboard");
		Tab tabLogout=createTab(VaadinIcon.COG, "Logout");
		
        tabs.add(tabProcess);
        tabs.add(tabParameter);
        tabs.add(tabUsers);
        tabs.add(tabDashboard);
        tabs.add(tabLogout);
        
        ComponentEventListener<SelectedChangeEvent> selectionChangeListener=(e)->{
        	Tab selectedTab = e.getSelectedTab();
        	
        	if(selectedTab.equals(tabProcess)) {
        		UI.getCurrent().navigate(UIProcessor.class);
        	}
        	else if (selectedTab.equals(tabParameter)) {
        		UI.getCurrent().navigate(UIParameters.class);
        	} 
        	else if (selectedTab.equals(tabUsers)) {
        		UI.getCurrent().navigate(UIUsers.class);
        	} 
        	else if (selectedTab.equals(tabDashboard)) {
        		UI.getCurrent().navigate(UIDashboard.class);
        	} else if(selectedTab.equals(tabLogout)) {
        		confirmAndRun("Logout", "Sure to logout", ()->securityService.logout());
        	}
        };
		tabs.addSelectedChangeListener(selectionChangeListener);

        
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
	}
	
	private Tab createTab(VaadinIcon viewIcon, String viewName) {
        Icon icon = viewIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("padding", "var(--lumo-space-xs)");

        RouterLink link = new RouterLink();
        link.add(icon, new Span(viewName));
        // Demo has no routes
        // link.setRoute(viewClass.java);
        link.setTabIndex(-1);

        return new Tab(link);
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
        notification.setPosition(Notification.Position.TOP_END);
	}
	
	public void notifyError(String content) {
		Notification notification = Notification.show(content);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_END);		
	}
	
	public void notifyInfo(String content) {
		Notification notification = Notification.show(content);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notification.setPosition(Notification.Position.TOP_END);		
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
		return integerField;
	}
	
	

}
