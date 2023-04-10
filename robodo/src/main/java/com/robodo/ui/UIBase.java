package com.robodo.ui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.services.ProcessService;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;


@SpringComponent
@UIScope
public class UIBase extends Div {
	private static final long serialVersionUID = 1L;

	ProcessService processService;
	
	public UIBase(ProcessService processService) {
		this.processService=processService;
	}
	
	
	public Component getComponentById(String id) {
		List<Component> allComponents=new ArrayList<Component>();
		getComponentsAll(UI.getCurrent(), allComponents);
		allComponents.stream().forEach(c->{
		});
		
		Optional<Component> optComponent = allComponents.stream().filter(c->c.getId().isPresent() && c.getId().get().equals(id)).findAny();
		if (optComponent.isPresent()) {
			return optComponent.get();
		}
		return null;
		
	}
	
	public Component getComponentById(Component parent, String id) {
		List<Component> allComponents=new ArrayList<Component>();
		getComponentsAll(parent, allComponents);
		Optional<Component> optComponent = allComponents.stream().filter(c->c.getId().isPresent() && c.getId().get().equals(id)).findAny();
		if (optComponent.isPresent()) {
			return optComponent.get();
		}
		return null;
		
	}
	
	public void getComponentsAll(Component parent, List<Component> allComponents) {
		List<Component> children = parent.getChildren().collect(Collectors.toList());
		System.err.println("found : "+children.size());
		allComponents.addAll(children);
		children.forEach(child->{
			getComponentsAll(child, allComponents);
		});
		
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
		RunnerUtil runnerUtil = new RunnerUtil(processService);
		byte[] imageBytes=HelperUtil.byteArr2Blob(file.getBinarycontent());
		StreamResource resource = new StreamResource("%s.png".formatted(String.valueOf(file.getId())), () -> new ByteArrayInputStream(imageBytes));
		Image image = new Image(resource, file.getDescription());

		add(image);
		image.setSizeFull();
		return image;
	}

}
