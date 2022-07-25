package com.datalinkedai.employee.service;

import com.datalinkedai.employee.domain.Training;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Training}.
 */
public interface TrainingService {
    /**
     * Save a training.
     *
     * @param training the entity to save.
     * @return the persisted entity.
     */
    Mono<Training> save(Training training);

    /**
     * Updates a training.
     *
     * @param training the entity to update.
     * @return the persisted entity.
     */
    Mono<Training> update(Training training);

    /**
     * Partially updates a training.
     *
     * @param training the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Training> partialUpdate(Training training);

    /**
     * Get all the trainings.
     *
     * @return the list of entities.
     */
    Flux<Training> findAll();

    /**
     * Returns the number of trainings available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" training.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Training> findOne(String id);

    /**
     * Delete the "id" training.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
