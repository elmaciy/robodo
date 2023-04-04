package com.robodo.ui;

import com.robodo.services.ProcessService;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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

}
