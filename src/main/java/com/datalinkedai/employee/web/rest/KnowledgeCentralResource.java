package com.datalinkedai.employee.web.rest;

import com.datalinkedai.employee.domain.KnowledgeCentral;
import com.datalinkedai.employee.exceptions.CandidateNotFoundException;
import com.datalinkedai.employee.repository.KnowledgeCentralRepository;
import com.datalinkedai.employee.service.KnowledgeCentralService;
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

/**
 * REST controller for managing {@link com.datalinkedai.employee.domain.KnowledgeCentral}.
 */
@RestController
@RequestMapping("/api")
public class KnowledgeCentralResource {

    private final Logger log = LoggerFactory.getLogger(KnowledgeCentralResource.class);

    private static final String ENTITY_NAME = "knowledgeCentral";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KnowledgeCentralService knowledgeCentralService;

    private final KnowledgeCentralRepository knowledgeCentralRepository;

    public KnowledgeCentralResource(
        KnowledgeCentralService knowledgeCentralService,
        KnowledgeCentralRepository knowledgeCentralRepository
    ) {
        this.knowledgeCentralService = knowledgeCentralService;
        this.knowledgeCentralRepository = knowledgeCentralRepository;
    }

    /**
     * {@code POST  /knowledge-centrals} : Create a new knowledgeCentral.
     *
     * @param knowledgeCentral the knowledgeCentral to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new knowledgeCentral, or with status {@code 400 (Bad Request)} if the knowledgeCentral has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/knowledge-centrals")
    public Mono<ResponseEntity<KnowledgeCentral>> createKnowledgeCentral(@Valid @RequestBody KnowledgeCentral knowledgeCentral)
        throws URISyntaxException {
        log.debug("REST request to save KnowledgeCentral : {}", knowledgeCentral);
        if (knowledgeCentral.getId() != null) {
            throw new BadRequestAlertException("A new knowledgeCentral cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return knowledgeCentralService
            .save(knowledgeCentral)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/knowledge-centrals/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /knowledge-centrals/:id} : Updates an existing knowledgeCentral.
     *
     * @param id the id of the knowledgeCentral to save.
     * @param knowledgeCentral the knowledgeCentral to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated knowledgeCentral,
     * or with status {@code 400 (Bad Request)} if the knowledgeCentral is not valid,
     * or with status {@code 500 (Internal Server Error)} if the knowledgeCentral couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/knowledge-centrals/{id}")
    public Mono<ResponseEntity<KnowledgeCentral>> updateKnowledgeCentral(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody KnowledgeCentral knowledgeCentral
    ) throws URISyntaxException {
        log.debug("REST request to update KnowledgeCentral : {}, {}", id, knowledgeCentral);
        if (knowledgeCentral.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledgeCentral.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return knowledgeCentralRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return knowledgeCentralService
                    .update(knowledgeCentral)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /knowledge-centrals/:id} : Partial updates given fields of an existing knowledgeCentral, field will ignore if it is null
     *
     * @param id the id of the knowledgeCentral to save.
     * @param knowledgeCentral the knowledgeCentral to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated knowledgeCentral,
     * or with status {@code 400 (Bad Request)} if the knowledgeCentral is not valid,
     * or with status {@code 404 (Not Found)} if the knowledgeCentral is not found,
     * or with status {@code 500 (Internal Server Error)} if the knowledgeCentral couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/knowledge-centrals/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<KnowledgeCentral>> partialUpdateKnowledgeCentral(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody KnowledgeCentral knowledgeCentral
    ) throws URISyntaxException {
        log.debug("REST request to partial update KnowledgeCentral partially : {}, {}", id, knowledgeCentral);
        if (knowledgeCentral.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, knowledgeCentral.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return knowledgeCentralRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<KnowledgeCentral> result = knowledgeCentralService.partialUpdate(knowledgeCentral);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /knowledge-centrals} : get all the knowledgeCentrals.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of knowledgeCentrals in body.
     */
    @GetMapping("/knowledge-centrals")
    public Mono<List<KnowledgeCentral>> getAllKnowledgeCentrals() {
        log.debug("REST request to get all KnowledgeCentrals");
        return knowledgeCentralService.findAll().collectList();
    }

    /**
     * {@code GET  /knowledge-centrals} : get all the knowledgeCentrals as a stream.
     * @return the {@link Flux} of knowledgeCentrals.
     */
    @GetMapping(value = "/knowledge-centrals", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<KnowledgeCentral> getAllKnowledgeCentralsAsStream() {
        log.debug("REST request to get all KnowledgeCentrals as a stream");
        return knowledgeCentralService.findAll();
    }

    /**
     * {@code GET  /knowledge-centrals/:id} : get the "id" knowledgeCentral.
     *
     * @param id the id of the knowledgeCentral to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the knowledgeCentral, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/knowledge-centrals/{id}")
    public Mono<ResponseEntity<KnowledgeCentral>> getKnowledgeCentral(@PathVariable String id) {
        log.debug("REST request to get KnowledgeCentral : {}", id);
        Mono<KnowledgeCentral> knowledgeCentral = knowledgeCentralService.findOne(id);
        return ResponseUtil.wrapOrNotFound(knowledgeCentral);
    }

    /**
     * {@code DELETE  /knowledge-centrals/:id} : delete the "id" knowledgeCentral.
     *
     * @param id the id of the knowledgeCentral to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/knowledge-centrals/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteKnowledgeCentral(@PathVariable String id) {
        log.debug("REST request to delete KnowledgeCentral : {}", id);
        return knowledgeCentralService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }
    @GetMapping("/knowledge-centrals/by/candidate/{candidateId}")
    public Mono<ResponseEntity<KnowledgeCentral>> getKnowledgeCentralByCandidate(@PathVariable String candidateId){
        log.debug("REST request to get KnowledgeCentralByCandidate : {}", candidateId);
        try {
            return ResponseUtil.wrapOrNotFound(knowledgeCentralService.getKnowledgeCentralByCandiate(candidateId));
        } catch (CandidateNotFoundException e) {
            throw new BadRequestAlertException("Invalid Candidate ID", ENTITY_NAME, "invalidCandidateId");
        } catch (Exception e) {
            log.error("Error : {}", e);
            throw new BadRequestAlertException("Record Not Found", ENTITY_NAME, "recNotFound");
        }
    }
}
