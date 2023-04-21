package com.robodo.base;

import static io.restassured.RestAssured.given;

import java.sql.Blob;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.robodo.model.ApiResponse;
import com.robodo.model.KeyValue;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.model.ProcessInstanceStepFile;
import com.robodo.utils.HelperUtil;
import com.robodo.utils.RunnerUtil;
import com.robodo.utils.SeleniumUtil;

import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public abstract class BaseWebStep extends BaseStep {
	
	 protected SeleniumUtil selenium;
	 int fileOrder=1;
	 
	 public BaseWebStep(RunnerUtil runnerUtil, ProcessInstanceStep processInstanceStep) {
		super(runnerUtil, processInstanceStep);
		 this.selenium=new SeleniumUtil(runnerUtil);
		 this.fileOrder=1;

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
	 

	 
	 public ApiResponse httpRequest(Method method, String endPoint, List<KeyValue> headers, Object body)  {
		 RequestSpecification given = given();
		 given.contentType("application/json; charset=UTF-16");
		 
		 runnerUtil.logger("Request: %s %s".formatted(method.toString(),endPoint));

		 
		 if (headers!=null && !headers.isEmpty()) {
			 for (KeyValue header : headers) {
				 runnerUtil.logger("add header=> %s : %s".formatted(header.getKey(), header.getValue()));
				 given.header(header.getKey(), header.getValue());
			 } 
		 }
		
		 if (body!=null) {
			 runnerUtil.logger("-------------------------------------");
			 runnerUtil.logger("body");
			 runnerUtil.logger("-------------------------------------");
			 
			 if (body instanceof String) {
				 runnerUtil.logger((String) body);
				 given.body(body); 
			 } else {
				String bodyString = HelperUtil.obj2String(body);
				runnerUtil.logger(bodyString);

				 given.body(body);
			 }
			 
			 
		 }


		 
		 Response r = given.when().request(method, endPoint);
		 
		 var apiResponse = ApiResponse.create()
				 .withBody(r.body().asString())
				 .withResponseCode(r.getStatusCode());
		 
		 for (var header : r.headers()) {
			apiResponse = apiResponse.withHeaderEntity(header.getName(), header.getValue()); 
		 }

		 apiResponse.print(runnerUtil);
		 
		 return apiResponse;
 
	 }
	 
	 
	 


	 
	 public <T> T json2Object(String jsonInput, Class<T> clz) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				return mapper.readValue(jsonInput, clz);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}
	 
	 





}
