package com.datalinkedai.employee.service;

import com.datalinkedai.employee.domain.Knowledge;
import com.datalinkedai.employee.domain.Tested;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Knowledge}.
 */
public interface KnowledgeService {
    /**
     * Save a knowledge.
     *
     * @param knowledge the entity to save.
     * @return the persisted entity.
     */
    Mono<Knowledge> save(Knowledge knowledge);

    /**
     * Updates a knowledge.
     *
     * @param knowledge the entity to update.
     * @return the persisted entity.
     */
    Mono<Knowledge> update(Knowledge knowledge);

    /**
     * Partially updates a knowledge.
     *
     * @param knowledge the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Knowledge> partialUpdate(Knowledge knowledge);

    /**
     * Get all the knowledges.
     *
     * @return the list of entities.
     */
    Flux<Knowledge> findAll();

    /**
     * Returns the number of knowledges available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" knowledge.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Knowledge> findOne(String id);

    /**
     * Delete the "id" knowledge.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
    /**
     * Get the knowlegde of a particular candidate object
     * 
     * @param candidateId id of the particular candidate
     * @return Knowledge object 
     * @throws Exception
     */
    Mono<Knowledge> getKnowledgeByCandidateTaken(String candidateId) throws Exception;

    /**
     * get knowledge for a particular test object
     * @param TestedName name of the test object
     * @return a particular test object
     * @throws Exception
     */
    Flux<Knowledge> getKnowledgeByTests(String testedName) throws Exception;
}
