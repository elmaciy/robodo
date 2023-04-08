package com.robodo.utils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;

import com.robodo.discoverer.BaseDiscoverer;
import com.robodo.model.EmailTemplate;
import com.robodo.model.ExecutionResultsForCommand;
import com.robodo.model.ExecutionResultsForInstance;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.steps.BaseSteps;

public class RunnerUtil {

	ProcessService processService;
	WebElement locatedWebElement;
	String valueExtracted;
	HashMap<String, String> hmExtractedValues = new HashMap<>();
	StringBuilder logs = new StringBuilder();

	public RunnerUtil(ProcessService processService) {
		this.processService = processService;
	}

	public ExecutionResultsForInstance runProcessInstance(ProcessInstance processInstance) {
		
		ExecutionResultsForInstance result=new ExecutionResultsForInstance(processInstance);

		RunnerSingleton.getInstance().start(processInstance.getCode());
				
		hmExtractedValues = HelperUtil.String2HashMap(result.getProcessInstance().getInstanceVariables());
		

		List<ProcessInstanceStep> steps = result.getProcessInstance().getSteps();

		
		for (ProcessInstanceStep step : steps) {
			if (step.getStatus().equals(ProcessInstanceStep.STATUS_COMPLETED)) {
				logger("skipping already completed step %s".formatted(step.getStepCode()));
				continue;
			}
			logs.setLength(0);
			
			//add existing logs if any
			if (step.getLogs() != null) {
				logs.append(step.getLogs());
			}
			processService.saveProcessInstance(result.getProcessInstance());

			
			String stepRunningKey="%s.%s.%s".formatted(processInstance.getProcessDefinition().getCode(),step.getStepCode(), String.valueOf(step.getId()));
			
			try {
				
				if (processInstance.getProcessDefinition().isSingletonStep(step)) {
					String similarRunningKey="%s.%s.".formatted(processInstance.getProcessDefinition().getCode(),step.getStepCode());
					boolean hasSimilarInstance=RunnerSingleton.getInstance().hasSimilarRunningInstance(similarRunningKey);

					if (hasSimilarInstance) {
						result.setMessage("");
						result.setStatus(ExecutionResultsForInstance.STATUS_STALLED);
						RunnerSingleton.getInstance().stop(processInstance.getCode());
						return result;
					}
					
				}
				

				result.getProcessInstance().setStatus(ProcessInstance.STATUS_RUNNING);
				result.getProcessInstance().setCurrentStepCode(step.getStepCode());
				
				RunnerSingleton.getInstance().start(stepRunningKey);
				
				runStep(result.getProcessInstance(), step);
				
				if (step.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING)) {
					logger("stopped at step [%s] at command [%s]".formatted(step.getStepCode(), step.getCommands()));
					step.setLogs(logs.toString());
					break;
				}
				
				if (step.getStatus().equals(ProcessInstanceStep.STATUS_FAILED)) {
					logger("failed at step [%s] at command [%s]".formatted(step.getStepCode(), step.getCommands()));
					step.setLogs(logs.toString());
					step.setFinished(LocalDateTime.now());
					break;
				}
				
				result.setStatus(ExecutionResultsForInstance.STATUS_SUCCESS);
			} catch (Exception e) {
				String message = e.getMessage();
				logger("exception at step [%s] at command [%s] : %s".formatted(step.getStepCode(), step.getCommands(),
						message));
				step.setLogs(logs.toString());
				step.setStatus(ProcessInstanceStep.STATUS_FAILED);
				step.setError(e.getMessage());
				step.setFinished(LocalDateTime.now());
				
				result.setMessage(e.getMessage());
				result.setStatus(ExecutionResultsForInstance.STATUS_FAILED);
				
				RunnerSingleton.getInstance().stop(stepRunningKey);
				
				break;
			} 

			RunnerSingleton.getInstance().stop(stepRunningKey);
			
			step.setLogs(logs.toString());
			result.getProcessInstance().setInstanceVariables(HelperUtil.hashMap2String(hmExtractedValues));
			processService.saveProcessInstance(result.getProcessInstance());

		} //for
		
		boolean allStepsCompleted = result.getProcessInstance().getSteps().stream()
				.allMatch(
						p -> 
							p.getStatus().equals(ProcessInstanceStep.STATUS_NEW) 
							||
							p.getStatus().equals(ProcessInstanceStep.STATUS_COMPLETED) 
							|| 
							p.getStatus().equals(ProcessInstanceStep.STATUS_FAILED)
						);

		result.getProcessInstance().setStatus(allStepsCompleted ? ProcessInstance.STATUS_COMPLETED : ProcessInstance.STATUS_RUNNING);
		
		if (allStepsCompleted) {
			result.getProcessInstance().setFinished(LocalDateTime.now());
			result.getProcessInstance().setAttemptNo(processInstance.getAttemptNo()+1);
		} 
		
		result.getProcessInstance().setInstanceVariables(HelperUtil.hashMap2String(hmExtractedValues));

		processService.saveProcessInstance(result.getProcessInstance());
		
		RunnerSingleton.getInstance().stop(result.getProcessInstance().getCode());
		
