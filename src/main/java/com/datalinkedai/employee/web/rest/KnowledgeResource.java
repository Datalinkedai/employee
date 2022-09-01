package com.datalinkedai.employee.web.rest;

import com.datalinkedai.employee.domain.Knowledge;
import com.datalinkedai.employee.exceptions.TestNotFoundException;
import com.datalinkedai.employee.repository.KnowledgeRepository;
import com.datalinkedai.employee.service.KnowledgeService;
import com.datalinkedai.employee.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;
import com.datalinkedai.employee.exceptions.CandidateNotFoundException;

/**
 * REST controller for managing
 * {@link com.datalinkedai.employee.domain.Knowledge}.
 */
@RestController
@RequestMapping("/api")
public class KnowledgeResource {

    private final Logger log = LoggerFactory.getLogger(KnowledgeResource.class);

    private static final String ENTITY_NAME = "knowledge";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KnowledgeService knowledgeService;

    private final KnowledgeRepository knowledgeRepository;

    public KnowledgeResource(KnowledgeService knowledgeService, KnowledgeRepository knowledgeRepository) {
        this.knowledgeService = knowledgeService;
        this.knowledgeRepository = knowledgeRepository;
    }

    /**
     * {@code POST  /knowledges} : Create a new knowledge.
     *
     * @param knowledge the knowledge to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new knowledge, or with status {@code 400 (Bad Request)} if
     *         the knowledge has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/knowledges")
    public Mono<ResponseEntity<Knowledge>> createKnowledge(@Valid @RequestBody Knowledge knowledge)
            throws URISyntaxException {
        log.debug("REST request to save Knowledge : {}", knowledge);
        if (knowledge.getId() != null) {
            throw new BadRequestAlertException("A new knowledge cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return knowledgeService
                .save(knowledge)
                .map(result -> {
                    try {
                        return ResponseEntity
                                .created(new URI("/api/knowledges/" + result.getId()))
                                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                                        result.getId()))
                                .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * {@code PUT  /knowledges/:id} : Updates an existing knowledge.
     *
     * @param id        the id of the knowledge to save.
     * @param knowledge the knowledge to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated knowledge,
     *         or with status {@code 400 (Bad Request)} if the knowledge is not
     *         valid,
     *         or with status {@code 500 (Internal Server Error)} if the knowledge
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/knowledges/{id}")
    public Mono<ResponseEntity<Knowledge>> updateKnowledge(
            @PathVariable(value = "id", required = false) final String id,
            @Valid @RequestBody Knowledge knowledge) throws URISyntaxException {
        log.debug("REST request to update Knowledge : {}, {}", id, knowledge);
        if (knowledge.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledge.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return knowledgeRepository
                .existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return knowledgeService
                            .update(knowledge)
                            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                            .map(result -> ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                                            result.getId()))
                                    .body(result));
                });
    }

    /**
     * {@code PATCH  /knowledges/:id} : Partial updates given fields of an existing
     * knowledge, field will ignore if it is null
     *
     * @param id        the id of the knowledge to save.
     * @param knowledge the knowledge to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated knowledge,
     *         or with status {@code 400 (Bad Request)} if the knowledge is not
     *         valid,
     *         or with status {@code 404 (Not Found)} if the knowledge is not found,
     *         or with status {@code 500 (Internal Server Error)} if the knowledge
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/knowledges/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Knowledge>> partialUpdateKnowledge(
            @PathVariable(value = "id", required = false) final String id,
            @NotNull @RequestBody Knowledge knowledge) throws URISyntaxException {
        log.debug("REST request to partial update Knowledge partially : {}, {}", id, knowledge);
        if (knowledge.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledge.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return knowledgeRepository
                .existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Knowledge> result = knowledgeService.partialUpdate(knowledge);

                    return result
                            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                            .map(res -> ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                                            res.getId()))
                                    .body(res));
                });
    }

    /**
     * {@code GET  /knowledges} : get all the knowledges.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of knowledges in body.
     */
    @GetMapping("/knowledges")
    public Mono<List<Knowledge>> getAllKnowledges() {
        log.debug("REST request to get all Knowledges");
        return knowledgeService.findAll().collectList();
    }

    /**
     * {@code GET  /knowledges} : get all the knowledges as a stream.
     * 
     * @return the {@link Flux} of knowledges.
     */
    @GetMapping(value = "/knowledges", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Knowledge> getAllKnowledgesAsStream() {
        log.debug("REST request to get all Knowledges as a stream");
        return knowledgeService.findAll();
    }

    /**
     * {@code GET  /knowledges/:id} : get the "id" knowledge.
     *
     * @param id the id of the knowledge to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the knowledge, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/knowledges/{id}")
    public Mono<ResponseEntity<Knowledge>> getKnowledge(@PathVariable String id) {
        log.debug("REST request to get Knowledge : {}", id);
        Mono<Knowledge> knowledge = knowledgeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(knowledge);
    }

    /**
     * {@code DELETE  /knowledges/:id} : delete the "id" knowledge.
     *
     * @param id the id of the knowledge to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/knowledges/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteKnowledge(@PathVariable String id) {
        log.debug("REST request to delete Knowledge : {}", id);
        return knowledgeService
                .delete(id)
                .map(result -> ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build());
    }

    /**
     * {@code GET  /knowledge/:candidateId} : get the "candidateId" .
     * 
     * @param candidateId the id of the candidate to retrieve their knowledge
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         , or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/knowledge/by/candidate/{candidateId}")
    public Mono<ResponseEntity<Knowledge>> getKnowledgeByCandidateTaken(@PathVariable String candidateId) {
        log.debug("REST request to get KnowledgeByCandidate : {}", candidateId);
        try {
            return ResponseUtil.wrapOrNotFound(knowledgeService.getKnowledgeByCandidateTaken(candidateId));
        } catch (CandidateNotFoundException e) {
            throw new BadRequestAlertException("Invalid Candidate ID", ENTITY_NAME, "invalidCandidateId");
        } catch (Exception e) {
            log.error("Error : {}", e);
            throw new BadRequestAlertException("Record Not Found", ENTITY_NAME, "recNotFound");
        }
    }

    /**
     * {@code GET "/knowledge/by/test/{testedName}" : get the "testedName"
     * @param testedName get test name of a particular test object
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */

    @GetMapping("/knowledge/by/test/{testedName}")
    public Mono<ResponseEntity<Knowledge>> getKnowledgeByTests(@PathVariable String testedName) {
        log.debug("REST request to get KnowledgeByTest : {}", testedName);
        try {
            return ResponseUtil.wrapOrNotFound(knowledgeService.getKnowledgeByTests(testedName));
        } catch (TestNotFoundException e) {
            throw new BadRequestAlertException("Invalid Test Name", ENTITY_NAME, "invalidTestName");
        } catch (Exception e) {
            log.error("Error : {}", e);
            throw new BadRequestAlertException("Record Not Found", ENTITY_NAME, "recNotFound");
        }
    }
}
