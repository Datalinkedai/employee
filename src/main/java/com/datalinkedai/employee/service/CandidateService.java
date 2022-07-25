package com.datalinkedai.employee.service;

import com.datalinkedai.employee.domain.Candidate;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Candidate}.
 */
public interface CandidateService {
    /**
     * Save a candidate.
     *
     * @param candidate the entity to save.
     * @return the persisted entity.
     */
    Mono<Candidate> save(Candidate candidate);

    /**
     * Updates a candidate.
     *
     * @param candidate the entity to update.
     * @return the persisted entity.
     */
    Mono<Candidate> update(Candidate candidate);

    /**
     * Partially updates a candidate.
     *
     * @param candidate the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Candidate> partialUpdate(Candidate candidate);

    /**
     * Get all the candidates.
     *
     * @return the list of entities.
     */
    Flux<Candidate> findAll();

    /**
     * Returns the number of candidates available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" candidate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Candidate> findOne(String id);

    /**
     * Delete the "id" candidate.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
