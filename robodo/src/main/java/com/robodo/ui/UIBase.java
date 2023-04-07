package com.robodo.ui;

import java.io.ByteArrayInputStream;
import java.io.File;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.services.ProcessService;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;
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
		String imageFileName=file.getFileName();
		RunnerUtil runnerUtil = new RunnerUtil(processService);
		String targetDir=runnerUtil.getTargetPath(step.getProcessInstance());
		
		String imagePath=targetDir+File.separator+imageFileName;
		byte[] imageBytes=HelperUtil.getFileAsByteArray(imagePath);
		runnerUtil.logger("image file [%s] loaded, (%s) bytes".formatted(imagePath,String.valueOf(imageBytes.length)));
		StreamResource resource = new StreamResource(imageFileName, () -> new ByteArrayInputStream(imageBytes));
		Image image = new Image(resource, file.getDescription());

		add(image);
		image.setSizeFull();
		return image;
	}

}
