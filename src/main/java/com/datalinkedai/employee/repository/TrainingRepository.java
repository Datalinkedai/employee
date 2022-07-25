package com.datalinkedai.employee.repository;

import com.datalinkedai.employee.domain.Training;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Training entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TrainingRepository extends ReactiveMongoRepository<Training, String> {}
