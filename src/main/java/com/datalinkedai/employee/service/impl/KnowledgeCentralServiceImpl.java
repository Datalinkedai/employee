package com.datalinkedai.employee.service.impl;

import com.datalinkedai.employee.domain.Candidate;
import com.datalinkedai.employee.domain.KnowledgeCentral;
import com.datalinkedai.employee.exceptions.CandidateNotFoundException;
import com.datalinkedai.employee.repository.CandidateRepository;
import com.datalinkedai.employee.repository.KnowledgeCentralRepository;
import com.datalinkedai.employee.service.KnowledgeCentralService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link KnowledgeCentral}.
 */
@Service
public class KnowledgeCentralServiceImpl implements KnowledgeCentralService {

    private final Logger log = LoggerFactory.getLogger(KnowledgeCentralServiceImpl.class);

    @Autowired
    private KnowledgeCentralRepository knowledgeCentralRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public Mono<KnowledgeCentral> save(KnowledgeCentral knowledgeCentral) {
        log.debug("Request to save KnowledgeCentral : {}", knowledgeCentral);
        return knowledgeCentralRepository.save(knowledgeCentral);
    }

    @Override
    public Mono<KnowledgeCentral> update(KnowledgeCentral knowledgeCentral) {
        log.debug("Request to save KnowledgeCentral : {}", knowledgeCentral);
        return knowledgeCentralRepository.save(knowledgeCentral);
    }

    @Override
    public Mono<KnowledgeCentral> partialUpdate(KnowledgeCentral knowledgeCentral) {
        log.debug("Request to partially update KnowledgeCentral : {}", knowledgeCentral);

        return knowledgeCentralRepository
            .findById(knowledgeCentral.getId())
            .map(existingKnowledgeCentral -> {
                if (knowledgeCentral.getAverageResult() != null) {
                    existingKnowledgeCentral.setAverageResult(knowledgeCentral.getAverageResult());
                }

                return existingKnowledgeCentral;
            })
            .flatMap(knowledgeCentralRepository::save);
    }

    @Override
    public Flux<KnowledgeCentral> findAll() {
        log.debug("Request to get all KnowledgeCentrals");
        return knowledgeCentralRepository.findAll();
    }

    public Mono<Long> countAll() {
        return knowledgeCentralRepository.count();
    }

    @Override
    public Mono<KnowledgeCentral> findOne(String id) {
        log.debug("Request to get KnowledgeCentral : {}", id);
        return knowledgeCentralRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete KnowledgeCentral : {}", id);
        return knowledgeCentralRepository.deleteById(id);
    }

    @Override
    public Mono<KnowledgeCentral> getKnowledgeCentralByCandiate(String candidateId) throws Exception {
        Candidate candidate;
        try {
            candidate = candidateRepository.findById(candidateId).toFuture().get();
        } catch (Exception e) {
            log.error("Candidate not found by: {}, {}", candidateId, e);
            throw new CandidateNotFoundException(candidateId);
        } 
        return knowledgeCentralRepository.getKnowledgeCentralByCandidateTaken(candidate);
    }
}
