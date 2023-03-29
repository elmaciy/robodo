package com.robodo.runner;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robodo.discoverer.BaseDiscoverer;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.singleton.RunnerSingleton;
import com.robodo.steps.BaseSteps;

public class RunnerUtil {

	WebElement locatedWebElement;
	Environment env;
	String valueExtracted;
	HashMap<String, String> hmExtractedValues = new HashMap<>();
	ProcessInstance processInstance;
	ProcessDefinition processDefinition;
	String runningCommand;
	StringBuilder logs = new StringBuilder();

	public RunnerUtil(Environment env) {
		this.env = env;
	}

	public ProcessInstance runProcessInstance(ProcessInstance processInstance) {

		this.processDefinition = processInstance.getProcessDefinition();
		this.processInstance = processInstance;

		boolean eligibleToRun = isEligibleToRunProcessDefinition(processDefinition);
		if (!eligibleToRun) {
			logger("not eligible for run");
			return null;
		}

		RunnerSingleton.getInstance().start(processDefinition.getCode());

		processInstance.setStatus(ProcessInstance.STATUS_RUNNING);
		

		List<ProcessInstanceStep> steps = processInstance.getSteps();

		Collections.sort(steps, new Comparator<ProcessInstanceStep>() {

			@Override
			public int compare(ProcessInstanceStep o1, ProcessInstanceStep o2) {
				return o1.getOrderNo().compareTo(o2.getOrderNo());
			}

		});

		for (ProcessInstanceStep step : processInstance.getSteps()) {
			if (step.getStatus().equals(ProcessInstanceStep.STATUS_COMPLETED)) {
				logger("skipping already completed step %s".formatted(step.getStepCode()));
				continue;
			}
			logs.setLength(0);
			if (step.getLogs() != null) {
				logs.append(step.getLogs());
			}
			processInstance.setCurrentStepCode(step.getStepCode());
			try {
				runStep(step);
				if (step.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING)) {
					logger("stopped at step [%s] at command [%s]".formatted(step.getStepCode(), runningCommand));
					step.setLogs(logs.toString());
					break;
				}
			} catch (Exception e) {
				String message = e.getMessage();
				logger("exception at step [%s] at command [%s] : %s".formatted(step.getStepCode(), runningCommand,
						message));
				step.setLogs(logs.toString());
				break;
			}

			step.setLogs(logs.toString());

		}

		hmExtractedValues.put("processInstance.id", processInstance.getId().toString());
		hmExtractedValues.put("processInstance.code", processInstance.getCode());
		hmExtractedValues.put("processInstance.currentStepCode", processInstance.getCurrentStepCode());

		printHmExtractedValues();
		persistHmapValues();

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

		RunnerSingleton.getInstance().stop(processDefinition.getCode());
		return this.processInstance;

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

	private boolean runCommand(ProcessInstanceStep step, String command) {

		String arg0 = getDo(command);
		String arg1 = getArg(command);

		logger("%s (%s)".formatted(arg0, arg1));

		if (arg0.equalsIgnoreCase("runStepClass")) {
			try {
				runStepClass(arg1);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else if (arg0.equalsIgnoreCase("sleep")) {
			LocalDateTime started = step.getStarted();
			LocalDateTime now = LocalDateTime.now();
			var secondToWait = Long.valueOf(arg1);
			LocalDateTime tobeFinishedAt = started.plusSeconds(secondToWait);

			boolean isOk = now.isAfter(tobeFinishedAt);
			if (!isOk) {
				logger("Process will be waited till %s".formatted(tobeFinishedAt));
			}

			return isOk;
		} else if (arg0.equalsIgnoreCase("waitHumanInteraction")) {
			boolean isApproved = step.isApproved();
			if (isApproved) {
				logger("approved by : %s at [%s]".formatted(step.getApprovedBy(), step.getApprovalDate()));
			} else {
				logger("not approved yet.");
			}
			return isApproved;
		}

		return false;
	}

	String getTargetPath() {
		String workingDir = env.getProperty("working.dir");
		String executionDir = workingDir + File.separator + "executions";
		int year = processInstance.getCreated().getYear();
		int month = processInstance.getCreated().getMonthValue();
		int day = processInstance.getCreated().getDayOfMonth();
		String subDir = "%d%s%02d%s%02d%s%s%s%s".formatted(year, File.separator, month, File.separator, day,
				File.separator, processInstance.getCode(), File.separator, processInstance.getCurrentStepCode());
		String targetDir = executionDir + File.separator + subDir;
		return targetDir;
	}

	private ProcessInstanceStep runStep(ProcessInstanceStep step) {
		if (step.getStatus().equals(ProcessInstanceStep.STATUS_NEW)) {

			step.setStatus(ProcessInstanceStep.STATUS_RUNNING);
			step.setStarted(LocalDateTime.now());
		}

		String normalizedCommand = normalize(step.getCommands());
		this.runningCommand = normalizedCommand;
		boolean isOk = runCommand(step, normalizedCommand);
		step.setStatus(isOk ? ProcessInstanceStep.STATUS_COMPLETED : ProcessInstanceStep.STATUS_RUNNING);
		step.setFinished(LocalDateTime.now());
		return step;

	}

	private void printHmExtractedValues() {
		if (hmExtractedValues.isEmpty()) {
			logger("no hash value collected so far");
			return;
		}

		logger("----------------------------------");
		logger("---- HASH VALUES COLLECTED   -----");
		logger("----------------------------------");
		hmExtractedValues.keySet().iterator().forEachRemaining(k -> {
			String v = hmExtractedValues.get(k);
			logger("%s \t= \t[%s]".formatted(k, v));
		});
		logger("----------------------------------");

	}

	private void persistHmapValues() {
		if (hmExtractedValues.isEmpty()) {
			return;
		}

		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(hmExtractedValues);
			this.processInstance.setInstanceVariables(json);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * ObjectMapper mapper = new ObjectMapper(); try { String json =
		 * mapper.writeValueAsString(hmExtractedValues); String
		 * fileToWrite=getTargetPath()+File.separator+"hashValues.json";
		 * FileUtil.writeAsString(new File(fileToWrite), json); } catch
		 * (JsonProcessingException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
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

	private void runStepClass(String className) {
		try {
			String packageName = env.getProperty("steps.package");
			Class<?> clazz = Class.forName(packageName + "." + className);
			java.lang.reflect.Constructor<?> constructor = clazz.getConstructor(RunnerUtil.class);
			BaseSteps stepClassInstance = (BaseSteps) constructor.newInstance(this);
			stepClassInstance.run();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("exception : %s".formatted(e.getMessage()));
		}

	}

	public void logger(String logStr) {
		String str = "%s - %s".formatted(new Date().toString(), logStr);
		System.err.println(str);
		logs.append(str + "\n");

	}

	public List<ProcessInstance> runProcessDiscovery(ProcessDefinition processDefinition) {
		try {
			String packageName = env.getProperty("discovery.package");
			Class<?> clazz = Class.forName(packageName + "." + processDefinition.getDiscovererClass());
			java.lang.reflect.Constructor<?> constructor = clazz.getConstructor(RunnerUtil.class);
			BaseDiscoverer discovererInstance = (BaseDiscoverer) constructor.newInstance(this);
			return discovererInstance.discover(processDefinition);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ProcessInstance>();
		}

	}

}
