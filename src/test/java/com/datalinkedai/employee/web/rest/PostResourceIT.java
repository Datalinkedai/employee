package com.datalinkedai.employee.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datalinkedai.employee.IntegrationTest;
import com.datalinkedai.employee.domain.Post;
import com.datalinkedai.employee.domain.enumeration.Status;
import com.datalinkedai.employee.repository.PostRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link PostResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PostResourceIT {

    private static final String DEFAULT_POST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_POST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_MINIMUM_EXPERIENCE = 1D;
    private static final Double UPDATED_MINIMUM_EXPERIENCE = 2D;

    private static final Double DEFAULT_MAXIMUM_EXPERIENCE = 1D;
    private static final Double UPDATED_MAXIMUM_EXPERIENCE = 2D;

    private static final String DEFAULT_ROLES = "AAAAAAAAAA";
    private static final String UPDATED_ROLES = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSIBILITY = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSIBILITY = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final String DEFAULT_TYPE_OF_EMPLOYMENT = "AAAAAAAAAA";
    private static final String UPDATED_TYPE_OF_EMPLOYMENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_POSTED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_POSTED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/posts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Post post;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createEntity() {
        Post post = new Post()
            .postName(DEFAULT_POST_NAME)
            .description(DEFAULT_DESCRIPTION)
            .minimumExperience(DEFAULT_MINIMUM_EXPERIENCE)
            .maximumExperience(DEFAULT_MAXIMUM_EXPERIENCE)
            .roles(DEFAULT_ROLES)
            .responsibility(DEFAULT_RESPONSIBILITY)
            .status(DEFAULT_STATUS)
            .typeOfEmployment(DEFAULT_TYPE_OF_EMPLOYMENT)
            .postedDate(DEFAULT_POSTED_DATE);
        return post;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createUpdatedEntity() {
        Post post = new Post()
            .postName(UPDATED_POST_NAME)
            .description(UPDATED_DESCRIPTION)
            .minimumExperience(UPDATED_MINIMUM_EXPERIENCE)
            .maximumExperience(UPDATED_MAXIMUM_EXPERIENCE)
            .roles(UPDATED_ROLES)
            .responsibility(UPDATED_RESPONSIBILITY)
            .status(UPDATED_STATUS)
            .typeOfEmployment(UPDATED_TYPE_OF_EMPLOYMENT)
            .postedDate(UPDATED_POSTED_DATE);
        return post;
    }

    @BeforeEach
    public void initTest() {
        postRepository.deleteAll().block();
        post = createEntity();
    }

    @Test
    void createPost() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().collectList().block().size();
        // Create the Post
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeCreate + 1);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getPostName()).isEqualTo(DEFAULT_POST_NAME);
        assertThat(testPost.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPost.getMinimumExperience()).isEqualTo(DEFAULT_MINIMUM_EXPERIENCE);
        assertThat(testPost.getMaximumExperience()).isEqualTo(DEFAULT_MAXIMUM_EXPERIENCE);
        assertThat(testPost.getRoles()).isEqualTo(DEFAULT_ROLES);
        assertThat(testPost.getResponsibility()).isEqualTo(DEFAULT_RESPONSIBILITY);
        assertThat(testPost.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPost.getTypeOfEmployment()).isEqualTo(DEFAULT_TYPE_OF_EMPLOYMENT);
        assertThat(testPost.getPostedDate()).isEqualTo(DEFAULT_POSTED_DATE);
    }

    @Test
    void createPostWithExistingId() throws Exception {
        // Create the Post with an existing ID
        post.setId("existing_id");

        int databaseSizeBeforeCreate = postRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkPostNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = postRepository.findAll().collectList().block().size();
        // set the field null
        post.setPostName(null);

        // Create the Post, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPosts() {
        // Initialize the database
        postRepository.save(post).block();

        // Get all the postList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(post.getId()))
            .jsonPath("$.[*].postName")
            .value(hasItem(DEFAULT_POST_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].minimumExperience")
            .value(hasItem(DEFAULT_MINIMUM_EXPERIENCE.doubleValue()))
            .jsonPath("$.[*].maximumExperience")
            .value(hasItem(DEFAULT_MAXIMUM_EXPERIENCE.doubleValue()))
            .jsonPath("$.[*].roles")
            .value(hasItem(DEFAULT_ROLES))
            .jsonPath("$.[*].responsibility")
            .value(hasItem(DEFAULT_RESPONSIBILITY))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].typeOfEmployment")
            .value(hasItem(DEFAULT_TYPE_OF_EMPLOYMENT))
            .jsonPath("$.[*].postedDate")
            .value(hasItem(DEFAULT_POSTED_DATE.toString()));
    }

    @Test
    void getPost() {
        // Initialize the database
        postRepository.save(post).block();

        // Get the post
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, post.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(post.getId()))
            .jsonPath("$.postName")
            .value(is(DEFAULT_POST_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.minimumExperience")
            .value(is(DEFAULT_MINIMUM_EXPERIENCE.doubleValue()))
            .jsonPath("$.maximumExperience")
            .value(is(DEFAULT_MAXIMUM_EXPERIENCE.doubleValue()))
            .jsonPath("$.roles")
            .value(is(DEFAULT_ROLES))
            .jsonPath("$.responsibility")
            .value(is(DEFAULT_RESPONSIBILITY))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.typeOfEmployment")
            .value(is(DEFAULT_TYPE_OF_EMPLOYMENT))
            .jsonPath("$.postedDate")
            .value(is(DEFAULT_POSTED_DATE.toString()));
    }

    @Test
    void getNonExistingPost() {
        // Get the post
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPost() throws Exception {
        // Initialize the database
        postRepository.save(post).block();

        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();

        // Update the post
        Post updatedPost = postRepository.findById(post.getId()).block();
        updatedPost
            .postName(UPDATED_POST_NAME)
            .description(UPDATED_DESCRIPTION)
            .minimumExperience(UPDATED_MINIMUM_EXPERIENCE)
            .maximumExperience(UPDATED_MAXIMUM_EXPERIENCE)
            .roles(UPDATED_ROLES)
            .responsibility(UPDATED_RESPONSIBILITY)
            .status(UPDATED_STATUS)
            .typeOfEmployment(UPDATED_TYPE_OF_EMPLOYMENT)
            .postedDate(UPDATED_POSTED_DATE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPost.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPost))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getPostName()).isEqualTo(UPDATED_POST_NAME);
        assertThat(testPost.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPost.getMinimumExperience()).isEqualTo(UPDATED_MINIMUM_EXPERIENCE);
        assertThat(testPost.getMaximumExperience()).isEqualTo(UPDATED_MAXIMUM_EXPERIENCE);
        assertThat(testPost.getRoles()).isEqualTo(UPDATED_ROLES);
        assertThat(testPost.getResponsibility()).isEqualTo(UPDATED_RESPONSIBILITY);
        assertThat(testPost.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPost.getTypeOfEmployment()).isEqualTo(UPDATED_TYPE_OF_EMPLOYMENT);
        assertThat(testPost.getPostedDate()).isEqualTo(UPDATED_POSTED_DATE);
    }

    @Test
    void putNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();
        post.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, post.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();
        post.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();
        post.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePostWithPatch() throws Exception {
        // Initialize the database
        postRepository.save(post).block();

        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();

        // Update the post using partial update
        Post partialUpdatedPost = new Post();
        partialUpdatedPost.setId(post.getId());

        partialUpdatedPost
            .maximumExperience(UPDATED_MAXIMUM_EXPERIENCE)
            .responsibility(UPDATED_RESPONSIBILITY)
            .status(UPDATED_STATUS)
            .typeOfEmployment(UPDATED_TYPE_OF_EMPLOYMENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPost.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPost))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getPostName()).isEqualTo(DEFAULT_POST_NAME);
        assertThat(testPost.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPost.getMinimumExperience()).isEqualTo(DEFAULT_MINIMUM_EXPERIENCE);
        assertThat(testPost.getMaximumExperience()).isEqualTo(UPDATED_MAXIMUM_EXPERIENCE);
        assertThat(testPost.getRoles()).isEqualTo(DEFAULT_ROLES);
        assertThat(testPost.getResponsibility()).isEqualTo(UPDATED_RESPONSIBILITY);
        assertThat(testPost.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPost.getTypeOfEmployment()).isEqualTo(UPDATED_TYPE_OF_EMPLOYMENT);
        assertThat(testPost.getPostedDate()).isEqualTo(DEFAULT_POSTED_DATE);
    }

    @Test
    void fullUpdatePostWithPatch() throws Exception {
        // Initialize the database
        postRepository.save(post).block();

        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();

        // Update the post using partial update
        Post partialUpdatedPost = new Post();
        partialUpdatedPost.setId(post.getId());

        partialUpdatedPost
            .postName(UPDATED_POST_NAME)
            .description(UPDATED_DESCRIPTION)
            .minimumExperience(UPDATED_MINIMUM_EXPERIENCE)
            .maximumExperience(UPDATED_MAXIMUM_EXPERIENCE)
            .roles(UPDATED_ROLES)
            .responsibility(UPDATED_RESPONSIBILITY)
            .status(UPDATED_STATUS)
            .typeOfEmployment(UPDATED_TYPE_OF_EMPLOYMENT)
            .postedDate(UPDATED_POSTED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPost.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPost))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getPostName()).isEqualTo(UPDATED_POST_NAME);
        assertThat(testPost.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPost.getMinimumExperience()).isEqualTo(UPDATED_MINIMUM_EXPERIENCE);
        assertThat(testPost.getMaximumExperience()).isEqualTo(UPDATED_MAXIMUM_EXPERIENCE);
        assertThat(testPost.getRoles()).isEqualTo(UPDATED_ROLES);
        assertThat(testPost.getResponsibility()).isEqualTo(UPDATED_RESPONSIBILITY);
        assertThat(testPost.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPost.getTypeOfEmployment()).isEqualTo(UPDATED_TYPE_OF_EMPLOYMENT);
        assertThat(testPost.getPostedDate()).isEqualTo(UPDATED_POSTED_DATE);
    }

    @Test
    void patchNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();
        post.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, post.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();
        post.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().collectList().block().size();
        post.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(post))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePost() {
        // Initialize the database
        postRepository.save(post).block();

        int databaseSizeBeforeDelete = postRepository.findAll().collectList().block().size();

        // Delete the post
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, post.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Post> postList = postRepository.findAll().collectList().block();
        assertThat(postList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
