package com.datalinkedai.employee.repository;

import com.datalinkedai.employee.domain.Questions;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Questions entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuestionsRepository extends ReactiveMongoRepository<Questions, String> {}
