package com.datalinkedai.employee.repository;

import com.datalinkedai.employee.domain.Knowledge;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Knowledge entity.
 */
@SuppressWarnings("unused")
@Repository
public interface KnowledgeRepository extends ReactiveMongoRepository<Knowledge, String> {}
