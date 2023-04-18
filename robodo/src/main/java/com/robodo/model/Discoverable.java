package com.robodo.model;

import java.util.List;

public interface Discoverable {
	public abstract List<ProcessInstance> discover(ProcessDefinition processDefinition);
}
