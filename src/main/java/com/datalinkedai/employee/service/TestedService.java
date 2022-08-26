package com.datalinkedai.employee.service;

import com.datalinkedai.employee.domain.Tested;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Tested}.
 */
public interface TestedService {
    /**
     * Save a tested.
     *
     * @param tested the entity to save.
     * @return the persisted entity.
     */
    Mono<Tested> save(Tested tested);

    /**
     * Updates a tested.
     *
     * @param tested the entity to update.
     * @return the persisted entity.
     */
    Mono<Tested> update(Tested tested);

    /**
     * Partially updates a tested.
     *
     * @param tested the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Tested> partialUpdate(Tested tested);

    /**
     * Get all the testeds.
     *
     * @return the list of entities.
     */
    Flux<Tested> findAll();

    /**
     * Returns the number of testeds available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" tested.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Tested> findOne(String id);

    /**
     * Delete the "id" tested.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
    
    /**
     * If randomised flag is true return radomised test questions
     * 
     * @param testName Get the test name
     * @return a Mono of Tested object with total test questions
     * @exception tested object not found error
     */
    Mono<Tested> getRandomizeQuestionsForTest(String testName) throws Exception;
}