		return result;

	}

	private ExecutionResultsForCommand runCommand(ProcessInstanceStep step, String command) {
		
		ExecutionResultsForCommand result=new ExecutionResultsForCommand();

		String arg0 = getDo(command);
		String arg1 = getArg(command);

		logger("%s (%s)".formatted(arg0, arg1));

		if (arg0.equalsIgnoreCase("runStepClass")) {
			return runStepClass(step, arg1);
		} else if (arg0.equalsIgnoreCase("sleep")) {
			LocalDateTime started = step.getStarted();
			LocalDateTime now = LocalDateTime.now();
			var secondToWait = Long.valueOf(arg1);
			LocalDateTime tobeFinishedAt = started.plusSeconds(secondToWait);

			boolean isOk = now.isAfter(tobeFinishedAt);
			if (!isOk) {
				return result.skipped().withMessage("Process will be waited till %s".formatted(tobeFinishedAt));
			}

			return result.succeeded();
		} else if (arg0.equalsIgnoreCase("waitHumanInteraction")) {
			if (!step.isNotificationSent()) {
				sendEmailNotificationForApproval(step, arg1);
				step.setNotificationSent(true);
			}
			boolean isApproved = step.isApproved();
			if (isApproved) {
				logger("approved by : %s at [%s]".formatted(step.getApprovedBy(), step.getApprovalDate()));
				return result.succeeded();
			} else {
				return result.skipped().withMessage("Step is not approved yet!");
			}
			
		}

		return result;
	}

	private void sendEmailNotificationForApproval(ProcessInstanceStep step, String emailTemplateCode) {
		EmailTemplate emailTemplate = processService.getEmailTemplateByCode(emailTemplateCode);
		if (emailTemplate==null) {
			throw new RuntimeException("email template %s not found");
		}
		boolean isMailSend = HelperUtil.sendEmailByTemplate(emailTemplate, step, this);
		if (!isMailSend) {
			throw new RuntimeException("email not sent");
		}
	}

	public String getTargetPath(ProcessInstance processInstance) {
		String workingDir = processService.getEnv().getProperty("working.dir");
		String executionDir = workingDir + File.separator + "executions";
		int year = processInstance.getCreated().getYear();
		int month = processInstance.getCreated().getMonthValue();
		int day = processInstance.getCreated().getDayOfMonth();
		String subDir = "%d%s%02d%s%02d%s%s".formatted(year, File.separator, month, File.separator, day, File.separator, processInstance.getCode());
		String targetDir = executionDir + File.separator + subDir;
		return targetDir;
	}

	private ProcessInstanceStep runStep(ProcessInstance processInstance, ProcessInstanceStep step) {
		if (step.getStatus().equals(ProcessInstanceStep.STATUS_NEW)) {
			step.setStatus(ProcessInstanceStep.STATUS_RUNNING);
			step.setStarted(LocalDateTime.now());
			processService.saveProcessInstance(processInstance);
		}

		String normalizedCommand = normalize(step.getCommands());
		
		ExecutionResultsForCommand result =  runCommand(step, normalizedCommand);
		
		if (result.getStatus().equals(ExecutionResultsForCommand.STATUS_FAILED)) {
			logger("Command [%s] execution is failed for step [%s] => %s".formatted(step.getCommands(),step.getStepCode(), result.getMessage()));
			step.setError(result.getMessage());
			processInstance.setError(result.getMessage());
			processInstance.setFailed(true);
			step.setStatus(ProcessInstanceStep.STATUS_FAILED);
		} else {
			boolean isOk = result.getStatus().equals(ExecutionResultsForCommand.STATUS_SUCCESS);
			step.setStatus(isOk ? ProcessInstanceStep.STATUS_COMPLETED : ProcessInstanceStep.STATUS_RUNNING);
			step.setError("");
			processInstance.setError("");
			step.setFinished(isOk ? LocalDateTime.now() : null);
		}
		
		
		return step;

	}
	
	


	private String normalize(String command) {
		if (command == null || command.strip().length() == 0) {
			return "";
		}

		return command.strip();
	}

	String getDo(String command) {
		String[] args = command.split(" ");
		if (args.length < 2) {
			return command.strip();
		}

		return StringUtils.substringBefore(command, " ");
	}

	String getArg(String command) {
		String[] args = command.split(" ");
		if (args.length < 2) {
			return "";
		}

		return StringUtils.substringAfter(command, " ").strip();
	}

	private ExecutionResultsForCommand runStepClass(ProcessInstanceStep step,  String className) {
		ExecutionResultsForCommand result = new ExecutionResultsForCommand();
		BaseSteps stepClassInstance=null;
		try {
			String packageName = processService.getEnv().getProperty("steps.package");
			Class<?> clazz = Class.forName(packageName + "." + className);
			java.lang.reflect.Constructor<?> constructor = clazz.getConstructor(RunnerUtil.class, ProcessInstanceStep.class);
		
			stepClassInstance = (BaseSteps) constructor.newInstance(this, step);
			if (stepClassInstance!=null) {
				stepClassInstance.setup();
			}
			
			step.getFiles().clear();
			stepClassInstance.run();
			result = result.succeeded();
		} catch (Exception e) {
			e.printStackTrace();
			result = result.failed().withMessage(e.getMessage());
		} finally {
			if (stepClassInstance!=null) {
				stepClassInstance.teardown();
			}
		}
		
		
		return result;

	}

	public void logger(String logStr) {
		String str = "%s - %s".formatted(new Date().toString(), logStr);
		System.err.println(str);
		logs.append(str + "\n");

	}

	public List<ProcessInstance> runProcessDiscovery(ProcessDefinition processDefinition) {
		try {
			String packageName = processService.getEnv().getProperty("discovery.package");
			Class<?> clazz = Class.forName(packageName + "." + processDefinition.getDiscovererClass());
			java.lang.reflect.Constructor<?> constructor = clazz.getConstructor(RunnerUtil.class);
			BaseDiscoverer discovererInstance = (BaseDiscoverer) constructor.newInstance(this);
			return discovererInstance.discover(processDefinition);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ProcessInstance>();
		}

	}

	public String getEnvironmentParameter(String key) {
		return this.processService.getEnv().getProperty(key);
	}

	public void setVariable(String key, String value) {
		hmExtractedValues.put(key, value);
	}
	
	public String getVariable(String key) {
		return hmExtractedValues.get(key);
	}

}
