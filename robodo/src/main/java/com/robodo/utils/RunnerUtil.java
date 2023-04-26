package com.robodo.utils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;

import com.robodo.base.BaseStep;
import com.robodo.model.Discoverable;
import com.robodo.model.EmailTemplate;
import com.robodo.model.ExecutionResultsForCommand;
import com.robodo.model.ProcessDefinition;
import com.robodo.model.ProcessDefinitionStep;
import com.robodo.model.ProcessInstance;
import com.robodo.model.ProcessInstanceStep;
import com.robodo.services.ProcessService;
import com.robodo.singleton.RunnerSingleton;

public class RunnerUtil {

	public ProcessService processService;
	WebElement locatedWebElement;
	String valueExtracted;
	HashMap<String, String> hmValues = new HashMap<>();
	StringBuilder logs = new StringBuilder();

	public RunnerUtil(ProcessService processService) {
		this.processService = processService;
	}

	public void runProcessInstance(ProcessInstance processInstance) {
		ProcessDefinition processDefinition = processService
				.getProcessDefinitionById(processInstance.getProcessDefinitionId());

		RunnerSingleton.getInstance().start(processInstance.getCode(), processDefinition.getCode());

		hmValues = HelperUtil.str2HashMap(processInstance.getInstanceVariables());

		if (processInstance.isInitialStatus()) {
			HashMap<String, String> hmForProcessDefinition = HelperUtil
					.str2HashMap(processDefinition.getInitialInstanceVariables());
			hmForProcessDefinition.entrySet().forEach(kv -> {
				hmValues.put(kv.getKey(), kv.getValue());
			});
			
			if (processInstance.toBeRetried()) {
				if (processDefinition.getRetryStep()!=null && !processDefinition.getRetryStep().strip().isEmpty()) {
					logger("Running retry class %s".formatted(processDefinition.getRetryStep()));
					runStepClass(processInstance.getFirstStep(), processDefinition.getRetryStep());
				}
			}
		}

		List<ProcessInstanceStep> steps = processInstance.getSteps();

		for (ProcessInstanceStep step : steps) {
			if (step.getStatus().equals(ProcessInstanceStep.STATUS_COMPLETED)) {
				// logger("skipping already completed step %s".formatted(step.getStepCode()));
				continue;
			}
			logs.setLength(0);

			// add existing logs if any
			if (step.getLogs() != null) {
				logs.append(step.getLogs());
			}

			ProcessDefinitionStep stepDef = processDefinition.getSteps().stream()
					.filter(p -> p.getOrderNo().equals(step.getOrderNo())).findAny().get();
			String stepRunningKey = "$STEP_%s".formatted(stepDef.getCode());
			try {

				if (stepDef.isSingleAtATime()) {
					boolean hasRunningInstance = RunnerSingleton.getInstance().hasRunningInstance(stepRunningKey);

					if (hasRunningInstance) {
						RunnerSingleton.getInstance().stop(processInstance.getCode(), processDefinition.getCode());
						return;
					}

				}

				RunnerSingleton.getInstance().start(stepRunningKey);
				processInstance.setStatus(ProcessInstance.STATUS_RUNNING);
				processService.saveProcessInstance(processInstance);

				// ----------------------------------
				runStep(processInstance, step);
				// ---------------------------------

				if (step.getStatus().equals(ProcessInstanceStep.STATUS_RUNNING)) {
					logger("stopped at step [%s] at command [%s]".formatted(step.getStepCode(), step.getCommands()));
					step.setLogs(logs.toString());
					step.setInstanceVariables(HelperUtil.hashMap2String(hmValues));
					RunnerSingleton.getInstance().stop(stepRunningKey);
					break;
				}

				if (step.getStatus().equals(ProcessInstanceStep.STATUS_FAILED)) {
					logger("failed at step [%s] at command [%s]".formatted(step.getStepCode(), step.getCommands()));
					step.setLogs(logs.toString());
					step.setFinished(LocalDateTime.now());
					step.setInstanceVariables(null);
					RunnerSingleton.getInstance().stop(stepRunningKey);
					break;
				}

				step.setInstanceVariables(HelperUtil.hashMap2String(hmValues));

			} catch (Exception e) {
				String message = e.getMessage();
				logger("exception at step [%s] at command [%s] : %s".formatted(step.getStepCode(), step.getCommands(),
						message));

				step.setLogs(logs.toString());
				step.setStatus(ProcessInstanceStep.STATUS_FAILED);
				step.setError(e.getMessage());
				step.setFinished(LocalDateTime.now());
				step.setInstanceVariables(null);
				RunnerSingleton.getInstance().stop(stepRunningKey);

				break;
			}

			RunnerSingleton.getInstance().stop(stepRunningKey);
			step.setLogs(logs.toString());

			processInstance.setInstanceVariables(HelperUtil.hashMap2String(hmValues));
			processService.saveProcessInstance(processInstance);

		} // for

		boolean allStepsCompleted = processInstance.getSteps().stream()
				.allMatch(p -> p.getStatus().equals(ProcessInstanceStep.STATUS_NEW)
						|| p.getStatus().equals(ProcessInstanceStep.STATUS_COMPLETED)
						|| p.getStatus().equals(ProcessInstanceStep.STATUS_FAILED));

		processInstance
				.setStatus(allStepsCompleted ? ProcessInstance.STATUS_COMPLETED : ProcessInstance.STATUS_RUNNING);

		if (allStepsCompleted) {
			processInstance.setFinished(LocalDateTime.now());
			processInstance.setAttemptNo(processInstance.getAttemptNo() + 1);

			ProcessInstanceStep latestProcessedStep = processInstance.getLatestProcessedStep();

			boolean isLastStepFailed = latestProcessedStep != null
					&& latestProcessedStep.getStatus().equals(ProcessInstanceStep.STATUS_FAILED);
			
			
			processInstance.setFailed(isLastStepFailed);
			processInstance.setError(isLastStepFailed ? latestProcessedStep.getError() : null);
		}

		processInstance.setInstanceVariables(HelperUtil.hashMap2String(hmValues));
		
		if (processInstance.isFailed()) {
			if (processDefinition.getFailStep()!=null && !processDefinition.getFailStep().strip().isEmpty()) {
				logger("Running fail step %s".formatted(processDefinition.getFailStep()));		
				ProcessInstanceStep latestProcessedStep = processInstance.getLatestProcessedStep();
				runStepClass(processInstance.getLatestProcessedStep(), processDefinition.getFailStep());
				latestProcessedStep.setLogs(logs.toString());
			}
		}
		
		processService.saveProcessInstance(processInstance);
		RunnerSingleton.getInstance().stop(processInstance.getCode(), processDefinition.getCode());

	}

