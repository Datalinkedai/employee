package com.datalinkedai.employee.service;

import com.datalinkedai.employee.domain.Documents;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Documents}.
 */
public interface DocumentsService {
    /**
     * Save a documents.
     *
     * @param documents the entity to save.
     * @return the persisted entity.
     */
    Mono<Documents> save(Documents documents);

    /**
     * Updates a documents.
     *
     * @param documents the entity to update.
     * @return the persisted entity.
     */
    Mono<Documents> update(Documents documents);

    /**
     * Partially updates a documents.
     *
     * @param documents the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Documents> partialUpdate(Documents documents);

    /**
     * Get all the documents.
     *
     * @return the list of entities.
     */
    Flux<Documents> findAll();

    /**
     * Returns the number of documents available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" documents.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Documents> findOne(String id);

    /**
     * Delete the "id" documents.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
