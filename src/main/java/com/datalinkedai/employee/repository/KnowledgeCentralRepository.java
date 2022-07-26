package com.datalinkedai.employee.repository;

import com.datalinkedai.employee.domain.Candidate;
import com.datalinkedai.employee.domain.KnowledgeCentral;

import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the KnowledgeCentral entity.
 */
// @SuppressWarnings("unused")
@Repository
public interface KnowledgeCentralRepository extends ReactiveMongoRepository<KnowledgeCentral, String> {

    Mono<KnowledgeCentral> getKnowledgeCentralByCandidateTaken(Candidate candidate);
}
