package com.datalinkedai.employee.service.impl;

import com.datalinkedai.employee.domain.Candidate;
import com.datalinkedai.employee.domain.Knowledge;
import com.datalinkedai.employee.repository.CandidateRepository;
import com.datalinkedai.employee.domain.Tested;
import com.datalinkedai.employee.exceptions.TestNotFoundException;
import com.datalinkedai.employee.repository.KnowledgeRepository;
import com.datalinkedai.employee.repository.TestedRepository;
import com.datalinkedai.employee.service.KnowledgeService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.datalinkedai.employee.exceptions.CandidateNotFoundException;

/**
 * Service Implementation for managing {@link Knowledge}.
 */
@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    private final Logger log = LoggerFactory.getLogger(KnowledgeServiceImpl.class);

    private final KnowledgeRepository knowledgeRepository;

    @Autowired
    private TestedRepository testedRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    public KnowledgeServiceImpl(KnowledgeRepository knowledgeRepository) {
        this.knowledgeRepository = knowledgeRepository;
    }

    @Override
    public Mono<Knowledge> save(Knowledge knowledge) {
        log.debug("Request to save Knowledge : {}", knowledge);
        return knowledgeRepository.save(knowledge);
    }

    @Override
    public Mono<Knowledge> update(Knowledge knowledge) {
        log.debug("Request to save Knowledge : {}", knowledge);
        return knowledgeRepository.save(knowledge);
    }

    @Override
    public Mono<Knowledge> partialUpdate(Knowledge knowledge) {
        log.debug("Request to partially update Knowledge : {}", knowledge);

        return knowledgeRepository
            .findById(knowledge.getId())
            .map(existingKnowledge -> {
                if (knowledge.getResult() != null) {
                    existingKnowledge.setResult(knowledge.getResult());
                }
                if (knowledge.getTestTaken() != null) {
                    existingKnowledge.setTestTaken(knowledge.getTestTaken());
                }
                if (knowledge.getCertificate() != null) {
                    existingKnowledge.setCertificate(knowledge.getCertificate());
                }
                if (knowledge.getCertificateContentType() != null) {
                    existingKnowledge.setCertificateContentType(knowledge.getCertificateContentType());
                }

                return existingKnowledge;
            })
            .flatMap(knowledgeRepository::save);
    }

    @Override
    public Flux<Knowledge> findAll() {
        log.debug("Request to get all Knowledges");
        return knowledgeRepository.findAll();
    }

    public Mono<Long> countAll() {
        return knowledgeRepository.count();
    }

    @Override
    public Mono<Knowledge> findOne(String id) {
        log.debug("Request to get Knowledge : {}", id);
        return knowledgeRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Knowledge : {}", id);
        return knowledgeRepository.deleteById(id);
    }

    @Override
    public Mono<Knowledge> getKnowledgeByCandidateTaken(String candidateId) throws Exception {
        Candidate candidate;
        try {
            candidate = candidateRepository.findById(candidateId).toFuture().get();
        } catch (Exception e) {
            log.error("Candidate not found by: {}, {}", candidateId, e);
            throw new CandidateNotFoundException(candidateId);
        } 
        return knowledgeRepository.getKnowledgeByCandidateTaken(candidate);
    }

    @Override
    public Mono<Knowledge> getKnowledgeByTests(String testedName) throws Exception {
        Tested test;
        try {
            test = testedRepository.findById(testedName).toFuture().get();
        } catch (Exception e) {
            log.error("Test not found by: {}, {}", testedName, e);
            throw new TestNotFoundException(testedName);
        }
        return knowledgeRepository.getKnowledgeByTests(test);
    }
}
