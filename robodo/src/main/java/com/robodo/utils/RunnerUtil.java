package com.robodo.utils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robodo.discoverer.BaseDiscoverer;
import com.robodo.model.ExecutionResultsForCommand;
import com.robodo.model.ExecutionResultsForInstance;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.singleton.SingletonForUIUpdate;
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

		boolean eligibleToRun = isEligibleToRunProcessDefinition(processInstance.getProcessDefinition());
		if (!eligibleToRun) {
			String msg="not eligible for runing";
			logger(msg);
			result.setMessage(msg);
			result.setStatus(ExecutionResultsForInstance.STATUS_NOT_ELIGIBLE);
			return result;
		}

		RunnerSingleton.getInstance().start(processInstance.getProcessDefinition().getCode());

		result.getProcessInstance().setRetryNo(processInstance.getRetryNo()+1);
		result.getProcessInstance().setStatus(ProcessInstance.STATUS_RUNNING);
		processService.saveProcessInstance(result.getProcessInstance());
		
		hmExtractedValues = String2HashMap(result.getProcessInstance().getInstanceVariables());
		

		List<ProcessInstanceStep> steps = result.getProcessInstance().getSteps();

		Collections.sort(steps, new Comparator<ProcessInstanceStep>() {

			@Override
			public int compare(ProcessInstanceStep o1, ProcessInstanceStep o2) {
				return o1.getOrderNo().compareTo(o2.getOrderNo());
			}

		});

		for (ProcessInstanceStep step : result.getProcessInstance().getSteps()) {
			if (step.getStatus().equals(ProcessInstanceStep.STATUS_COMPLETED)) {
				logger("skipping already completed step %s".formatted(step.getStepCode()));
				continue;
			}
			logs.setLength(0);
			//add existing logs if any
			if (step.getLogs() != null) {
				logs.append(step.getLogs());
			}
			result.getProcessInstance().setCurrentStepCode(step.getStepCode());
			processService.saveProcessInstance(result.getProcessInstance());
			
			try {
				runStep(result.getProcessInstance(), step);
				if (step.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING)) {
					logger("stopped at step [%s] at command [%s]".formatted(step.getStepCode(), step.getCommands()));
					step.setLogs(logs.toString());
					break;
				}
				result.setStatus(ExecutionResultsForInstance.STATUS_SUCCESS);
			} catch (Exception e) {
				String message = e.getMessage();
				logger("exception at step [%s] at command [%s] : %s".formatted(step.getStepCode(), step.getCommands(),
						message));
				step.setLogs(logs.toString());
				break;
			}

			step.setLogs(logs.toString());
			processService.saveProcessInstance(result.getProcessInstance());

		} //for

		
		

		boolean allStepsCompleted = processInstance.getSteps().stream()
				.allMatch(p -> p.getStatus().equals(ProcessInstanceStep.STATUS_COMPLETED));

		processInstance.setStatus(allStepsCompleted ? ProcessInstance.END : ProcessInstance.STATUS_RUNNING);
		if (allStepsCompleted) {
			processInstance.setFinished(LocalDateTime.now());
		} else {
			boolean isInHumanIntegration = processInstance.getSteps().stream()
					.filter(p -> !p.getStatus().equals(ProcessInstanceStep.STATUS_COMPLETED)).findFirst().get()
					.getCommands().contains("waitHumanInteraction");
			if (isInHumanIntegration) {
				processInstance.setCurrentStepCode(ProcessInstance.STATUS_HUMAN);

			}
		}
		
		hmExtractedValues.put("processInstance.currentStepCode", processInstance.getCurrentStepCode());
		result.getProcessInstance().setInstanceVariables(hashMap2String(hmExtractedValues));

		processService.saveProcessInstance(result.getProcessInstance());
		
		RunnerSingleton.getInstance().stop(result.getProcessInstance().getProcessDefinition().getCode());
		return result;

	}

	public boolean isEligibleToRunProcessDefinition(ProcessDefinition processDefinition) {
		boolean isSingleton = processDefinition.isSingleAtATime();
		if (isSingleton) {
			boolean isRunning = RunnerSingleton.getInstance().hasRunningInstance(processDefinition.getCode());
			if (isRunning) {
				logger("singleton process [%s) is already running".formatted(processDefinition.getCode()));
				return false;
			}
		}

		return true;
	}

	private ExecutionResultsForCommand runCommand(ProcessInstanceStep step, String command) {
		
		ExecutionResultsForCommand result=new ExecutionResultsForCommand();

		String arg0 = getDo(command);
		String arg1 = getArg(command);

		logger("%s (%s)".formatted(arg0, arg1));

		if (arg0.equalsIgnoreCase("runStepClass")) {
			return runStepClass(arg1);
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

	String getTargetPath(ProcessInstance processInstance) {
		String workingDir = processService.getEnv().getProperty("working.dir");
		String executionDir = workingDir + File.separator + "executions";
		int year = processInstance.getCreated().getYear();
		int month = processInstance.getCreated().getMonthValue();
		int day = processInstance.getCreated().getDayOfMonth();
		String subDir = "%d%s%02d%s%02d%s%s%s%s".formatted(year, File.separator, month, File.separator, day,
				File.separator, processInstance.getCode(), File.separator, processInstance.getCurrentStepCode());
		String targetDir = executionDir + File.separator + subDir;
		return targetDir;
	}

	private ProcessInstanceStep runStep(ProcessInstance instance, ProcessInstanceStep step) {
		if (step.getStatus().equals(ProcessInstanceStep.STATUS_NEW)) {
			step.setStatus(ProcessInstanceStep.STATUS_RUNNING);
			step.setStarted(LocalDateTime.now());
		}

		String normalizedCommand = normalize(step.getCommands());
		
		ExecutionResultsForCommand result =  runCommand(step, normalizedCommand);
		
		if (result.getStatus().equals(ExecutionResultsForCommand.STATUS_FAILED)) {
			logger("Command [%s] execution is failed for step [%s] => %s".formatted(step.getCommands(),step.getStepCode(), result.getMessage()));
			step.setError(result.getMessage());
			instance.setError(result.getMessage());
			step.setStatus(ProcessInstanceStep.STATUS_FAILED);
		} else {
			boolean isOk = result.getStatus().equals(ExecutionResultsForCommand.STATUS_SUCCESS);
			step.setStatus(isOk ? ProcessInstanceStep.STATUS_COMPLETED : ProcessInstanceStep.STATUS_RUNNING);
			step.setError("");
			instance.setError("");
			step.setFinished(LocalDateTime.now());
		}
		
		
		return step;

	}
	
	public static String hashMap2String(HashMap<String,String> hm) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(hm);
			return json;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static HashMap<String,String>  String2HashMap(String data) {
		
		HashMap<String,String> hm= new HashMap<String,String>();
		try {
			JSONParser parser = new JSONParser(data);
			LinkedHashMap<String, Object> linkedList = parser.parseObject();
			linkedList.keySet().stream().forEach(key->{
				hm.put(key, (String) linkedList.get(key));
			});
			return hm;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<String,String>();
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

	private ExecutionResultsForCommand runStepClass(String className) {
		ExecutionResultsForCommand result = new ExecutionResultsForCommand();
		try {
			String packageName = processService.getEnv().getProperty("steps.package");
			Class<?> clazz = Class.forName(packageName + "." + className);
			java.lang.reflect.Constructor<?> constructor = clazz.getConstructor(RunnerUtil.class);
			BaseSteps stepClassInstance = (BaseSteps) constructor.newInstance(this);
			stepClassInstance.run();
			return result.succeeded();
		} catch (Exception e) {
			e.printStackTrace();
			return result.failed().withMessage(e.getMessage());
		}

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
			SingletonForUIUpdate.getInstance().setLastUpdate();
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
