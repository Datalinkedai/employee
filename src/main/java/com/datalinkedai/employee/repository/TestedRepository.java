package com.datalinkedai.employee.repository;

import com.datalinkedai.employee.domain.Tested;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Tested entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TestedRepository extends ReactiveMongoRepository<Tested, String> {}
