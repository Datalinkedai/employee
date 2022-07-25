package com.datalinkedai.employee.repository;

import com.datalinkedai.employee.domain.Options;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Options entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OptionsRepository extends ReactiveMongoRepository<Options, String> {}
