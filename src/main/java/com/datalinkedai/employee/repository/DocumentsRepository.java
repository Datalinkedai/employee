package com.datalinkedai.employee.repository;

import com.datalinkedai.employee.domain.Documents;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Documents entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DocumentsRepository extends ReactiveMongoRepository<Documents, String> {}
