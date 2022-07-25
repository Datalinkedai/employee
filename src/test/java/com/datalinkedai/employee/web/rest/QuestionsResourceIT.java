package com.datalinkedai.employee.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datalinkedai.employee.IntegrationTest;
import com.datalinkedai.employee.domain.Questions;
import com.datalinkedai.employee.domain.enumeration.AnswerType;
import com.datalinkedai.employee.repository.QuestionsRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link QuestionsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class QuestionsResourceIT {

    private static final String DEFAULT_QUESTION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION_NAME = "BBBBBBBBBB";

    private static final AnswerType DEFAULT_ANSWER_TYPE = AnswerType.MCQ;
    private static final AnswerType UPDATED_ANSWER_TYPE = AnswerType.TEXT;

    private static final byte[] DEFAULT_IMAGE_LINK = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE_LINK = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_LINK_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_LINK_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/questions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private QuestionsRepository questionsRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Questions questions;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Questions createEntity() {
        Questions questions = new Questions()
            .questionName(DEFAULT_QUESTION_NAME)
            .answerType(DEFAULT_ANSWER_TYPE)
            .imageLink(DEFAULT_IMAGE_LINK)
            .imageLinkContentType(DEFAULT_IMAGE_LINK_CONTENT_TYPE);
        return questions;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Questions createUpdatedEntity() {
        Questions questions = new Questions()
            .questionName(UPDATED_QUESTION_NAME)
            .answerType(UPDATED_ANSWER_TYPE)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE);
        return questions;
    }

    @BeforeEach
    public void initTest() {
        questionsRepository.deleteAll().block();
        questions = createEntity();
    }

    @Test
    void createQuestions() throws Exception {
        int databaseSizeBeforeCreate = questionsRepository.findAll().collectList().block().size();
        // Create the Questions
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(questions))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Questions in the database
        List<Questions> questionsList = questionsRepository.findAll().collectList().block();
        assertThat(questionsList).hasSize(databaseSizeBeforeCreate + 1);
        Questions testQuestions = questionsList.get(questionsList.size() - 1);
        assertThat(testQuestions.getQuestionName()).isEqualTo(DEFAULT_QUESTION_NAME);
        assertThat(testQuestions.getAnswerType()).isEqualTo(DEFAULT_ANSWER_TYPE);
        assertThat(testQuestions.getImageLink()).isEqualTo(DEFAULT_IMAGE_LINK);
        assertThat(testQuestions.getImageLinkContentType()).isEqualTo(DEFAULT_IMAGE_LINK_CONTENT_TYPE);
    }

    @Test
    void createQuestionsWithExistingId() throws Exception {
        // Create the Questions with an existing ID
        questions.setId("existing_id");

        int databaseSizeBeforeCreate = questionsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(questions))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Questions in the database
        List<Questions> questionsList = questionsRepository.findAll().collectList().block();
        assertThat(questionsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllQuestionsAsStream() {
        // Initialize the database
        questionsRepository.save(questions).block();

        List<Questions> questionsList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Questions.class)
            .getResponseBody()
            .filter(questions::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(questionsList).isNotNull();
        assertThat(questionsList).hasSize(1);
        Questions testQuestions = questionsList.get(0);
        assertThat(testQuestions.getQuestionName()).isEqualTo(DEFAULT_QUESTION_NAME);
        assertThat(testQuestions.getAnswerType()).isEqualTo(DEFAULT_ANSWER_TYPE);
        assertThat(testQuestions.getImageLink()).isEqualTo(DEFAULT_IMAGE_LINK);
        assertThat(testQuestions.getImageLinkContentType()).isEqualTo(DEFAULT_IMAGE_LINK_CONTENT_TYPE);
    }

    @Test
    void getAllQuestions() {
        // Initialize the database
        questionsRepository.save(questions).block();

        // Get all the questionsList
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
            .value(hasItem(questions.getId()))
            .jsonPath("$.[*].questionName")
            .value(hasItem(DEFAULT_QUESTION_NAME))
            .jsonPath("$.[*].answerType")
            .value(hasItem(DEFAULT_ANSWER_TYPE.toString()))
            .jsonPath("$.[*].imageLinkContentType")
            .value(hasItem(DEFAULT_IMAGE_LINK_CONTENT_TYPE))
            .jsonPath("$.[*].imageLink")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE_LINK)));
    }

    @Test
    void getQuestions() {
        // Initialize the database
        questionsRepository.save(questions).block();

        // Get the questions
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, questions.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(questions.getId()))
            .jsonPath("$.questionName")
            .value(is(DEFAULT_QUESTION_NAME))
            .jsonPath("$.answerType")
            .value(is(DEFAULT_ANSWER_TYPE.toString()))
            .jsonPath("$.imageLinkContentType")
            .value(is(DEFAULT_IMAGE_LINK_CONTENT_TYPE))
            .jsonPath("$.imageLink")
            .value(is(Base64Utils.encodeToString(DEFAULT_IMAGE_LINK)));
    }

    @Test
    void getNonExistingQuestions() {
        // Get the questions
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewQuestions() throws Exception {
        // Initialize the database
        questionsRepository.save(questions).block();

        int databaseSizeBeforeUpdate = questionsRepository.findAll().collectList().block().size();

        // Update the questions
        Questions updatedQuestions = questionsRepository.findById(questions.getId()).block();
        updatedQuestions
            .questionName(UPDATED_QUESTION_NAME)
            .answerType(UPDATED_ANSWER_TYPE)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedQuestions.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedQuestions))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Questions in the database
        List<Questions> questionsList = questionsRepository.findAll().collectList().block();
        assertThat(questionsList).hasSize(databaseSizeBeforeUpdate);
        Questions testQuestions = questionsList.get(questionsList.size() - 1);
        assertThat(testQuestions.getQuestionName()).isEqualTo(UPDATED_QUESTION_NAME);
        assertThat(testQuestions.getAnswerType()).isEqualTo(UPDATED_ANSWER_TYPE);
        assertThat(testQuestions.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
        assertThat(testQuestions.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
    }

    @Test
    void putNonExistingQuestions() throws Exception {
        int databaseSizeBeforeUpdate = questionsRepository.findAll().collectList().block().size();
        questions.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, questions.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(questions))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Questions in the database
        List<Questions> questionsList = questionsRepository.findAll().collectList().block();
        assertThat(questionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchQuestions() throws Exception {
        int databaseSizeBeforeUpdate = questionsRepository.findAll().collectList().block().size();
        questions.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(questions))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Questions in the database
        List<Questions> questionsList = questionsRepository.findAll().collectList().block();
        assertThat(questionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamQuestions() throws Exception {
        int databaseSizeBeforeUpdate = questionsRepository.findAll().collectList().block().size();
        questions.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(questions))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Questions in the database
        List<Questions> questionsList = questionsRepository.findAll().collectList().block();
        assertThat(questionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateQuestionsWithPatch() throws Exception {
        // Initialize the database
        questionsRepository.save(questions).block();

        int databaseSizeBeforeUpdate = questionsRepository.findAll().collectList().block().size();

        // Update the questions using partial update
        Questions partialUpdatedQuestions = new Questions();
        partialUpdatedQuestions.setId(questions.getId());

        partialUpdatedQuestions
            .questionName(UPDATED_QUESTION_NAME)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedQuestions.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedQuestions))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Questions in the database
        List<Questions> questionsList = questionsRepository.findAll().collectList().block();
        assertThat(questionsList).hasSize(databaseSizeBeforeUpdate);
        Questions testQuestions = questionsList.get(questionsList.size() - 1);
        assertThat(testQuestions.getQuestionName()).isEqualTo(UPDATED_QUESTION_NAME);
        assertThat(testQuestions.getAnswerType()).isEqualTo(DEFAULT_ANSWER_TYPE);
        assertThat(testQuestions.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
        assertThat(testQuestions.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
    }

    @Test
    void fullUpdateQuestionsWithPatch() throws Exception {
        // Initialize the database
        questionsRepository.save(questions).block();

        int databaseSizeBeforeUpdate = questionsRepository.findAll().collectList().block().size();

        // Update the questions using partial update
        Questions partialUpdatedQuestions = new Questions();
        partialUpdatedQuestions.setId(questions.getId());

        partialUpdatedQuestions
            .questionName(UPDATED_QUESTION_NAME)
            .answerType(UPDATED_ANSWER_TYPE)
            .imageLink(UPDATED_IMAGE_LINK)
            .imageLinkContentType(UPDATED_IMAGE_LINK_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedQuestions.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedQuestions))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Questions in the database
        List<Questions> questionsList = questionsRepository.findAll().collectList().block();
        assertThat(questionsList).hasSize(databaseSizeBeforeUpdate);
        Questions testQuestions = questionsList.get(questionsList.size() - 1);
        assertThat(testQuestions.getQuestionName()).isEqualTo(UPDATED_QUESTION_NAME);
        assertThat(testQuestions.getAnswerType()).isEqualTo(UPDATED_ANSWER_TYPE);
        assertThat(testQuestions.getImageLink()).isEqualTo(UPDATED_IMAGE_LINK);
        assertThat(testQuestions.getImageLinkContentType()).isEqualTo(UPDATED_IMAGE_LINK_CONTENT_TYPE);
    }

    @Test
    void patchNonExistingQuestions() throws Exception {
        int databaseSizeBeforeUpdate = questionsRepository.findAll().collectList().block().size();
        questions.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, questions.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(questions))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Questions in the database
        List<Questions> questionsList = questionsRepository.findAll().collectList().block();
        assertThat(questionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchQuestions() throws Exception {
        int databaseSizeBeforeUpdate = questionsRepository.findAll().collectList().block().size();
        questions.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(questions))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Questions in the database
        List<Questions> questionsList = questionsRepository.findAll().collectList().block();
        assertThat(questionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamQuestions() throws Exception {
        int databaseSizeBeforeUpdate = questionsRepository.findAll().collectList().block().size();
        questions.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(questions))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Questions in the database
        List<Questions> questionsList = questionsRepository.findAll().collectList().block();
        assertThat(questionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteQuestions() {
        // Initialize the database
        questionsRepository.save(questions).block();

        int databaseSizeBeforeDelete = questionsRepository.findAll().collectList().block().size();

        // Delete the questions
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, questions.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Questions> questionsList = questionsRepository.findAll().collectList().block();
        assertThat(questionsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
