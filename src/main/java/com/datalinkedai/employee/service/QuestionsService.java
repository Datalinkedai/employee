package com.datalinkedai.employee.service;

import com.datalinkedai.employee.domain.Questions;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Questions}.
 */
public interface QuestionsService {
    /**
     * Save a questions.
     *
     * @param questions the entity to save.
     * @return the persisted entity.
     */
    Mono<Questions> save(Questions questions);

    /**
     * Updates a questions.
     *
     * @param questions the entity to update.
     * @return the persisted entity.
     */
    Mono<Questions> update(Questions questions);

    /**
     * Partially updates a questions.
     *
     * @param questions the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Questions> partialUpdate(Questions questions);

    /**
     * Get all the questions.
     *
     * @return the list of entities.
     */
    Flux<Questions> findAll();

    /**
     * Returns the number of questions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" questions.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Questions> findOne(String id);

    /**
     * Delete the "id" questions.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
