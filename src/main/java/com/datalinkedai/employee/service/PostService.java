package com.datalinkedai.employee.service;

import com.datalinkedai.employee.domain.Post;
import com.datalinkedai.employee.domain.Interview;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Post}.
 */
public interface PostService {
    /**
     * Save a post.
     *
     * @param post the entity to save.
     * @return the persisted entity.
     */
    Mono<Post> save(Post post);

    /**
     * Updates a post.
     *
     * @param post the entity to update.
     * @return the persisted entity.
     */
    Mono<Post> update(Post post);

    /**
     * Partially updates a post.
     *
     * @param post the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Post> partialUpdate(Post post);

    /**
     * Get all the posts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Post> findAll(Pageable pageable);

    /**
     * Returns the number of posts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" post.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Post> findOne(String id);

    /**
     * Delete the "id" post.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
    /**
     * Save a interview.
     * @param postId the id of the post entity.
     * @return the interview details
     */
    Mono<Interview> applyForJob(String postId);
}
