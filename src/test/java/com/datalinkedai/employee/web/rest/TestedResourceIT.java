package com.datalinkedai.employee.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datalinkedai.employee.IntegrationTest;
import com.datalinkedai.employee.domain.Tested;
import com.datalinkedai.employee.repository.TestedRepository;
import java.time.Duration;
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
 * Integration tests for the {@link TestedResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TestedResourceIT {

    private static final String DEFAULT_TEST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TEST_NAME = "BBBBBBBBBB";

    private static final Duration DEFAULT_TIME_TO_COMPLETE = Duration.ofHours(6);
    private static final Duration UPDATED_TIME_TO_COMPLETE = Duration.ofHours(12);

    private static final Integer DEFAULT_TOTAL_QUESTIONS = 1;
    private static final Integer UPDATED_TOTAL_QUESTIONS = 2;

    private static final Boolean DEFAULT_RANDOMIZE = false;
    private static final Boolean UPDATED_RANDOMIZE = true;

    private static final Double DEFAULT_PASSING_PRCNT = 0D;
    private static final Double UPDATED_PASSING_PRCNT = 1D;

    private static final Double DEFAULT_EXPIRY_MONTHS = 1D;
    private static final Double UPDATED_EXPIRY_MONTHS = 2D;

    private static final String ENTITY_API_URL = "/api/testeds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private TestedRepository testedRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Tested tested;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tested createEntity() {
        Tested tested = new Tested()
            .testName(DEFAULT_TEST_NAME)
            .timeToComplete(DEFAULT_TIME_TO_COMPLETE)
            .totalQuestions(DEFAULT_TOTAL_QUESTIONS)
            .randomize(DEFAULT_RANDOMIZE)
            .passingPrcnt(DEFAULT_PASSING_PRCNT)
            .expiryMonths(DEFAULT_EXPIRY_MONTHS);
        return tested;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tested createUpdatedEntity() {
        Tested tested = new Tested()
            .testName(UPDATED_TEST_NAME)
            .timeToComplete(UPDATED_TIME_TO_COMPLETE)
            .totalQuestions(UPDATED_TOTAL_QUESTIONS)
            .randomize(UPDATED_RANDOMIZE)
            .passingPrcnt(UPDATED_PASSING_PRCNT)
            .expiryMonths(UPDATED_EXPIRY_MONTHS);
        return tested;
    }

    @BeforeEach
    public void initTest() {
        testedRepository.deleteAll().block();
        tested = createEntity();
    }

    @Test
    void createTested() throws Exception {
        int databaseSizeBeforeCreate = testedRepository.findAll().collectList().block().size();
        // Create the Tested
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tested))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Tested in the database
        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeCreate + 1);
        Tested testTested = testedList.get(testedList.size() - 1);
        assertThat(testTested.getTestName()).isEqualTo(DEFAULT_TEST_NAME);
        assertThat(testTested.getTimeToComplete()).isEqualTo(DEFAULT_TIME_TO_COMPLETE);
        assertThat(testTested.getTotalQuestions()).isEqualTo(DEFAULT_TOTAL_QUESTIONS);
        assertThat(testTested.getRandomize()).isEqualTo(DEFAULT_RANDOMIZE);
        assertThat(testTested.getPassingPrcnt()).isEqualTo(DEFAULT_PASSING_PRCNT);
        assertThat(testTested.getExpiryMonths()).isEqualTo(DEFAULT_EXPIRY_MONTHS);
    }

    @Test
    void createTestedWithExistingId() throws Exception {
        // Create the Tested with an existing ID
        tested.setId("existing_id");

        int databaseSizeBeforeCreate = testedRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tested))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tested in the database
        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTestNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = testedRepository.findAll().collectList().block().size();
        // set the field null
        tested.setTestName(null);

        // Create the Tested, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tested))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTotalQuestionsIsRequired() throws Exception {
        int databaseSizeBeforeTest = testedRepository.findAll().collectList().block().size();
        // set the field null
        tested.setTotalQuestions(null);

        // Create the Tested, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tested))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllTestedsAsStream() {
        // Initialize the database
        testedRepository.save(tested).block();

        List<Tested> testedList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Tested.class)
            .getResponseBody()
            .filter(tested::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(testedList).isNotNull();
        assertThat(testedList).hasSize(1);
        Tested testTested = testedList.get(0);
        assertThat(testTested.getTestName()).isEqualTo(DEFAULT_TEST_NAME);
        assertThat(testTested.getTimeToComplete()).isEqualTo(DEFAULT_TIME_TO_COMPLETE);
        assertThat(testTested.getTotalQuestions()).isEqualTo(DEFAULT_TOTAL_QUESTIONS);
        assertThat(testTested.getRandomize()).isEqualTo(DEFAULT_RANDOMIZE);
        assertThat(testTested.getPassingPrcnt()).isEqualTo(DEFAULT_PASSING_PRCNT);
        assertThat(testTested.getExpiryMonths()).isEqualTo(DEFAULT_EXPIRY_MONTHS);
    }

    @Test
    void getAllTesteds() {
        // Initialize the database
        testedRepository.save(tested).block();

        // Get all the testedList
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
            .value(hasItem(tested.getId()))
            .jsonPath("$.[*].testName")
            .value(hasItem(DEFAULT_TEST_NAME))
            .jsonPath("$.[*].timeToComplete")
            .value(hasItem(DEFAULT_TIME_TO_COMPLETE.toString()))
            .jsonPath("$.[*].totalQuestions")
            .value(hasItem(DEFAULT_TOTAL_QUESTIONS))
            .jsonPath("$.[*].randomize")
            .value(hasItem(DEFAULT_RANDOMIZE.booleanValue()))
            .jsonPath("$.[*].passingPrcnt")
            .value(hasItem(DEFAULT_PASSING_PRCNT.doubleValue()))
            .jsonPath("$.[*].expiryMonths")
            .value(hasItem(DEFAULT_EXPIRY_MONTHS.doubleValue()));
    }

    @Test
    void getTested() {
        // Initialize the database
        testedRepository.save(tested).block();

        // Get the tested
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tested.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tested.getId()))
            .jsonPath("$.testName")
            .value(is(DEFAULT_TEST_NAME))
            .jsonPath("$.timeToComplete")
            .value(is(DEFAULT_TIME_TO_COMPLETE.toString()))
            .jsonPath("$.totalQuestions")
            .value(is(DEFAULT_TOTAL_QUESTIONS))
            .jsonPath("$.randomize")
            .value(is(DEFAULT_RANDOMIZE.booleanValue()))
            .jsonPath("$.passingPrcnt")
            .value(is(DEFAULT_PASSING_PRCNT.doubleValue()))
            .jsonPath("$.expiryMonths")
            .value(is(DEFAULT_EXPIRY_MONTHS.doubleValue()));
    }

    @Test
    void getNonExistingTested() {
        // Get the tested
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTested() throws Exception {
        // Initialize the database
        testedRepository.save(tested).block();

        int databaseSizeBeforeUpdate = testedRepository.findAll().collectList().block().size();

        // Update the tested
        Tested updatedTested = testedRepository.findById(tested.getId()).block();
        updatedTested
            .testName(UPDATED_TEST_NAME)
            .timeToComplete(UPDATED_TIME_TO_COMPLETE)
            .totalQuestions(UPDATED_TOTAL_QUESTIONS)
            .randomize(UPDATED_RANDOMIZE)
            .passingPrcnt(UPDATED_PASSING_PRCNT)
            .expiryMonths(UPDATED_EXPIRY_MONTHS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTested.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTested))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tested in the database
        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeUpdate);
        Tested testTested = testedList.get(testedList.size() - 1);
        assertThat(testTested.getTestName()).isEqualTo(UPDATED_TEST_NAME);
        assertThat(testTested.getTimeToComplete()).isEqualTo(UPDATED_TIME_TO_COMPLETE);
        assertThat(testTested.getTotalQuestions()).isEqualTo(UPDATED_TOTAL_QUESTIONS);
        assertThat(testTested.getRandomize()).isEqualTo(UPDATED_RANDOMIZE);
        assertThat(testTested.getPassingPrcnt()).isEqualTo(UPDATED_PASSING_PRCNT);
        assertThat(testTested.getExpiryMonths()).isEqualTo(UPDATED_EXPIRY_MONTHS);
    }

    @Test
    void putNonExistingTested() throws Exception {
        int databaseSizeBeforeUpdate = testedRepository.findAll().collectList().block().size();
        tested.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tested.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tested))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tested in the database
        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTested() throws Exception {
        int databaseSizeBeforeUpdate = testedRepository.findAll().collectList().block().size();
        tested.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tested))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tested in the database
        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTested() throws Exception {
        int databaseSizeBeforeUpdate = testedRepository.findAll().collectList().block().size();
        tested.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tested))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tested in the database
        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTestedWithPatch() throws Exception {
        // Initialize the database
        testedRepository.save(tested).block();

        int databaseSizeBeforeUpdate = testedRepository.findAll().collectList().block().size();

        // Update the tested using partial update
        Tested partialUpdatedTested = new Tested();
        partialUpdatedTested.setId(tested.getId());

        partialUpdatedTested
            .timeToComplete(UPDATED_TIME_TO_COMPLETE)
            .totalQuestions(UPDATED_TOTAL_QUESTIONS)
            .randomize(UPDATED_RANDOMIZE)
            .passingPrcnt(UPDATED_PASSING_PRCNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTested.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTested))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tested in the database
        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeUpdate);
        Tested testTested = testedList.get(testedList.size() - 1);
        assertThat(testTested.getTestName()).isEqualTo(DEFAULT_TEST_NAME);
        assertThat(testTested.getTimeToComplete()).isEqualTo(UPDATED_TIME_TO_COMPLETE);
        assertThat(testTested.getTotalQuestions()).isEqualTo(UPDATED_TOTAL_QUESTIONS);
        assertThat(testTested.getRandomize()).isEqualTo(UPDATED_RANDOMIZE);
        assertThat(testTested.getPassingPrcnt()).isEqualTo(UPDATED_PASSING_PRCNT);
        assertThat(testTested.getExpiryMonths()).isEqualTo(DEFAULT_EXPIRY_MONTHS);
    }

    @Test
    void fullUpdateTestedWithPatch() throws Exception {
        // Initialize the database
        testedRepository.save(tested).block();

        int databaseSizeBeforeUpdate = testedRepository.findAll().collectList().block().size();

        // Update the tested using partial update
        Tested partialUpdatedTested = new Tested();
        partialUpdatedTested.setId(tested.getId());

        partialUpdatedTested
            .testName(UPDATED_TEST_NAME)
            .timeToComplete(UPDATED_TIME_TO_COMPLETE)
            .totalQuestions(UPDATED_TOTAL_QUESTIONS)
            .randomize(UPDATED_RANDOMIZE)
            .passingPrcnt(UPDATED_PASSING_PRCNT)
            .expiryMonths(UPDATED_EXPIRY_MONTHS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTested.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTested))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tested in the database
        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeUpdate);
        Tested testTested = testedList.get(testedList.size() - 1);
        assertThat(testTested.getTestName()).isEqualTo(UPDATED_TEST_NAME);
        assertThat(testTested.getTimeToComplete()).isEqualTo(UPDATED_TIME_TO_COMPLETE);
        assertThat(testTested.getTotalQuestions()).isEqualTo(UPDATED_TOTAL_QUESTIONS);
        assertThat(testTested.getRandomize()).isEqualTo(UPDATED_RANDOMIZE);
        assertThat(testTested.getPassingPrcnt()).isEqualTo(UPDATED_PASSING_PRCNT);
        assertThat(testTested.getExpiryMonths()).isEqualTo(UPDATED_EXPIRY_MONTHS);
    }

    @Test
    void patchNonExistingTested() throws Exception {
        int databaseSizeBeforeUpdate = testedRepository.findAll().collectList().block().size();
        tested.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tested.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tested))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tested in the database
        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTested() throws Exception {
        int databaseSizeBeforeUpdate = testedRepository.findAll().collectList().block().size();
        tested.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tested))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tested in the database
        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTested() throws Exception {
        int databaseSizeBeforeUpdate = testedRepository.findAll().collectList().block().size();
        tested.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tested))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tested in the database
        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTested() {
        // Initialize the database
        testedRepository.save(tested).block();

        int databaseSizeBeforeDelete = testedRepository.findAll().collectList().block().size();

        // Delete the tested
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tested.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Tested> testedList = testedRepository.findAll().collectList().block();
        assertThat(testedList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
