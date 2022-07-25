package com.datalinkedai.employee.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datalinkedai.employee.IntegrationTest;
import com.datalinkedai.employee.domain.KnowledgeCentral;
import com.datalinkedai.employee.repository.KnowledgeCentralRepository;
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
 * Integration tests for the {@link KnowledgeCentralResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class KnowledgeCentralResourceIT {

    private static final Double DEFAULT_AVERAGE_RESULT = 0D;
    private static final Double UPDATED_AVERAGE_RESULT = 1D;

    private static final String ENTITY_API_URL = "/api/knowledge-centrals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private KnowledgeCentralRepository knowledgeCentralRepository;

    @Autowired
    private WebTestClient webTestClient;

    private KnowledgeCentral knowledgeCentral;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KnowledgeCentral createEntity() {
        KnowledgeCentral knowledgeCentral = new KnowledgeCentral().averageResult(DEFAULT_AVERAGE_RESULT);
        return knowledgeCentral;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static KnowledgeCentral createUpdatedEntity() {
        KnowledgeCentral knowledgeCentral = new KnowledgeCentral().averageResult(UPDATED_AVERAGE_RESULT);
        return knowledgeCentral;
    }

    @BeforeEach
    public void initTest() {
        knowledgeCentralRepository.deleteAll().block();
        knowledgeCentral = createEntity();
    }

    @Test
    void createKnowledgeCentral() throws Exception {
        int databaseSizeBeforeCreate = knowledgeCentralRepository.findAll().collectList().block().size();
        // Create the KnowledgeCentral
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledgeCentral))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the KnowledgeCentral in the database
        List<KnowledgeCentral> knowledgeCentralList = knowledgeCentralRepository.findAll().collectList().block();
        assertThat(knowledgeCentralList).hasSize(databaseSizeBeforeCreate + 1);
        KnowledgeCentral testKnowledgeCentral = knowledgeCentralList.get(knowledgeCentralList.size() - 1);
        assertThat(testKnowledgeCentral.getAverageResult()).isEqualTo(DEFAULT_AVERAGE_RESULT);
    }

    @Test
    void createKnowledgeCentralWithExistingId() throws Exception {
        // Create the KnowledgeCentral with an existing ID
        knowledgeCentral.setId("existing_id");

        int databaseSizeBeforeCreate = knowledgeCentralRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledgeCentral))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the KnowledgeCentral in the database
        List<KnowledgeCentral> knowledgeCentralList = knowledgeCentralRepository.findAll().collectList().block();
        assertThat(knowledgeCentralList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllKnowledgeCentralsAsStream() {
        // Initialize the database
        knowledgeCentralRepository.save(knowledgeCentral).block();

        List<KnowledgeCentral> knowledgeCentralList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(KnowledgeCentral.class)
            .getResponseBody()
            .filter(knowledgeCentral::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(knowledgeCentralList).isNotNull();
        assertThat(knowledgeCentralList).hasSize(1);
        KnowledgeCentral testKnowledgeCentral = knowledgeCentralList.get(0);
        assertThat(testKnowledgeCentral.getAverageResult()).isEqualTo(DEFAULT_AVERAGE_RESULT);
    }

    @Test
    void getAllKnowledgeCentrals() {
        // Initialize the database
        knowledgeCentralRepository.save(knowledgeCentral).block();

        // Get all the knowledgeCentralList
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
            .value(hasItem(knowledgeCentral.getId()))
            .jsonPath("$.[*].averageResult")
            .value(hasItem(DEFAULT_AVERAGE_RESULT.doubleValue()));
    }

    @Test
    void getKnowledgeCentral() {
        // Initialize the database
        knowledgeCentralRepository.save(knowledgeCentral).block();

        // Get the knowledgeCentral
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, knowledgeCentral.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(knowledgeCentral.getId()))
            .jsonPath("$.averageResult")
            .value(is(DEFAULT_AVERAGE_RESULT.doubleValue()));
    }

    @Test
    void getNonExistingKnowledgeCentral() {
        // Get the knowledgeCentral
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewKnowledgeCentral() throws Exception {
        // Initialize the database
        knowledgeCentralRepository.save(knowledgeCentral).block();

        int databaseSizeBeforeUpdate = knowledgeCentralRepository.findAll().collectList().block().size();

        // Update the knowledgeCentral
        KnowledgeCentral updatedKnowledgeCentral = knowledgeCentralRepository.findById(knowledgeCentral.getId()).block();
        updatedKnowledgeCentral.averageResult(UPDATED_AVERAGE_RESULT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedKnowledgeCentral.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedKnowledgeCentral))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the KnowledgeCentral in the database
        List<KnowledgeCentral> knowledgeCentralList = knowledgeCentralRepository.findAll().collectList().block();
        assertThat(knowledgeCentralList).hasSize(databaseSizeBeforeUpdate);
        KnowledgeCentral testKnowledgeCentral = knowledgeCentralList.get(knowledgeCentralList.size() - 1);
        assertThat(testKnowledgeCentral.getAverageResult()).isEqualTo(UPDATED_AVERAGE_RESULT);
    }

    @Test
    void putNonExistingKnowledgeCentral() throws Exception {
        int databaseSizeBeforeUpdate = knowledgeCentralRepository.findAll().collectList().block().size();
        knowledgeCentral.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, knowledgeCentral.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledgeCentral))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the KnowledgeCentral in the database
        List<KnowledgeCentral> knowledgeCentralList = knowledgeCentralRepository.findAll().collectList().block();
        assertThat(knowledgeCentralList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchKnowledgeCentral() throws Exception {
        int databaseSizeBeforeUpdate = knowledgeCentralRepository.findAll().collectList().block().size();
        knowledgeCentral.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledgeCentral))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the KnowledgeCentral in the database
        List<KnowledgeCentral> knowledgeCentralList = knowledgeCentralRepository.findAll().collectList().block();
        assertThat(knowledgeCentralList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamKnowledgeCentral() throws Exception {
        int databaseSizeBeforeUpdate = knowledgeCentralRepository.findAll().collectList().block().size();
        knowledgeCentral.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledgeCentral))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the KnowledgeCentral in the database
        List<KnowledgeCentral> knowledgeCentralList = knowledgeCentralRepository.findAll().collectList().block();
        assertThat(knowledgeCentralList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateKnowledgeCentralWithPatch() throws Exception {
        // Initialize the database
        knowledgeCentralRepository.save(knowledgeCentral).block();

        int databaseSizeBeforeUpdate = knowledgeCentralRepository.findAll().collectList().block().size();

        // Update the knowledgeCentral using partial update
        KnowledgeCentral partialUpdatedKnowledgeCentral = new KnowledgeCentral();
        partialUpdatedKnowledgeCentral.setId(knowledgeCentral.getId());

        partialUpdatedKnowledgeCentral.averageResult(UPDATED_AVERAGE_RESULT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedKnowledgeCentral.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedKnowledgeCentral))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the KnowledgeCentral in the database
        List<KnowledgeCentral> knowledgeCentralList = knowledgeCentralRepository.findAll().collectList().block();
        assertThat(knowledgeCentralList).hasSize(databaseSizeBeforeUpdate);
        KnowledgeCentral testKnowledgeCentral = knowledgeCentralList.get(knowledgeCentralList.size() - 1);
        assertThat(testKnowledgeCentral.getAverageResult()).isEqualTo(UPDATED_AVERAGE_RESULT);
    }

    @Test
    void fullUpdateKnowledgeCentralWithPatch() throws Exception {
        // Initialize the database
        knowledgeCentralRepository.save(knowledgeCentral).block();

        int databaseSizeBeforeUpdate = knowledgeCentralRepository.findAll().collectList().block().size();

        // Update the knowledgeCentral using partial update
        KnowledgeCentral partialUpdatedKnowledgeCentral = new KnowledgeCentral();
        partialUpdatedKnowledgeCentral.setId(knowledgeCentral.getId());

        partialUpdatedKnowledgeCentral.averageResult(UPDATED_AVERAGE_RESULT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedKnowledgeCentral.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedKnowledgeCentral))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the KnowledgeCentral in the database
        List<KnowledgeCentral> knowledgeCentralList = knowledgeCentralRepository.findAll().collectList().block();
        assertThat(knowledgeCentralList).hasSize(databaseSizeBeforeUpdate);
        KnowledgeCentral testKnowledgeCentral = knowledgeCentralList.get(knowledgeCentralList.size() - 1);
        assertThat(testKnowledgeCentral.getAverageResult()).isEqualTo(UPDATED_AVERAGE_RESULT);
    }

    @Test
    void patchNonExistingKnowledgeCentral() throws Exception {
        int databaseSizeBeforeUpdate = knowledgeCentralRepository.findAll().collectList().block().size();
        knowledgeCentral.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, knowledgeCentral.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledgeCentral))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the KnowledgeCentral in the database
        List<KnowledgeCentral> knowledgeCentralList = knowledgeCentralRepository.findAll().collectList().block();
        assertThat(knowledgeCentralList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchKnowledgeCentral() throws Exception {
        int databaseSizeBeforeUpdate = knowledgeCentralRepository.findAll().collectList().block().size();
        knowledgeCentral.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledgeCentral))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the KnowledgeCentral in the database
        List<KnowledgeCentral> knowledgeCentralList = knowledgeCentralRepository.findAll().collectList().block();
        assertThat(knowledgeCentralList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamKnowledgeCentral() throws Exception {
        int databaseSizeBeforeUpdate = knowledgeCentralRepository.findAll().collectList().block().size();
        knowledgeCentral.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(knowledgeCentral))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the KnowledgeCentral in the database
        List<KnowledgeCentral> knowledgeCentralList = knowledgeCentralRepository.findAll().collectList().block();
        assertThat(knowledgeCentralList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteKnowledgeCentral() {
        // Initialize the database
        knowledgeCentralRepository.save(knowledgeCentral).block();

        int databaseSizeBeforeDelete = knowledgeCentralRepository.findAll().collectList().block().size();

        // Delete the knowledgeCentral
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, knowledgeCentral.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<KnowledgeCentral> knowledgeCentralList = knowledgeCentralRepository.findAll().collectList().block();
        assertThat(knowledgeCentralList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
