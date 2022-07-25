package com.datalinkedai.employee.repository;

import com.datalinkedai.employee.domain.Interview;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Interview entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InterviewRepository extends ReactiveMongoRepository<Interview, String> {}
