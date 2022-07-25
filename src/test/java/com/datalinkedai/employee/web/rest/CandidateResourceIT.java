package com.datalinkedai.employee.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datalinkedai.employee.IntegrationTest;
import com.datalinkedai.employee.domain.Candidate;
import com.datalinkedai.employee.domain.enumeration.Status;
import com.datalinkedai.employee.repository.CandidateRepository;
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
 * Integration tests for the {@link CandidateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CandidateResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "0488846003";
    private static final String UPDATED_PHONE_NUMBER = "7085993922";

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EDUCTION_QUALIFICATION = "AAAAAAAAAA";
    private static final String UPDATED_EDUCTION_QUALIFICATION = "BBBBBBBBBB";

    private static final String DEFAULT_RESUME_LINK = "AAAAAAAAAA";
    private static final String UPDATED_RESUME_LINK = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.INACTIVE;

    private static final String ENTITY_API_URL = "/api/candidates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Candidate candidate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidate createEntity() {
        Candidate candidate = new Candidate()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .userName(DEFAULT_USER_NAME)
            .eductionQualification(DEFAULT_EDUCTION_QUALIFICATION)
            .resumeLink(DEFAULT_RESUME_LINK)
            .status(DEFAULT_STATUS);
        return candidate;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidate createUpdatedEntity() {
        Candidate candidate = new Candidate()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .userName(UPDATED_USER_NAME)
            .eductionQualification(UPDATED_EDUCTION_QUALIFICATION)
            .resumeLink(UPDATED_RESUME_LINK)
            .status(UPDATED_STATUS);
        return candidate;
    }

    @BeforeEach
    public void initTest() {
        candidateRepository.deleteAll().block();
        candidate = createEntity();
    }

    @Test
    void createCandidate() throws Exception {
        int databaseSizeBeforeCreate = candidateRepository.findAll().collectList().block().size();
        // Create the Candidate
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(candidate))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeCreate + 1);
        Candidate testCandidate = candidateList.get(candidateList.size() - 1);
        assertThat(testCandidate.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testCandidate.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCandidate.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testCandidate.getUserName()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testCandidate.getEductionQualification()).isEqualTo(DEFAULT_EDUCTION_QUALIFICATION);
        assertThat(testCandidate.getResumeLink()).isEqualTo(DEFAULT_RESUME_LINK);
        assertThat(testCandidate.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createCandidateWithExistingId() throws Exception {
        // Create the Candidate with an existing ID
        candidate.setId("existing_id");

        int databaseSizeBeforeCreate = candidateRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(candidate))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = candidateRepository.findAll().collectList().block().size();
        // set the field null
        candidate.setFirstName(null);

        // Create the Candidate, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(candidate))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = candidateRepository.findAll().collectList().block().size();
        // set the field null
        candidate.setLastName(null);

        // Create the Candidate, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(candidate))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = candidateRepository.findAll().collectList().block().size();
        // set the field null
        candidate.setPhoneNumber(null);

        // Create the Candidate, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(candidate))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUserNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = candidateRepository.findAll().collectList().block().size();
        // set the field null
        candidate.setUserName(null);

        // Create the Candidate, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(candidate))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCandidatesAsStream() {
        // Initialize the database
        candidateRepository.save(candidate).block();

        List<Candidate> candidateList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Candidate.class)
            .getResponseBody()
            .filter(candidate::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(candidateList).isNotNull();
        assertThat(candidateList).hasSize(1);
        Candidate testCandidate = candidateList.get(0);
        assertThat(testCandidate.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testCandidate.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCandidate.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testCandidate.getUserName()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testCandidate.getEductionQualification()).isEqualTo(DEFAULT_EDUCTION_QUALIFICATION);
        assertThat(testCandidate.getResumeLink()).isEqualTo(DEFAULT_RESUME_LINK);
        assertThat(testCandidate.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void getAllCandidates() {
        // Initialize the database
        candidateRepository.save(candidate).block();

        // Get all the candidateList
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
            .value(hasItem(candidate.getId()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].phoneNumber")
            .value(hasItem(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.[*].userName")
            .value(hasItem(DEFAULT_USER_NAME))
            .jsonPath("$.[*].eductionQualification")
            .value(hasItem(DEFAULT_EDUCTION_QUALIFICATION))
            .jsonPath("$.[*].resumeLink")
            .value(hasItem(DEFAULT_RESUME_LINK))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getCandidate() {
        // Initialize the database
        candidateRepository.save(candidate).block();

        // Get the candidate
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, candidate.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(candidate.getId()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.phoneNumber")
            .value(is(DEFAULT_PHONE_NUMBER))
            .jsonPath("$.userName")
            .value(is(DEFAULT_USER_NAME))
            .jsonPath("$.eductionQualification")
            .value(is(DEFAULT_EDUCTION_QUALIFICATION))
            .jsonPath("$.resumeLink")
            .value(is(DEFAULT_RESUME_LINK))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingCandidate() {
        // Get the candidate
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCandidate() throws Exception {
        // Initialize the database
        candidateRepository.save(candidate).block();

        int databaseSizeBeforeUpdate = candidateRepository.findAll().collectList().block().size();

        // Update the candidate
        Candidate updatedCandidate = candidateRepository.findById(candidate.getId()).block();
        updatedCandidate
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .userName(UPDATED_USER_NAME)
            .eductionQualification(UPDATED_EDUCTION_QUALIFICATION)
            .resumeLink(UPDATED_RESUME_LINK)
            .status(UPDATED_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCandidate.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCandidate))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
        Candidate testCandidate = candidateList.get(candidateList.size() - 1);
        assertThat(testCandidate.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCandidate.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testCandidate.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testCandidate.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testCandidate.getEductionQualification()).isEqualTo(UPDATED_EDUCTION_QUALIFICATION);
        assertThat(testCandidate.getResumeLink()).isEqualTo(UPDATED_RESUME_LINK);
        assertThat(testCandidate.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingCandidate() throws Exception {
        int databaseSizeBeforeUpdate = candidateRepository.findAll().collectList().block().size();
        candidate.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, candidate.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(candidate))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCandidate() throws Exception {
        int databaseSizeBeforeUpdate = candidateRepository.findAll().collectList().block().size();
        candidate.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(candidate))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCandidate() throws Exception {
        int databaseSizeBeforeUpdate = candidateRepository.findAll().collectList().block().size();
        candidate.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(candidate))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCandidateWithPatch() throws Exception {
        // Initialize the database
        candidateRepository.save(candidate).block();

        int databaseSizeBeforeUpdate = candidateRepository.findAll().collectList().block().size();

        // Update the candidate using partial update
        Candidate partialUpdatedCandidate = new Candidate();
        partialUpdatedCandidate.setId(candidate.getId());

        partialUpdatedCandidate
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .userName(UPDATED_USER_NAME)
            .eductionQualification(UPDATED_EDUCTION_QUALIFICATION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCandidate.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCandidate))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
        Candidate testCandidate = candidateList.get(candidateList.size() - 1);
        assertThat(testCandidate.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCandidate.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testCandidate.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testCandidate.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testCandidate.getEductionQualification()).isEqualTo(UPDATED_EDUCTION_QUALIFICATION);
        assertThat(testCandidate.getResumeLink()).isEqualTo(DEFAULT_RESUME_LINK);
        assertThat(testCandidate.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void fullUpdateCandidateWithPatch() throws Exception {
        // Initialize the database
        candidateRepository.save(candidate).block();

        int databaseSizeBeforeUpdate = candidateRepository.findAll().collectList().block().size();

        // Update the candidate using partial update
        Candidate partialUpdatedCandidate = new Candidate();
        partialUpdatedCandidate.setId(candidate.getId());

        partialUpdatedCandidate
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .userName(UPDATED_USER_NAME)
            .eductionQualification(UPDATED_EDUCTION_QUALIFICATION)
            .resumeLink(UPDATED_RESUME_LINK)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCandidate.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCandidate))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
        Candidate testCandidate = candidateList.get(candidateList.size() - 1);
        assertThat(testCandidate.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCandidate.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testCandidate.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testCandidate.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testCandidate.getEductionQualification()).isEqualTo(UPDATED_EDUCTION_QUALIFICATION);
        assertThat(testCandidate.getResumeLink()).isEqualTo(UPDATED_RESUME_LINK);
        assertThat(testCandidate.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingCandidate() throws Exception {
        int databaseSizeBeforeUpdate = candidateRepository.findAll().collectList().block().size();
        candidate.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, candidate.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(candidate))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCandidate() throws Exception {
        int databaseSizeBeforeUpdate = candidateRepository.findAll().collectList().block().size();
        candidate.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(candidate))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCandidate() throws Exception {
        int databaseSizeBeforeUpdate = candidateRepository.findAll().collectList().block().size();
        candidate.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(candidate))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCandidate() {
        // Initialize the database
        candidateRepository.save(candidate).block();

        int databaseSizeBeforeDelete = candidateRepository.findAll().collectList().block().size();

        // Delete the candidate
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, candidate.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Candidate> candidateList = candidateRepository.findAll().collectList().block();
        assertThat(candidateList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
