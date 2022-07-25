package com.datalinkedai.employee.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datalinkedai.employee.IntegrationTest;
import com.datalinkedai.employee.domain.Options;
import com.datalinkedai.employee.repository.OptionsRepository;
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
 * Integration tests for the {@link OptionsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OptionsResourceIT {

    private static final String DEFAULT_OPTION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OPTION_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/options";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private OptionsRepository optionsRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Options options;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Options createEntity() {
        Options options = new Options().optionName(DEFAULT_OPTION_NAME);
        return options;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Options createUpdatedEntity() {
        Options options = new Options().optionName(UPDATED_OPTION_NAME);
        return options;
    }

    @BeforeEach
    public void initTest() {
        optionsRepository.deleteAll().block();
        options = createEntity();
    }

    @Test
    void createOptions() throws Exception {
        int databaseSizeBeforeCreate = optionsRepository.findAll().collectList().block().size();
        // Create the Options
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(options))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll().collectList().block();
        assertThat(optionsList).hasSize(databaseSizeBeforeCreate + 1);
        Options testOptions = optionsList.get(optionsList.size() - 1);
        assertThat(testOptions.getOptionName()).isEqualTo(DEFAULT_OPTION_NAME);
    }

    @Test
    void createOptionsWithExistingId() throws Exception {
        // Create the Options with an existing ID
        options.setId("existing_id");

        int databaseSizeBeforeCreate = optionsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(options))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll().collectList().block();
        assertThat(optionsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllOptionsAsStream() {
        // Initialize the database
        optionsRepository.save(options).block();

        List<Options> optionsList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Options.class)
            .getResponseBody()
            .filter(options::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(optionsList).isNotNull();
        assertThat(optionsList).hasSize(1);
        Options testOptions = optionsList.get(0);
        assertThat(testOptions.getOptionName()).isEqualTo(DEFAULT_OPTION_NAME);
    }

    @Test
    void getAllOptions() {
        // Initialize the database
        optionsRepository.save(options).block();

        // Get all the optionsList
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
            .value(hasItem(options.getId()))
            .jsonPath("$.[*].optionName")
            .value(hasItem(DEFAULT_OPTION_NAME));
    }

    @Test
    void getOptions() {
        // Initialize the database
        optionsRepository.save(options).block();

        // Get the options
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, options.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(options.getId()))
            .jsonPath("$.optionName")
            .value(is(DEFAULT_OPTION_NAME));
    }

    @Test
    void getNonExistingOptions() {
        // Get the options
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewOptions() throws Exception {
        // Initialize the database
        optionsRepository.save(options).block();

        int databaseSizeBeforeUpdate = optionsRepository.findAll().collectList().block().size();

        // Update the options
        Options updatedOptions = optionsRepository.findById(options.getId()).block();
        updatedOptions.optionName(UPDATED_OPTION_NAME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedOptions.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedOptions))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll().collectList().block();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
        Options testOptions = optionsList.get(optionsList.size() - 1);
        assertThat(testOptions.getOptionName()).isEqualTo(UPDATED_OPTION_NAME);
    }

    @Test
    void putNonExistingOptions() throws Exception {
        int databaseSizeBeforeUpdate = optionsRepository.findAll().collectList().block().size();
        options.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, options.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(options))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll().collectList().block();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOptions() throws Exception {
        int databaseSizeBeforeUpdate = optionsRepository.findAll().collectList().block().size();
        options.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(options))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll().collectList().block();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOptions() throws Exception {
        int databaseSizeBeforeUpdate = optionsRepository.findAll().collectList().block().size();
        options.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(options))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll().collectList().block();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOptionsWithPatch() throws Exception {
        // Initialize the database
        optionsRepository.save(options).block();

        int databaseSizeBeforeUpdate = optionsRepository.findAll().collectList().block().size();

        // Update the options using partial update
        Options partialUpdatedOptions = new Options();
        partialUpdatedOptions.setId(options.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOptions.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOptions))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll().collectList().block();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
        Options testOptions = optionsList.get(optionsList.size() - 1);
        assertThat(testOptions.getOptionName()).isEqualTo(DEFAULT_OPTION_NAME);
    }

    @Test
    void fullUpdateOptionsWithPatch() throws Exception {
        // Initialize the database
        optionsRepository.save(options).block();

        int databaseSizeBeforeUpdate = optionsRepository.findAll().collectList().block().size();

        // Update the options using partial update
        Options partialUpdatedOptions = new Options();
        partialUpdatedOptions.setId(options.getId());

        partialUpdatedOptions.optionName(UPDATED_OPTION_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOptions.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOptions))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll().collectList().block();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
        Options testOptions = optionsList.get(optionsList.size() - 1);
        assertThat(testOptions.getOptionName()).isEqualTo(UPDATED_OPTION_NAME);
    }

    @Test
    void patchNonExistingOptions() throws Exception {
        int databaseSizeBeforeUpdate = optionsRepository.findAll().collectList().block().size();
        options.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, options.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(options))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll().collectList().block();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOptions() throws Exception {
        int databaseSizeBeforeUpdate = optionsRepository.findAll().collectList().block().size();
        options.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(options))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll().collectList().block();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOptions() throws Exception {
        int databaseSizeBeforeUpdate = optionsRepository.findAll().collectList().block().size();
        options.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(options))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Options in the database
        List<Options> optionsList = optionsRepository.findAll().collectList().block();
        assertThat(optionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOptions() {
        // Initialize the database
        optionsRepository.save(options).block();

        int databaseSizeBeforeDelete = optionsRepository.findAll().collectList().block().size();

        // Delete the options
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, options.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Options> optionsList = optionsRepository.findAll().collectList().block();
        assertThat(optionsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
