package com.datalinkedai.employee.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datalinkedai.employee.IntegrationTest;
import com.datalinkedai.employee.domain.Training;
import com.datalinkedai.employee.repository.TrainingRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link TrainingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TrainingResourceIT {

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_REPEATS = false;
    private static final Boolean UPDATED_REPEATS = true;

    private static final String ENTITY_API_URL = "/api/trainings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Training training;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Training createEntity() {
        Training training = new Training()
            .startDate(DEFAULT_START_DATE)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .endDate(DEFAULT_END_DATE)
            .type(DEFAULT_TYPE)
            .repeats(DEFAULT_REPEATS);
        return training;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Training createUpdatedEntity() {
        Training training = new Training()
            .startDate(UPDATED_START_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .endDate(UPDATED_END_DATE)
            .type(UPDATED_TYPE)
            .repeats(UPDATED_REPEATS);
        return training;
    }

    @BeforeEach
    public void initTest() {
        trainingRepository.deleteAll().block();
        training = createEntity();
    }

    @Test
    void createTraining() throws Exception {
        int databaseSizeBeforeCreate = trainingRepository.findAll().collectList().block().size();
        // Create the Training
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(training))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Training in the database
        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeCreate + 1);
        Training testTraining = trainingList.get(trainingList.size() - 1);
        assertThat(testTraining.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testTraining.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testTraining.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testTraining.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testTraining.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTraining.getRepeats()).isEqualTo(DEFAULT_REPEATS);
    }

    @Test
    void createTrainingWithExistingId() throws Exception {
        // Create the Training with an existing ID
        training.setId("existing_id");

        int databaseSizeBeforeCreate = trainingRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(training))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Training in the database
        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = trainingRepository.findAll().collectList().block().size();
        // set the field null
        training.setStartDate(null);

        // Create the Training, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(training))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkEndDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = trainingRepository.findAll().collectList().block().size();
        // set the field null
        training.setEndDate(null);

        // Create the Training, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(training))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllTrainingsAsStream() {
        // Initialize the database
        trainingRepository.save(training).block();

        List<Training> trainingList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Training.class)
            .getResponseBody()
            .filter(training::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(trainingList).isNotNull();
        assertThat(trainingList).hasSize(1);
        Training testTraining = trainingList.get(0);
        assertThat(testTraining.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testTraining.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testTraining.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testTraining.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testTraining.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTraining.getRepeats()).isEqualTo(DEFAULT_REPEATS);
    }

    @Test
    void getAllTrainings() {
        // Initialize the database
        trainingRepository.save(training).block();

        // Get all the trainingList
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
            .value(hasItem(training.getId()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].startTime")
            .value(hasItem(DEFAULT_START_TIME.toString()))
            .jsonPath("$.[*].endTime")
            .value(hasItem(DEFAULT_END_TIME.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE))
            .jsonPath("$.[*].repeats")
            .value(hasItem(DEFAULT_REPEATS.booleanValue()));
    }

    @Test
    void getTraining() {
        // Initialize the database
        trainingRepository.save(training).block();

        // Get the training
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, training.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(training.getId()))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.startTime")
            .value(is(DEFAULT_START_TIME.toString()))
            .jsonPath("$.endTime")
            .value(is(DEFAULT_END_TIME.toString()))
            .jsonPath("$.endDate")
            .value(is(DEFAULT_END_DATE.toString()))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE))
            .jsonPath("$.repeats")
            .value(is(DEFAULT_REPEATS.booleanValue()));
    }

    @Test
    void getNonExistingTraining() {
        // Get the training
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTraining() throws Exception {
        // Initialize the database
        trainingRepository.save(training).block();

        int databaseSizeBeforeUpdate = trainingRepository.findAll().collectList().block().size();

        // Update the training
        Training updatedTraining = trainingRepository.findById(training.getId()).block();
        updatedTraining
            .startDate(UPDATED_START_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .endDate(UPDATED_END_DATE)
            .type(UPDATED_TYPE)
            .repeats(UPDATED_REPEATS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTraining.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTraining))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Training in the database
        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeUpdate);
        Training testTraining = trainingList.get(trainingList.size() - 1);
        assertThat(testTraining.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTraining.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testTraining.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testTraining.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testTraining.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTraining.getRepeats()).isEqualTo(UPDATED_REPEATS);
    }

    @Test
    void putNonExistingTraining() throws Exception {
        int databaseSizeBeforeUpdate = trainingRepository.findAll().collectList().block().size();
        training.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, training.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(training))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Training in the database
        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTraining() throws Exception {
        int databaseSizeBeforeUpdate = trainingRepository.findAll().collectList().block().size();
        training.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(training))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Training in the database
        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTraining() throws Exception {
        int databaseSizeBeforeUpdate = trainingRepository.findAll().collectList().block().size();
        training.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(training))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Training in the database
        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTrainingWithPatch() throws Exception {
        // Initialize the database
        trainingRepository.save(training).block();

        int databaseSizeBeforeUpdate = trainingRepository.findAll().collectList().block().size();

        // Update the training using partial update
        Training partialUpdatedTraining = new Training();
        partialUpdatedTraining.setId(training.getId());

        partialUpdatedTraining.startDate(UPDATED_START_DATE).startTime(UPDATED_START_TIME).endDate(UPDATED_END_DATE).type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTraining.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTraining))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Training in the database
        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeUpdate);
        Training testTraining = trainingList.get(trainingList.size() - 1);
        assertThat(testTraining.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTraining.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testTraining.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testTraining.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testTraining.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTraining.getRepeats()).isEqualTo(DEFAULT_REPEATS);
    }

    @Test
    void fullUpdateTrainingWithPatch() throws Exception {
        // Initialize the database
        trainingRepository.save(training).block();

        int databaseSizeBeforeUpdate = trainingRepository.findAll().collectList().block().size();

        // Update the training using partial update
        Training partialUpdatedTraining = new Training();
        partialUpdatedTraining.setId(training.getId());

        partialUpdatedTraining
            .startDate(UPDATED_START_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .endDate(UPDATED_END_DATE)
            .type(UPDATED_TYPE)
            .repeats(UPDATED_REPEATS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTraining.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTraining))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Training in the database
        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeUpdate);
        Training testTraining = trainingList.get(trainingList.size() - 1);
        assertThat(testTraining.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTraining.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testTraining.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testTraining.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testTraining.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTraining.getRepeats()).isEqualTo(UPDATED_REPEATS);
    }

    @Test
    void patchNonExistingTraining() throws Exception {
        int databaseSizeBeforeUpdate = trainingRepository.findAll().collectList().block().size();
        training.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, training.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(training))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Training in the database
        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTraining() throws Exception {
        int databaseSizeBeforeUpdate = trainingRepository.findAll().collectList().block().size();
        training.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(training))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Training in the database
        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTraining() throws Exception {
        int databaseSizeBeforeUpdate = trainingRepository.findAll().collectList().block().size();
        training.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(training))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Training in the database
        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTraining() {
        // Initialize the database
        trainingRepository.save(training).block();

        int databaseSizeBeforeDelete = trainingRepository.findAll().collectList().block().size();

        // Delete the training
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, training.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Training> trainingList = trainingRepository.findAll().collectList().block();
        assertThat(trainingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
