package com.robodo.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.ProcessInstanceStepFile;

public interface ProcessInstanceStepFileRepo extends CrudRepository<ProcessInstanceStepFile, Long> {
	List<ProcessInstanceStepFile> findByProcessInstanceStepId(Long processInstanceStepId);

}
