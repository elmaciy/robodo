package com.robodo.model;

import java.util.Objects;

public class RunningProcess {
	private String name;
	private String group;
	long startTs;
	
	
	
	
	public RunningProcess(String name, String group, long startTs) {
		super();
		this.name = name;
		this.group = group;
		this.startTs = startTs;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}


	public long getStartTs() {
		return startTs;
	}


	public void setStartTs(long startTs) {
		this.startTs = startTs;
	}


	@Override
	public int hashCode() {
		return Objects.hash(group, name);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RunningProcess other = (RunningProcess) obj;
		return Objects.equals(group, other.group) && Objects.equals(name, other.name);
	}

}
