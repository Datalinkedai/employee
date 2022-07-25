package com.datalinkedai.employee.service;

import com.datalinkedai.employee.domain.KnowledgeCentral;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link KnowledgeCentral}.
 */
public interface KnowledgeCentralService {
    /**
     * Save a knowledgeCentral.
     *
     * @param knowledgeCentral the entity to save.
     * @return the persisted entity.
     */
    Mono<KnowledgeCentral> save(KnowledgeCentral knowledgeCentral);

    /**
     * Updates a knowledgeCentral.
     *
     * @param knowledgeCentral the entity to update.
     * @return the persisted entity.
     */
    Mono<KnowledgeCentral> update(KnowledgeCentral knowledgeCentral);

    /**
     * Partially updates a knowledgeCentral.
     *
     * @param knowledgeCentral the entity to update partially.
     * @return the persisted entity.
     */
    Mono<KnowledgeCentral> partialUpdate(KnowledgeCentral knowledgeCentral);

    /**
     * Get all the knowledgeCentrals.
     *
     * @return the list of entities.
     */
    Flux<KnowledgeCentral> findAll();

    /**
     * Returns the number of knowledgeCentrals available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" knowledgeCentral.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<KnowledgeCentral> findOne(String id);

    /**
     * Delete the "id" knowledgeCentral.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
