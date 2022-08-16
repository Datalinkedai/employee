package com.datalinkedai.employee.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datalinkedai.employee.IntegrationTest;
import com.datalinkedai.employee.domain.Interview;
import com.datalinkedai.employee.domain.enumeration.InterviewStatus;
import com.datalinkedai.employee.repository.InterviewRepository;
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
 * Integration tests for the {@link InterviewResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InterviewResourceIT {

    private static final String DEFAULT_INTERVIEW_NAME = "AAAAAAAAAA";
    private static final String UPDATED_INTERVIEW_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_SCHEDULED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SCHEDULED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_RESCEDULED = 1;
    private static final Integer UPDATED_RESCEDULED = 2;

    private static final LocalDate DEFAULT_RESCHEDULE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RESCHEDULE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Instant DEFAULT_RESCHEDULE_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESCHEDULE_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_RESCHEDULE_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESCHEDULE_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_RESCHEDULE_APPROVED = false;
    private static final Boolean UPDATED_RESCHEDULE_APPROVED = true;

    private static final InterviewStatus DEFAULT_INTERVIEW_STATUS = InterviewStatus.ACCEPTED;
    private static final InterviewStatus UPDATED_INTERVIEW_STATUS = InterviewStatus.REJECTED;

    private static final String ENTITY_API_URL = "/api/interviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Interview interview;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Interview createEntity() {
        Interview interview = new Interview()
            .interviewName(DEFAULT_INTERVIEW_NAME)
            .scheduledDate(DEFAULT_SCHEDULED_DATE)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .resceduled(DEFAULT_RESCEDULED)
            .rescheduleDate(DEFAULT_RESCHEDULE_DATE)
            .rescheduleStartTime(DEFAULT_RESCHEDULE_START_TIME)
            .rescheduleEndTime(DEFAULT_RESCHEDULE_END_TIME)
            .rescheduleApproved(DEFAULT_RESCHEDULE_APPROVED)
            .interviewStatus(DEFAULT_INTERVIEW_STATUS);
        return interview;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Interview createUpdatedEntity() {
        Interview interview = new Interview()
            .interviewName(UPDATED_INTERVIEW_NAME)
            .scheduledDate(UPDATED_SCHEDULED_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .resceduled(UPDATED_RESCEDULED)
            .rescheduleDate(UPDATED_RESCHEDULE_DATE)
            .rescheduleStartTime(UPDATED_RESCHEDULE_START_TIME)
            .rescheduleEndTime(UPDATED_RESCHEDULE_END_TIME)
            .rescheduleApproved(UPDATED_RESCHEDULE_APPROVED)
            .interviewStatus(UPDATED_INTERVIEW_STATUS);
        return interview;
    }

    @BeforeEach
    public void initTest() {
        interviewRepository.deleteAll().block();
        interview = createEntity();
    }

    @Test
    void createInterview() throws Exception {
        int databaseSizeBeforeCreate = interviewRepository.findAll().collectList().block().size();
        // Create the Interview
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interview))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Interview in the database
        List<Interview> interviewList = interviewRepository.findAll().collectList().block();
        assertThat(interviewList).hasSize(databaseSizeBeforeCreate + 1);
        Interview testInterview = interviewList.get(interviewList.size() - 1);
        assertThat(testInterview.getInterviewName()).isEqualTo(DEFAULT_INTERVIEW_NAME);
        assertThat(testInterview.getScheduledDate()).isEqualTo(DEFAULT_SCHEDULED_DATE);
        assertThat(testInterview.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testInterview.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testInterview.getResceduled()).isEqualTo(DEFAULT_RESCEDULED);
        assertThat(testInterview.getRescheduleDate()).isEqualTo(DEFAULT_RESCHEDULE_DATE);
        assertThat(testInterview.getRescheduleStartTime()).isEqualTo(DEFAULT_RESCHEDULE_START_TIME);
        assertThat(testInterview.getRescheduleEndTime()).isEqualTo(DEFAULT_RESCHEDULE_END_TIME);
        assertThat(testInterview.getRescheduleApproved()).isEqualTo(DEFAULT_RESCHEDULE_APPROVED);
        assertThat(testInterview.getInterviewStatus()).isEqualTo(DEFAULT_INTERVIEW_STATUS);
    }

    @Test
    void createInterviewWithExistingId() throws Exception {
        // Create the Interview with an existing ID
        interview.setId("existing_id");

        int databaseSizeBeforeCreate = interviewRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interview))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interview in the database
        List<Interview> interviewList = interviewRepository.findAll().collectList().block();
        assertThat(interviewList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkScheduledDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = interviewRepository.findAll().collectList().block().size();
        // set the field null
        interview.setScheduledDate(null);

        // Create the Interview, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interview))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Interview> interviewList = interviewRepository.findAll().collectList().block();
        assertThat(interviewList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllInterviewsAsStream() {
        // Initialize the database
        interviewRepository.save(interview).block();

        List<Interview> interviewList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Interview.class)
            .getResponseBody()
            .filter(interview::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(interviewList).isNotNull();
        assertThat(interviewList).hasSize(1);
        Interview testInterview = interviewList.get(0);
        assertThat(testInterview.getInterviewName()).isEqualTo(DEFAULT_INTERVIEW_NAME);
        assertThat(testInterview.getScheduledDate()).isEqualTo(DEFAULT_SCHEDULED_DATE);
        assertThat(testInterview.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testInterview.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testInterview.getResceduled()).isEqualTo(DEFAULT_RESCEDULED);
        assertThat(testInterview.getRescheduleDate()).isEqualTo(DEFAULT_RESCHEDULE_DATE);
        assertThat(testInterview.getRescheduleStartTime()).isEqualTo(DEFAULT_RESCHEDULE_START_TIME);
        assertThat(testInterview.getRescheduleEndTime()).isEqualTo(DEFAULT_RESCHEDULE_END_TIME);
        assertThat(testInterview.getRescheduleApproved()).isEqualTo(DEFAULT_RESCHEDULE_APPROVED);
        assertThat(testInterview.getInterviewStatus()).isEqualTo(DEFAULT_INTERVIEW_STATUS);
    }

    @Test
    void getAllInterviews() {
        // Initialize the database
        interviewRepository.save(interview).block();

        // Get all the interviewList
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
            .value(hasItem(interview.getId()))
            .jsonPath("$.[*].interviewName")
            .value(hasItem(DEFAULT_INTERVIEW_NAME))
            .jsonPath("$.[*].scheduledDate")
            .value(hasItem(DEFAULT_SCHEDULED_DATE.toString()))
            .jsonPath("$.[*].startTime")
            .value(hasItem(DEFAULT_START_TIME.toString()))
            .jsonPath("$.[*].endTime")
            .value(hasItem(DEFAULT_END_TIME.toString()))
            .jsonPath("$.[*].resceduled")
            .value(hasItem(DEFAULT_RESCEDULED))
            .jsonPath("$.[*].rescheduleDate")
            .value(hasItem(DEFAULT_RESCHEDULE_DATE.toString()))
            .jsonPath("$.[*].rescheduleStartTime")
            .value(hasItem(DEFAULT_RESCHEDULE_START_TIME.toString()))
            .jsonPath("$.[*].rescheduleEndTime")
            .value(hasItem(DEFAULT_RESCHEDULE_END_TIME.toString()))
            .jsonPath("$.[*].rescheduleApproved")
            .value(hasItem(DEFAULT_RESCHEDULE_APPROVED.booleanValue()))
            .jsonPath("$.[*].interviewStatus")
            .value(hasItem(DEFAULT_INTERVIEW_STATUS.toString()));
    }

    @Test
    void getInterview() {
        // Initialize the database
        interviewRepository.save(interview).block();

        // Get the interview
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, interview.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(interview.getId()))
            .jsonPath("$.interviewName")
            .value(is(DEFAULT_INTERVIEW_NAME))
            .jsonPath("$.scheduledDate")
            .value(is(DEFAULT_SCHEDULED_DATE.toString()))
            .jsonPath("$.startTime")
            .value(is(DEFAULT_START_TIME.toString()))
            .jsonPath("$.endTime")
            .value(is(DEFAULT_END_TIME.toString()))
            .jsonPath("$.resceduled")
            .value(is(DEFAULT_RESCEDULED))
            .jsonPath("$.rescheduleDate")
            .value(is(DEFAULT_RESCHEDULE_DATE.toString()))
            .jsonPath("$.rescheduleStartTime")
            .value(is(DEFAULT_RESCHEDULE_START_TIME.toString()))
            .jsonPath("$.rescheduleEndTime")
            .value(is(DEFAULT_RESCHEDULE_END_TIME.toString()))
            .jsonPath("$.rescheduleApproved")
            .value(is(DEFAULT_RESCHEDULE_APPROVED.booleanValue()))
            .jsonPath("$.interviewStatus")
            .value(is(DEFAULT_INTERVIEW_STATUS.toString()));
    }

    @Test
    void getNonExistingInterview() {
        // Get the interview
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewInterview() throws Exception {
        // Initialize the database
        interviewRepository.save(interview).block();

        int databaseSizeBeforeUpdate = interviewRepository.findAll().collectList().block().size();

        // Update the interview
        Interview updatedInterview = interviewRepository.findById(interview.getId()).block();
        updatedInterview
            .interviewName(UPDATED_INTERVIEW_NAME)
            .scheduledDate(UPDATED_SCHEDULED_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .resceduled(UPDATED_RESCEDULED)
            .rescheduleDate(UPDATED_RESCHEDULE_DATE)
            .rescheduleStartTime(UPDATED_RESCHEDULE_START_TIME)
            .rescheduleEndTime(UPDATED_RESCHEDULE_END_TIME)
            .rescheduleApproved(UPDATED_RESCHEDULE_APPROVED)
            .interviewStatus(UPDATED_INTERVIEW_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedInterview.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedInterview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Interview in the database
        List<Interview> interviewList = interviewRepository.findAll().collectList().block();
        assertThat(interviewList).hasSize(databaseSizeBeforeUpdate);
        Interview testInterview = interviewList.get(interviewList.size() - 1);
        assertThat(testInterview.getInterviewName()).isEqualTo(UPDATED_INTERVIEW_NAME);
        assertThat(testInterview.getScheduledDate()).isEqualTo(UPDATED_SCHEDULED_DATE);
        assertThat(testInterview.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testInterview.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testInterview.getResceduled()).isEqualTo(UPDATED_RESCEDULED);
        assertThat(testInterview.getRescheduleDate()).isEqualTo(UPDATED_RESCHEDULE_DATE);
        assertThat(testInterview.getRescheduleStartTime()).isEqualTo(UPDATED_RESCHEDULE_START_TIME);
        assertThat(testInterview.getRescheduleEndTime()).isEqualTo(UPDATED_RESCHEDULE_END_TIME);
        assertThat(testInterview.getRescheduleApproved()).isEqualTo(UPDATED_RESCHEDULE_APPROVED);
        assertThat(testInterview.getInterviewStatus()).isEqualTo(UPDATED_INTERVIEW_STATUS);
    }

    @Test
    void putNonExistingInterview() throws Exception {
        int databaseSizeBeforeUpdate = interviewRepository.findAll().collectList().block().size();
        interview.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, interview.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interview))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interview in the database
        List<Interview> interviewList = interviewRepository.findAll().collectList().block();
        assertThat(interviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInterview() throws Exception {
        int databaseSizeBeforeUpdate = interviewRepository.findAll().collectList().block().size();
        interview.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interview))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interview in the database
        List<Interview> interviewList = interviewRepository.findAll().collectList().block();
        assertThat(interviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInterview() throws Exception {
        int databaseSizeBeforeUpdate = interviewRepository.findAll().collectList().block().size();
        interview.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(interview))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Interview in the database
        List<Interview> interviewList = interviewRepository.findAll().collectList().block();
        assertThat(interviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInterviewWithPatch() throws Exception {
        // Initialize the database
        interviewRepository.save(interview).block();

        int databaseSizeBeforeUpdate = interviewRepository.findAll().collectList().block().size();

        // Update the interview using partial update
        Interview partialUpdatedInterview = new Interview();
        partialUpdatedInterview.setId(interview.getId());

        partialUpdatedInterview
            .scheduledDate(UPDATED_SCHEDULED_DATE)
            .startTime(UPDATED_START_TIME)
            .rescheduleDate(UPDATED_RESCHEDULE_DATE)
            .rescheduleStartTime(UPDATED_RESCHEDULE_START_TIME)
            .rescheduleApproved(UPDATED_RESCHEDULE_APPROVED)
            .interviewStatus(UPDATED_INTERVIEW_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInterview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedInterview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Interview in the database
        List<Interview> interviewList = interviewRepository.findAll().collectList().block();
        assertThat(interviewList).hasSize(databaseSizeBeforeUpdate);
        Interview testInterview = interviewList.get(interviewList.size() - 1);
        assertThat(testInterview.getInterviewName()).isEqualTo(DEFAULT_INTERVIEW_NAME);
        assertThat(testInterview.getScheduledDate()).isEqualTo(UPDATED_SCHEDULED_DATE);
        assertThat(testInterview.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testInterview.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testInterview.getResceduled()).isEqualTo(DEFAULT_RESCEDULED);
        assertThat(testInterview.getRescheduleDate()).isEqualTo(UPDATED_RESCHEDULE_DATE);
        assertThat(testInterview.getRescheduleStartTime()).isEqualTo(UPDATED_RESCHEDULE_START_TIME);
        assertThat(testInterview.getRescheduleEndTime()).isEqualTo(DEFAULT_RESCHEDULE_END_TIME);
        assertThat(testInterview.getRescheduleApproved()).isEqualTo(UPDATED_RESCHEDULE_APPROVED);
        assertThat(testInterview.getInterviewStatus()).isEqualTo(UPDATED_INTERVIEW_STATUS);
    }

    @Test
    void fullUpdateInterviewWithPatch() throws Exception {
        // Initialize the database
        interviewRepository.save(interview).block();

        int databaseSizeBeforeUpdate = interviewRepository.findAll().collectList().block().size();

        // Update the interview using partial update
        Interview partialUpdatedInterview = new Interview();
        partialUpdatedInterview.setId(interview.getId());

        partialUpdatedInterview
            .interviewName(UPDATED_INTERVIEW_NAME)
            .scheduledDate(UPDATED_SCHEDULED_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .resceduled(UPDATED_RESCEDULED)
            .rescheduleDate(UPDATED_RESCHEDULE_DATE)
            .rescheduleStartTime(UPDATED_RESCHEDULE_START_TIME)
            .rescheduleEndTime(UPDATED_RESCHEDULE_END_TIME)
            .rescheduleApproved(UPDATED_RESCHEDULE_APPROVED)
            .interviewStatus(UPDATED_INTERVIEW_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInterview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedInterview))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Interview in the database
        List<Interview> interviewList = interviewRepository.findAll().collectList().block();
        assertThat(interviewList).hasSize(databaseSizeBeforeUpdate);
        Interview testInterview = interviewList.get(interviewList.size() - 1);
        assertThat(testInterview.getInterviewName()).isEqualTo(UPDATED_INTERVIEW_NAME);
        assertThat(testInterview.getScheduledDate()).isEqualTo(UPDATED_SCHEDULED_DATE);
        assertThat(testInterview.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testInterview.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testInterview.getResceduled()).isEqualTo(UPDATED_RESCEDULED);
        assertThat(testInterview.getRescheduleDate()).isEqualTo(UPDATED_RESCHEDULE_DATE);
        assertThat(testInterview.getRescheduleStartTime()).isEqualTo(UPDATED_RESCHEDULE_START_TIME);
        assertThat(testInterview.getRescheduleEndTime()).isEqualTo(UPDATED_RESCHEDULE_END_TIME);
        assertThat(testInterview.getRescheduleApproved()).isEqualTo(UPDATED_RESCHEDULE_APPROVED);
        assertThat(testInterview.getInterviewStatus()).isEqualTo(UPDATED_INTERVIEW_STATUS);
    }

    @Test
    void patchNonExistingInterview() throws Exception {
        int databaseSizeBeforeUpdate = interviewRepository.findAll().collectList().block().size();
        interview.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, interview.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(interview))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interview in the database
        List<Interview> interviewList = interviewRepository.findAll().collectList().block();
        assertThat(interviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInterview() throws Exception {
        int databaseSizeBeforeUpdate = interviewRepository.findAll().collectList().block().size();
        interview.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(interview))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Interview in the database
        List<Interview> interviewList = interviewRepository.findAll().collectList().block();
        assertThat(interviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInterview() throws Exception {
        int databaseSizeBeforeUpdate = interviewRepository.findAll().collectList().block().size();
        interview.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(interview))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Interview in the database
        List<Interview> interviewList = interviewRepository.findAll().collectList().block();
        assertThat(interviewList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInterview() {
        // Initialize the database
        interviewRepository.save(interview).block();

        int databaseSizeBeforeDelete = interviewRepository.findAll().collectList().block().size();

        // Delete the interview
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, interview.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Interview> interviewList = interviewRepository.findAll().collectList().block();
        assertThat(interviewList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
