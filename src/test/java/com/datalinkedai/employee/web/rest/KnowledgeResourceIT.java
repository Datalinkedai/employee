package com.datalinkedai.employee.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datalinkedai.employee.IntegrationTest;
import com.datalinkedai.employee.domain.Knowledge;
import com.datalinkedai.employee.repository.KnowledgeRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link KnowledgeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class KnowledgeResourceIT {

    private static final Double DEFAULT_RESULT = 0D;
    private static final Double UPDATED_RESULT = 1D;

    private static final Instant DEFAULT_TEST_TAKEN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TEST_TAKEN = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final byte[] DEFAULT_CERTIFICATE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CERTIFICATE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_CERTIFICATE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CERTIFICATE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/knowledges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private KnowledgeRepository knowledgeRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Knowledge knowledge;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Knowledge createEntity() {
        Knowledge knowledge = new Knowledge()
            .result(DEFAULT_RESULT)
            .testTaken(DEFAULT_TEST_TAKEN)
            .certificate(DEFAULT_CERTIFICATE)
            .certificateContentType(DEFAULT_CERTIFICATE_CONTENT_TYPE);
        return knowledge;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Knowledge createUpdatedEntity() {
        Knowledge knowledge = new Knowledge()
            .result(UPDATED_RESULT)
            .testTaken(UPDATED_TEST_TAKEN)
            .certificate(UPDATED_CERTIFICATE)
            .certificateContentType(UPDATED_CERTIFICATE_CONTENT_TYPE);
        return knowledge;
    }

    @BeforeEach
    public void initTest() {
        knowledgeRepository.deleteAll().block();
        knowledge = createEntity();
    }

    @Test
    void createKnowledge() throws Exception {
        int databaseSizeBeforeCreate = knowledgeRepository.findAll().collectList().block().size();
        // Create the Knowledge
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledge))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Knowledge in the database
        List<Knowledge> knowledgeList = knowledgeRepository.findAll().collectList().block();
        assertThat(knowledgeList).hasSize(databaseSizeBeforeCreate + 1);
        Knowledge testKnowledge = knowledgeList.get(knowledgeList.size() - 1);
        assertThat(testKnowledge.getResult()).isEqualTo(DEFAULT_RESULT);
        assertThat(testKnowledge.getTestTaken()).isEqualTo(DEFAULT_TEST_TAKEN);
        assertThat(testKnowledge.getCertificate()).isEqualTo(DEFAULT_CERTIFICATE);
        assertThat(testKnowledge.getCertificateContentType()).isEqualTo(DEFAULT_CERTIFICATE_CONTENT_TYPE);
    }

    @Test
    void createKnowledgeWithExistingId() throws Exception {
        // Create the Knowledge with an existing ID
        knowledge.setId("existing_id");

        int databaseSizeBeforeCreate = knowledgeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledge))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Knowledge in the database
        List<Knowledge> knowledgeList = knowledgeRepository.findAll().collectList().block();
        assertThat(knowledgeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTestTakenIsRequired() throws Exception {
        int databaseSizeBeforeTest = knowledgeRepository.findAll().collectList().block().size();
        // set the field null
        knowledge.setTestTaken(null);

        // Create the Knowledge, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledge))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Knowledge> knowledgeList = knowledgeRepository.findAll().collectList().block();
        assertThat(knowledgeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllKnowledgesAsStream() {
        // Initialize the database
        knowledgeRepository.save(knowledge).block();

        List<Knowledge> knowledgeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Knowledge.class)
            .getResponseBody()
            .filter(knowledge::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(knowledgeList).isNotNull();
        assertThat(knowledgeList).hasSize(1);
        Knowledge testKnowledge = knowledgeList.get(0);
        assertThat(testKnowledge.getResult()).isEqualTo(DEFAULT_RESULT);
        assertThat(testKnowledge.getTestTaken()).isEqualTo(DEFAULT_TEST_TAKEN);
        assertThat(testKnowledge.getCertificate()).isEqualTo(DEFAULT_CERTIFICATE);
        assertThat(testKnowledge.getCertificateContentType()).isEqualTo(DEFAULT_CERTIFICATE_CONTENT_TYPE);
    }

    @Test
    void getAllKnowledges() {
        // Initialize the database
        knowledgeRepository.save(knowledge).block();

        // Get all the knowledgeList
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
            .value(hasItem(knowledge.getId()))
            .jsonPath("$.[*].result")
            .value(hasItem(DEFAULT_RESULT.doubleValue()))
            .jsonPath("$.[*].testTaken")
            .value(hasItem(DEFAULT_TEST_TAKEN.toString()))
            .jsonPath("$.[*].certificateContentType")
            .value(hasItem(DEFAULT_CERTIFICATE_CONTENT_TYPE))
            .jsonPath("$.[*].certificate")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_CERTIFICATE)));
    }

    @Test
    void getKnowledge() {
        // Initialize the database
        knowledgeRepository.save(knowledge).block();

        // Get the knowledge
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, knowledge.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(knowledge.getId()))
            .jsonPath("$.result")
            .value(is(DEFAULT_RESULT.doubleValue()))
            .jsonPath("$.testTaken")
            .value(is(DEFAULT_TEST_TAKEN.toString()))
            .jsonPath("$.certificateContentType")
            .value(is(DEFAULT_CERTIFICATE_CONTENT_TYPE))
            .jsonPath("$.certificate")
            .value(is(Base64Utils.encodeToString(DEFAULT_CERTIFICATE)));
    }

    @Test
    void getNonExistingKnowledge() {
        // Get the knowledge
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewKnowledge() throws Exception {
        // Initialize the database
        knowledgeRepository.save(knowledge).block();

        int databaseSizeBeforeUpdate = knowledgeRepository.findAll().collectList().block().size();

        // Update the knowledge
        Knowledge updatedKnowledge = knowledgeRepository.findById(knowledge.getId()).block();
        updatedKnowledge
            .result(UPDATED_RESULT)
            .testTaken(UPDATED_TEST_TAKEN)
            .certificate(UPDATED_CERTIFICATE)
            .certificateContentType(UPDATED_CERTIFICATE_CONTENT_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedKnowledge.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedKnowledge))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Knowledge in the database
        List<Knowledge> knowledgeList = knowledgeRepository.findAll().collectList().block();
        assertThat(knowledgeList).hasSize(databaseSizeBeforeUpdate);
        Knowledge testKnowledge = knowledgeList.get(knowledgeList.size() - 1);
        assertThat(testKnowledge.getResult()).isEqualTo(UPDATED_RESULT);
        assertThat(testKnowledge.getTestTaken()).isEqualTo(UPDATED_TEST_TAKEN);
        assertThat(testKnowledge.getCertificate()).isEqualTo(UPDATED_CERTIFICATE);
        assertThat(testKnowledge.getCertificateContentType()).isEqualTo(UPDATED_CERTIFICATE_CONTENT_TYPE);
    }

    @Test
    void putNonExistingKnowledge() throws Exception {
        int databaseSizeBeforeUpdate = knowledgeRepository.findAll().collectList().block().size();
        knowledge.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, knowledge.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledge))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Knowledge in the database
        List<Knowledge> knowledgeList = knowledgeRepository.findAll().collectList().block();
        assertThat(knowledgeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchKnowledge() throws Exception {
        int databaseSizeBeforeUpdate = knowledgeRepository.findAll().collectList().block().size();
        knowledge.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledge))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Knowledge in the database
        List<Knowledge> knowledgeList = knowledgeRepository.findAll().collectList().block();
        assertThat(knowledgeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamKnowledge() throws Exception {
        int databaseSizeBeforeUpdate = knowledgeRepository.findAll().collectList().block().size();
        knowledge.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledge))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Knowledge in the database
        List<Knowledge> knowledgeList = knowledgeRepository.findAll().collectList().block();
        assertThat(knowledgeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateKnowledgeWithPatch() throws Exception {
        // Initialize the database
        knowledgeRepository.save(knowledge).block();

        int databaseSizeBeforeUpdate = knowledgeRepository.findAll().collectList().block().size();

        // Update the knowledge using partial update
        Knowledge partialUpdatedKnowledge = new Knowledge();
        partialUpdatedKnowledge.setId(knowledge.getId());

        partialUpdatedKnowledge
            .result(UPDATED_RESULT)
            .testTaken(UPDATED_TEST_TAKEN)
            .certificate(UPDATED_CERTIFICATE)
            .certificateContentType(UPDATED_CERTIFICATE_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedKnowledge.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedKnowledge))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Knowledge in the database
        List<Knowledge> knowledgeList = knowledgeRepository.findAll().collectList().block();
        assertThat(knowledgeList).hasSize(databaseSizeBeforeUpdate);
        Knowledge testKnowledge = knowledgeList.get(knowledgeList.size() - 1);
        assertThat(testKnowledge.getResult()).isEqualTo(UPDATED_RESULT);
        assertThat(testKnowledge.getTestTaken()).isEqualTo(UPDATED_TEST_TAKEN);
        assertThat(testKnowledge.getCertificate()).isEqualTo(UPDATED_CERTIFICATE);
        assertThat(testKnowledge.getCertificateContentType()).isEqualTo(UPDATED_CERTIFICATE_CONTENT_TYPE);
    }

    @Test
    void fullUpdateKnowledgeWithPatch() throws Exception {
        // Initialize the database
        knowledgeRepository.save(knowledge).block();

        int databaseSizeBeforeUpdate = knowledgeRepository.findAll().collectList().block().size();

        // Update the knowledge using partial update
        Knowledge partialUpdatedKnowledge = new Knowledge();
        partialUpdatedKnowledge.setId(knowledge.getId());

        partialUpdatedKnowledge
            .result(UPDATED_RESULT)
            .testTaken(UPDATED_TEST_TAKEN)
            .certificate(UPDATED_CERTIFICATE)
            .certificateContentType(UPDATED_CERTIFICATE_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedKnowledge.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedKnowledge))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Knowledge in the database
        List<Knowledge> knowledgeList = knowledgeRepository.findAll().collectList().block();
        assertThat(knowledgeList).hasSize(databaseSizeBeforeUpdate);
        Knowledge testKnowledge = knowledgeList.get(knowledgeList.size() - 1);
        assertThat(testKnowledge.getResult()).isEqualTo(UPDATED_RESULT);
        assertThat(testKnowledge.getTestTaken()).isEqualTo(UPDATED_TEST_TAKEN);
        assertThat(testKnowledge.getCertificate()).isEqualTo(UPDATED_CERTIFICATE);
        assertThat(testKnowledge.getCertificateContentType()).isEqualTo(UPDATED_CERTIFICATE_CONTENT_TYPE);
    }

    @Test
    void patchNonExistingKnowledge() throws Exception {
        int databaseSizeBeforeUpdate = knowledgeRepository.findAll().collectList().block().size();
        knowledge.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, knowledge.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledge))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Knowledge in the database
        List<Knowledge> knowledgeList = knowledgeRepository.findAll().collectList().block();
        assertThat(knowledgeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchKnowledge() throws Exception {
        int databaseSizeBeforeUpdate = knowledgeRepository.findAll().collectList().block().size();
        knowledge.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledge))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Knowledge in the database
        List<Knowledge> knowledgeList = knowledgeRepository.findAll().collectList().block();
        assertThat(knowledgeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamKnowledge() throws Exception {
        int databaseSizeBeforeUpdate = knowledgeRepository.findAll().collectList().block().size();
        knowledge.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledge))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Knowledge in the database
        List<Knowledge> knowledgeList = knowledgeRepository.findAll().collectList().block();
        assertThat(knowledgeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteKnowledge() {
        // Initialize the database
        knowledgeRepository.save(knowledge).block();

        int databaseSizeBeforeDelete = knowledgeRepository.findAll().collectList().block().size();

        // Delete the knowledge
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, knowledge.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Knowledge> knowledgeList = knowledgeRepository.findAll().collectList().block();
        assertThat(knowledgeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
