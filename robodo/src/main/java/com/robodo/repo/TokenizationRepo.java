package com.robodo.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.robodo.model.Tokenization;

public interface TokenizationRepo extends CrudRepository<Tokenization, Long> {
	List<Tokenization> findByTokenAndPurposeAndPurposeDetail(String token, String purpose, String purposeDetail);
	List<Tokenization> findByPurposeAndPurposeDetail(String purpose, String purposeDetail);
	List<Tokenization> findByValidToBefore(LocalDateTime validTo);

}