	private ExecutionResultsForCommand runCommand(ProcessInstanceStep step, String command) {

		ExecutionResultsForCommand result = new ExecutionResultsForCommand();

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
			boolean isValidEmailTemplate = validateEmailTemplate(arg1);
			if (!step.isNotificationSent()) {
				if (isValidEmailTemplate) {
					sendEmailNotificationForApproval(step, arg1);
				}
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

		return result.succeeded();
	}

	private boolean validateEmailTemplate(String emailTemplateCode) {
		return processService.getEmailTemplateByCode(emailTemplateCode) != null;
	}

	private void sendEmailNotificationForApproval(ProcessInstanceStep step, String emailTemplateCode) {
		EmailTemplate emailTemplate = processService.getEmailTemplateByCode(emailTemplateCode);
		if (emailTemplate == null) {
			throw new RuntimeException("email template %s not found");
		}

		HelperUtil.sendEmailByTemplate(emailTemplate, step, this);
	}

	private void runStep(ProcessInstance processInstance, ProcessInstanceStep step) {
		if (step.getStatus().equals(ProcessInstanceStep.STATUS_NEW)) {
			step.setStatus(ProcessInstanceStep.STATUS_RUNNING);
			step.setStarted(LocalDateTime.now());
			processService.saveProcessInstance(processInstance);
		}

		processService.deleteAllStepFiles(step);

		String normalizedCommand = normalize(step.getCommands());

		ExecutionResultsForCommand result = runCommand(step, normalizedCommand);

		if (result.getStatus().equals(ExecutionResultsForCommand.STATUS_FAILED)) {
			logger("Command [%s] execution is failed for step [%s] => %s".formatted(step.getCommands(),
					step.getStepCode(), result.getMessage()));
			step.setError(result.getMessage());
			step.setStatus(ProcessInstanceStep.STATUS_FAILED);
		} else {
			boolean isOk = result.getStatus().equals(ExecutionResultsForCommand.STATUS_SUCCESS);
			step.setStatus(isOk ? ProcessInstanceStep.STATUS_COMPLETED : ProcessInstanceStep.STATUS_RUNNING);
			step.setError(null);
			step.setFinished(isOk ? LocalDateTime.now() : null);
		}

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

	public ExecutionResultsForCommand runStepClass(ProcessInstanceStep step, String className) {
		ExecutionResultsForCommand result = new ExecutionResultsForCommand();
		BaseStep stepClassInstance = null;
		try {
			String packageName = processService.getEnvProperty("steps.package");
			Class<?> clazz = Class.forName(packageName + "." + className);
			java.lang.reflect.Constructor<?> constructor = clazz.getConstructor(RunnerUtil.class,
					ProcessInstanceStep.class);

			stepClassInstance = (BaseStep) constructor.newInstance(this, step);
			if (stepClassInstance != null) {
				stepClassInstance.setup();
			}

			stepClassInstance.run();
			result = result.succeeded();
		} catch (Exception e) {
			e.printStackTrace();
			result = result.failed().withMessage(e.getMessage());
		} finally {
			if (stepClassInstance != null) {
				stepClassInstance.teardown();
			}
		}

		return result;

	}

	public void logger(String logStr) {
		String str = "%s - %s".formatted(new Date().toString(), logStr);
		System.out.println(str);
		logs.append(str + "\n");

	}

	public List<ProcessInstance> runProcessDiscovery(ProcessDefinition processDefinition) {
		try {
			String packageName = processService.getEnvProperty("steps.package");
			Class<?> clazz = Class.forName(packageName + "." + processDefinition.getDiscovererClass());
			java.lang.reflect.Constructor<?> constructor = clazz.getConstructor(RunnerUtil.class,
					ProcessInstanceStep.class);
			BaseStep discovererInstance = (BaseStep) constructor.newInstance(this, null);
			return ((Discoverable) discovererInstance).discover(processDefinition);
			// return discovererInstance.discover(processDefinition);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	public String getEnvironmentParameter(String key) {
		return this.processService.getEnvProperty(key);
	}

	public void setVariable(String key, String value) {
		hmValues.put(key, value);
	}

	public String getVariable(String key) {
		return hmValues.get(key);
	}

	public void clearLogs() {
		this.logs.setLength(0);

	}

	public void clearVariables() {
		this.hmValues.clear();

	}

	public String getLogs() {
		return this.logs.toString();
	}

	public void loadVariables(HashMap<String, String> hm2Load) {
		this.hmValues.putAll(hm2Load);
		
	}

}
