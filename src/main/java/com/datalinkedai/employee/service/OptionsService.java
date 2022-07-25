package com.datalinkedai.employee.service;

import com.datalinkedai.employee.domain.Options;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Options}.
 */
public interface OptionsService {
    /**
     * Save a options.
     *
     * @param options the entity to save.
     * @return the persisted entity.
     */
    Mono<Options> save(Options options);

    /**
     * Updates a options.
     *
     * @param options the entity to update.
     * @return the persisted entity.
     */
    Mono<Options> update(Options options);

    /**
     * Partially updates a options.
     *
     * @param options the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Options> partialUpdate(Options options);

    /**
     * Get all the options.
     *
     * @return the list of entities.
     */
    Flux<Options> findAll();

    /**
     * Returns the number of options available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" options.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Options> findOne(String id);

    /**
     * Delete the "id" options.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
