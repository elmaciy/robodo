package com.robodo.repo;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.ProcessInstance;

public interface ProcessInstanceRepo extends CrudRepository<ProcessInstance, Long> {

}
