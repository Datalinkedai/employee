package com.datalinkedai.employee.service;

import com.datalinkedai.employee.domain.Interview;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Interview}.
 */
public interface InterviewService {
    /**
     * Save a interview.
     *
     * @param interview the entity to save.
     * @return the persisted entity.
     */
    Mono<Interview> save(Interview interview);

    /**
     * Updates a interview.
     *
     * @param interview the entity to update.
     * @return the persisted entity.
     */
    Mono<Interview> update(Interview interview);

    /**
     * Partially updates a interview.
     *
     * @param interview the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Interview> partialUpdate(Interview interview);

    /**
     * Get all the interviews.
     *
     * @return the list of entities.
     */
    Flux<Interview> findAll();

    /**
     * Returns the number of interviews available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" interview.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Interview> findOne(String id);

    /**
     * Delete the "id" interview.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * @param candidateId
     * @param interviewStatus
     * @return count of interview based on status and candidate
     */
    Mono<Integer> getInterviewScheduledForCandidate(String candidateId, String interviewStatus);

    /**
     *
     * @param candidateId
     * @return list of interview for an interviewer
     */
    Flux<Interview> getInterviewByInterviewBy(String candidateId);

    /**
     *
     * @param candidate
     * @param interviewStatus
     * @return
     */
    Mono<Interview> getInterviewByCandidateAndStatus(String candidate, String interviewStatus);
}
