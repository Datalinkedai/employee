package com.datalinkedai.employee.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datalinkedai.employee.IntegrationTest;
import com.datalinkedai.employee.domain.Documents;
import com.datalinkedai.employee.domain.enumeration.DocumentType;
import com.datalinkedai.employee.repository.DocumentsRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link DocumentsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DocumentsResourceIT {

    private static final DocumentType DEFAULT_DOCUMENT_TYPE = DocumentType.IMAGE;
    private static final DocumentType UPDATED_DOCUMENT_TYPE = DocumentType.CERTIFICATE;

    private static final byte[] DEFAULT_DOCUMENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DOCUMENT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_DOCUMENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DOCUMENT_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_DOCUMENT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENT_LINK = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DOCUMENT_EXPIRY = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DOCUMENT_EXPIRY = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_VERIFIED = false;
    private static final Boolean UPDATED_VERIFIED = true;

    private static final String ENTITY_API_URL = "/api/documents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Documents documents;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Documents createEntity() {
        Documents documents = new Documents()
            .documentType(DEFAULT_DOCUMENT_TYPE)
            .document(DEFAULT_DOCUMENT)
            .documentContentType(DEFAULT_DOCUMENT_CONTENT_TYPE)
            .documentLink(DEFAULT_DOCUMENT_LINK)
            .documentExpiry(DEFAULT_DOCUMENT_EXPIRY)
            .verified(DEFAULT_VERIFIED);
        return documents;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Documents createUpdatedEntity() {
        Documents documents = new Documents()
            .documentType(UPDATED_DOCUMENT_TYPE)
            .document(UPDATED_DOCUMENT)
            .documentContentType(UPDATED_DOCUMENT_CONTENT_TYPE)
            .documentLink(UPDATED_DOCUMENT_LINK)
            .documentExpiry(UPDATED_DOCUMENT_EXPIRY)
            .verified(UPDATED_VERIFIED);
        return documents;
    }

    @BeforeEach
    public void initTest() {
        documentsRepository.deleteAll().block();
        documents = createEntity();
    }

    @Test
    void createDocuments() throws Exception {
        int databaseSizeBeforeCreate = documentsRepository.findAll().collectList().block().size();
        // Create the Documents
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(documents))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Documents in the database
        List<Documents> documentsList = documentsRepository.findAll().collectList().block();
        assertThat(documentsList).hasSize(databaseSizeBeforeCreate + 1);
        Documents testDocuments = documentsList.get(documentsList.size() - 1);
        assertThat(testDocuments.getDocumentType()).isEqualTo(DEFAULT_DOCUMENT_TYPE);
        assertThat(testDocuments.getDocument()).isEqualTo(DEFAULT_DOCUMENT);
        assertThat(testDocuments.getDocumentContentType()).isEqualTo(DEFAULT_DOCUMENT_CONTENT_TYPE);
        assertThat(testDocuments.getDocumentLink()).isEqualTo(DEFAULT_DOCUMENT_LINK);
        assertThat(testDocuments.getDocumentExpiry()).isEqualTo(DEFAULT_DOCUMENT_EXPIRY);
        assertThat(testDocuments.getVerified()).isEqualTo(DEFAULT_VERIFIED);
    }

    @Test
    void createDocumentsWithExistingId() throws Exception {
        // Create the Documents with an existing ID
        documents.setId("existing_id");

        int databaseSizeBeforeCreate = documentsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(documents))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Documents in the database
        List<Documents> documentsList = documentsRepository.findAll().collectList().block();
        assertThat(documentsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkDocumentTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = documentsRepository.findAll().collectList().block().size();
        // set the field null
        documents.setDocumentType(null);

        // Create the Documents, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(documents))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Documents> documentsList = documentsRepository.findAll().collectList().block();
        assertThat(documentsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllDocumentsAsStream() {
        // Initialize the database
        documentsRepository.save(documents).block();

        List<Documents> documentsList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Documents.class)
            .getResponseBody()
            .filter(documents::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(documentsList).isNotNull();
        assertThat(documentsList).hasSize(1);
        Documents testDocuments = documentsList.get(0);
        assertThat(testDocuments.getDocumentType()).isEqualTo(DEFAULT_DOCUMENT_TYPE);
        assertThat(testDocuments.getDocument()).isEqualTo(DEFAULT_DOCUMENT);
        assertThat(testDocuments.getDocumentContentType()).isEqualTo(DEFAULT_DOCUMENT_CONTENT_TYPE);
        assertThat(testDocuments.getDocumentLink()).isEqualTo(DEFAULT_DOCUMENT_LINK);
        assertThat(testDocuments.getDocumentExpiry()).isEqualTo(DEFAULT_DOCUMENT_EXPIRY);
        assertThat(testDocuments.getVerified()).isEqualTo(DEFAULT_VERIFIED);
    }

    @Test
    void getAllDocuments() {
        // Initialize the database
        documentsRepository.save(documents).block();

        // Get all the documentsList
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
            .value(hasItem(documents.getId()))
            .jsonPath("$.[*].documentType")
            .value(hasItem(DEFAULT_DOCUMENT_TYPE.toString()))
            .jsonPath("$.[*].documentContentType")
            .value(hasItem(DEFAULT_DOCUMENT_CONTENT_TYPE))
            .jsonPath("$.[*].document")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_DOCUMENT)))
            .jsonPath("$.[*].documentLink")
            .value(hasItem(DEFAULT_DOCUMENT_LINK))
            .jsonPath("$.[*].documentExpiry")
            .value(hasItem(DEFAULT_DOCUMENT_EXPIRY.toString()))
            .jsonPath("$.[*].verified")
            .value(hasItem(DEFAULT_VERIFIED.booleanValue()));
    }

    @Test
    void getDocuments() {
        // Initialize the database
        documentsRepository.save(documents).block();

        // Get the documents
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, documents.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(documents.getId()))
            .jsonPath("$.documentType")
            .value(is(DEFAULT_DOCUMENT_TYPE.toString()))
            .jsonPath("$.documentContentType")
            .value(is(DEFAULT_DOCUMENT_CONTENT_TYPE))
            .jsonPath("$.document")
            .value(is(Base64Utils.encodeToString(DEFAULT_DOCUMENT)))
            .jsonPath("$.documentLink")
            .value(is(DEFAULT_DOCUMENT_LINK))
            .jsonPath("$.documentExpiry")
            .value(is(DEFAULT_DOCUMENT_EXPIRY.toString()))
            .jsonPath("$.verified")
            .value(is(DEFAULT_VERIFIED.booleanValue()));
    }

    @Test
    void getNonExistingDocuments() {
        // Get the documents
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewDocuments() throws Exception {
        // Initialize the database
        documentsRepository.save(documents).block();

        int databaseSizeBeforeUpdate = documentsRepository.findAll().collectList().block().size();

        // Update the documents
        Documents updatedDocuments = documentsRepository.findById(documents.getId()).block();
        updatedDocuments
            .documentType(UPDATED_DOCUMENT_TYPE)
            .document(UPDATED_DOCUMENT)
            .documentContentType(UPDATED_DOCUMENT_CONTENT_TYPE)
            .documentLink(UPDATED_DOCUMENT_LINK)
            .documentExpiry(UPDATED_DOCUMENT_EXPIRY)
            .verified(UPDATED_VERIFIED);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDocuments.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedDocuments))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Documents in the database
        List<Documents> documentsList = documentsRepository.findAll().collectList().block();
        assertThat(documentsList).hasSize(databaseSizeBeforeUpdate);
        Documents testDocuments = documentsList.get(documentsList.size() - 1);
        assertThat(testDocuments.getDocumentType()).isEqualTo(UPDATED_DOCUMENT_TYPE);
        assertThat(testDocuments.getDocument()).isEqualTo(UPDATED_DOCUMENT);
        assertThat(testDocuments.getDocumentContentType()).isEqualTo(UPDATED_DOCUMENT_CONTENT_TYPE);
        assertThat(testDocuments.getDocumentLink()).isEqualTo(UPDATED_DOCUMENT_LINK);
        assertThat(testDocuments.getDocumentExpiry()).isEqualTo(UPDATED_DOCUMENT_EXPIRY);
        assertThat(testDocuments.getVerified()).isEqualTo(UPDATED_VERIFIED);
    }

    @Test
    void putNonExistingDocuments() throws Exception {
        int databaseSizeBeforeUpdate = documentsRepository.findAll().collectList().block().size();
        documents.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, documents.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(documents))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Documents in the database
        List<Documents> documentsList = documentsRepository.findAll().collectList().block();
        assertThat(documentsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDocuments() throws Exception {
        int databaseSizeBeforeUpdate = documentsRepository.findAll().collectList().block().size();
        documents.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(documents))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Documents in the database
        List<Documents> documentsList = documentsRepository.findAll().collectList().block();
        assertThat(documentsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDocuments() throws Exception {
        int databaseSizeBeforeUpdate = documentsRepository.findAll().collectList().block().size();
        documents.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(documents))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Documents in the database
        List<Documents> documentsList = documentsRepository.findAll().collectList().block();
        assertThat(documentsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDocumentsWithPatch() throws Exception {
        // Initialize the database
        documentsRepository.save(documents).block();

        int databaseSizeBeforeUpdate = documentsRepository.findAll().collectList().block().size();

        // Update the documents using partial update
        Documents partialUpdatedDocuments = new Documents();
        partialUpdatedDocuments.setId(documents.getId());

        partialUpdatedDocuments.verified(UPDATED_VERIFIED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDocuments.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDocuments))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Documents in the database
        List<Documents> documentsList = documentsRepository.findAll().collectList().block();
        assertThat(documentsList).hasSize(databaseSizeBeforeUpdate);
        Documents testDocuments = documentsList.get(documentsList.size() - 1);
        assertThat(testDocuments.getDocumentType()).isEqualTo(DEFAULT_DOCUMENT_TYPE);
        assertThat(testDocuments.getDocument()).isEqualTo(DEFAULT_DOCUMENT);
        assertThat(testDocuments.getDocumentContentType()).isEqualTo(DEFAULT_DOCUMENT_CONTENT_TYPE);
        assertThat(testDocuments.getDocumentLink()).isEqualTo(DEFAULT_DOCUMENT_LINK);
        assertThat(testDocuments.getDocumentExpiry()).isEqualTo(DEFAULT_DOCUMENT_EXPIRY);
        assertThat(testDocuments.getVerified()).isEqualTo(UPDATED_VERIFIED);
    }

    @Test
    void fullUpdateDocumentsWithPatch() throws Exception {
        // Initialize the database
        documentsRepository.save(documents).block();

        int databaseSizeBeforeUpdate = documentsRepository.findAll().collectList().block().size();

        // Update the documents using partial update
        Documents partialUpdatedDocuments = new Documents();
        partialUpdatedDocuments.setId(documents.getId());

        partialUpdatedDocuments
            .documentType(UPDATED_DOCUMENT_TYPE)
            .document(UPDATED_DOCUMENT)
            .documentContentType(UPDATED_DOCUMENT_CONTENT_TYPE)
            .documentLink(UPDATED_DOCUMENT_LINK)
            .documentExpiry(UPDATED_DOCUMENT_EXPIRY)
            .verified(UPDATED_VERIFIED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDocuments.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDocuments))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Documents in the database
        List<Documents> documentsList = documentsRepository.findAll().collectList().block();
        assertThat(documentsList).hasSize(databaseSizeBeforeUpdate);
        Documents testDocuments = documentsList.get(documentsList.size() - 1);
        assertThat(testDocuments.getDocumentType()).isEqualTo(UPDATED_DOCUMENT_TYPE);
        assertThat(testDocuments.getDocument()).isEqualTo(UPDATED_DOCUMENT);
        assertThat(testDocuments.getDocumentContentType()).isEqualTo(UPDATED_DOCUMENT_CONTENT_TYPE);
        assertThat(testDocuments.getDocumentLink()).isEqualTo(UPDATED_DOCUMENT_LINK);
        assertThat(testDocuments.getDocumentExpiry()).isEqualTo(UPDATED_DOCUMENT_EXPIRY);
        assertThat(testDocuments.getVerified()).isEqualTo(UPDATED_VERIFIED);
    }

    @Test
    void patchNonExistingDocuments() throws Exception {
        int databaseSizeBeforeUpdate = documentsRepository.findAll().collectList().block().size();
        documents.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, documents.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(documents))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Documents in the database
        List<Documents> documentsList = documentsRepository.findAll().collectList().block();
        assertThat(documentsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDocuments() throws Exception {
        int databaseSizeBeforeUpdate = documentsRepository.findAll().collectList().block().size();
        documents.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(documents))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Documents in the database
        List<Documents> documentsList = documentsRepository.findAll().collectList().block();
        assertThat(documentsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDocuments() throws Exception {
        int databaseSizeBeforeUpdate = documentsRepository.findAll().collectList().block().size();
        documents.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(documents))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Documents in the database
        List<Documents> documentsList = documentsRepository.findAll().collectList().block();
        assertThat(documentsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDocuments() {
        // Initialize the database
        documentsRepository.save(documents).block();

        int databaseSizeBeforeDelete = documentsRepository.findAll().collectList().block().size();

        // Delete the documents
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, documents.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Documents> documentsList = documentsRepository.findAll().collectList().block();
        assertThat(documentsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
