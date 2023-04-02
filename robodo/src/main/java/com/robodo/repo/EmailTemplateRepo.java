package com.robodo.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.EmailTemplate;

public interface EmailTemplateRepo extends CrudRepository<EmailTemplate, Long> {
	List<EmailTemplate> findByCode(String code);

}
