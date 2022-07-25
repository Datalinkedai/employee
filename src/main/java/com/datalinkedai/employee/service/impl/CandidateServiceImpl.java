package com.datalinkedai.employee.service.impl;

import com.datalinkedai.employee.domain.Candidate;
import com.datalinkedai.employee.repository.CandidateRepository;
import com.datalinkedai.employee.service.CandidateService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Candidate}.
 */
@Service
public class CandidateServiceImpl implements CandidateService {

    private final Logger log = LoggerFactory.getLogger(CandidateServiceImpl.class);

    private final CandidateRepository candidateRepository;

    public CandidateServiceImpl(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    @Override
    public Mono<Candidate> save(Candidate candidate) {
        log.debug("Request to save Candidate : {}", candidate);
        return candidateRepository.save(candidate);
    }

    @Override
    public Mono<Candidate> update(Candidate candidate) {
        log.debug("Request to save Candidate : {}", candidate);
        return candidateRepository.save(candidate);
    }

    @Override
    public Mono<Candidate> partialUpdate(Candidate candidate) {
        log.debug("Request to partially update Candidate : {}", candidate);

        return candidateRepository
            .findById(candidate.getId())
            .map(existingCandidate -> {
                if (candidate.getFirstName() != null) {
                    existingCandidate.setFirstName(candidate.getFirstName());
                }
                if (candidate.getLastName() != null) {
                    existingCandidate.setLastName(candidate.getLastName());
                }
                if (candidate.getPhoneNumber() != null) {
                    existingCandidate.setPhoneNumber(candidate.getPhoneNumber());
                }
                if (candidate.getUserName() != null) {
                    existingCandidate.setUserName(candidate.getUserName());
                }
                if (candidate.getEductionQualification() != null) {
                    existingCandidate.setEductionQualification(candidate.getEductionQualification());
                }
                if (candidate.getResumeLink() != null) {
                    existingCandidate.setResumeLink(candidate.getResumeLink());
                }
                if (candidate.getStatus() != null) {
                    existingCandidate.setStatus(candidate.getStatus());
                }

                return existingCandidate;
            })
            .flatMap(candidateRepository::save);
    }

    @Override
    public Flux<Candidate> findAll() {
        log.debug("Request to get all Candidates");
        return candidateRepository.findAll();
    }

    public Mono<Long> countAll() {
        return candidateRepository.count();
    }

    @Override
    public Mono<Candidate> findOne(String id) {
        log.debug("Request to get Candidate : {}", id);
        return candidateRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Candidate : {}", id);
        return candidateRepository.deleteById(id);
    }
}
