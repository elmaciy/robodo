package com.robodo.base;

import java.sql.Blob;

import javax.sql.rowset.serial.SerialBlob;

import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;
import com.robodo.utils.SeleniumUtil;

public abstract class BaseWebStep extends BaseStep {
	
	 protected RunnerUtil runnerUtil;
	 protected ProcessInstanceStep processInstanceStep;
	 protected SeleniumUtil selenium;
	 int fileOrder;
	 
	 public BaseWebStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
	 }
	 
	 
	 public void takeStepScreenShot(ProcessInstanceStep processInstanceStep, String description, boolean toAttach) {
		 takeStepScreenShot(processInstanceStep, description, toAttach, null);
	 }
	 
	 
	 public void takeStepScreenShot(ProcessInstanceStep processInstanceStep, String description, boolean toAttach, Runnable actionBefore) {
		 
		 if (!HelperUtil.isValidForFileName(description)) {
			 throw new RuntimeException("the filename is not valid for filename convension : %s".formatted(description));
		 }
		 
		 if (actionBefore!=null) {
			 actionBefore.run();
		 }
		 byte[] screenShotAsByteArray = null;
		 ProcessInstanceStepFile file = null;
		 try {
			 screenShotAsByteArray = selenium.screenShotAsByteArray(processInstanceStep.getProcessInstance());
			 file=new ProcessInstanceStepFile();
			 file.setFileOrder(fileOrder++);
			 file.setMimeType(ProcessInstanceStepFile.MIME_TYPE_SCREENSHOT);
		 } catch(Exception e) {
			 e.printStackTrace();
			 throw new RuntimeException("Exception takeStepScreenShot in converting screenshot content : %s".formatted(e.getMessage()));
		 }
		
		 Blob blobContent=null;
		 try {
			 blobContent= new SerialBlob(screenShotAsByteArray);
		 } catch(Exception e) {
			 e.printStackTrace();
			 throw new RuntimeException("Exception takeStepScreenShot in converting screenshot content : %s".formatted(e.getMessage()));
		 }
		 file.setBinarycontent(blobContent);
		 file.setFileType(ProcessInstanceStepFile.TYPE_SS);
		 file.setDescription(description);
		 file.setProcessInstanceStepId(processInstanceStep.getId());
		 file.setAttach(toAttach);
		 
		 
		 runnerUtil.processService.saveProcessInstanceStepFile(file);
	 }



}
