package com.datalinkedai.employee.repository;

import com.datalinkedai.employee.domain.Questions;
import com.datalinkedai.employee.domain.Tested;

import reactor.core.publisher.Flux;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Questions entity.
 */
// @SuppressWarnings("unused")
@Repository
public interface QuestionsRepository extends ReactiveMongoRepository<Questions, String> {

    Flux<Questions> getQuestionsByTested(Tested tested);
}
