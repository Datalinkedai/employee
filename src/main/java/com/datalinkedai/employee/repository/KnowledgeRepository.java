package com.datalinkedai.employee.repository;

import com.datalinkedai.employee.domain.Candidate;
import com.datalinkedai.employee.domain.Knowledge;
import com.datalinkedai.employee.domain.Tested;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data MongoDB reactive repository for the Knowledge entity.
 */
@SuppressWarnings("unused")
@Repository
public interface KnowledgeRepository extends ReactiveMongoRepository<Knowledge, String> {
    Flux<Knowledge> getKnowledgeByTests(Tested test);

    Mono<Knowledge> getKnowledgeByCandidateTaken(Candidate candidate);
}

