package com.datalinkedai.employee.web.rest;

import com.datalinkedai.employee.domain.Tested;
import com.datalinkedai.employee.repository.TestedRepository;
import com.datalinkedai.employee.service.TestedService;
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
 * REST controller for managing {@link com.datalinkedai.employee.domain.Tested}.
 */
@RestController
@RequestMapping("/api")
public class TestedResource {

    private final Logger log = LoggerFactory.getLogger(TestedResource.class);

    private static final String ENTITY_NAME = "tested";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TestedService testedService;

    private final TestedRepository testedRepository;

    public TestedResource(TestedService testedService, TestedRepository testedRepository) {
        this.testedService = testedService;
        this.testedRepository = testedRepository;
    }

    /**
     * {@code POST  /testeds} : Create a new tested.
     *
     * @param tested the tested to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tested, or with status {@code 400 (Bad Request)} if the tested has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/testeds")
    public Mono<ResponseEntity<Tested>> createTested(@Valid @RequestBody Tested tested) throws URISyntaxException {
        log.debug("REST request to save Tested : {}", tested);
        if (tested.getId() != null) {
            throw new BadRequestAlertException("A new tested cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return testedService
            .save(tested)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/testeds/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /testeds/:id} : Updates an existing tested.
     *
     * @param id the id of the tested to save.
     * @param tested the tested to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tested,
     * or with status {@code 400 (Bad Request)} if the tested is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tested couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/testeds/{id}")
    public Mono<ResponseEntity<Tested>> updateTested(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Tested tested
    ) throws URISyntaxException {
        log.debug("REST request to update Tested : {}, {}", id, tested);
        if (tested.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tested.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return testedRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return testedService
                    .update(tested)
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
     * {@code PATCH  /testeds/:id} : Partial updates given fields of an existing tested, field will ignore if it is null
     *
     * @param id the id of the tested to save.
     * @param tested the tested to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tested,
     * or with status {@code 400 (Bad Request)} if the tested is not valid,
     * or with status {@code 404 (Not Found)} if the tested is not found,
     * or with status {@code 500 (Internal Server Error)} if the tested couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/testeds/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Tested>> partialUpdateTested(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Tested tested
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tested partially : {}, {}", id, tested);
        if (tested.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tested.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return testedRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Tested> result = testedService.partialUpdate(tested);

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
     * {@code GET  /testeds} : get all the testeds.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of testeds in body.
     */
    @GetMapping("/testeds")
    public Mono<List<Tested>> getAllTesteds() {
        log.debug("REST request to get all Testeds");
        return testedService.findAll().collectList();
    }

    /**
     * {@code GET  /testeds} : get all the testeds as a stream.
     * @return the {@link Flux} of testeds.
     */
    @GetMapping(value = "/testeds", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Tested> getAllTestedsAsStream() {
        log.debug("REST request to get all Testeds as a stream");
        return testedService.findAll();
    }

    /**
     * {@code GET  /testeds/:id} : get the "id" tested.
     *
     * @param id the id of the tested to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tested, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/testeds/{id}")
    public Mono<ResponseEntity<Tested>> getTested(@PathVariable String id) {
        log.debug("REST request to get Tested : {}", id);
        Mono<Tested> tested = testedService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tested);
    }

    /**
     * {@code DELETE  /testeds/:id} : delete the "id" tested.
     *
     * @param id the id of the tested to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/testeds/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteTested(@PathVariable String id) {
        log.debug("REST request to delete Tested : {}", id);
        return testedService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }

    /**
     * {@code GET /testeds/test/name/:testname} get the question according to the tested setup
     * @param testName the testname for which the question needs to be provided
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the proper tested,
     * or with status {@code 400 (Bad Request)} if the test name is not valid,
     */
    @GetMapping("/testeds/test/name/{testname}")
    public Mono<ResponseEntity<Tested>> getRandomizeQuestionsForTest(@PathVariable("testname") String testName) {
        log.debug("REST request to get Randomised Question from Test Name: {}", testName);
        try {
            return ResponseUtil.wrapOrNotFound(testedService.getRandomizeQuestionsForTest(testName));
        } catch (Exception e) {
            log.error("Randomise Question has an error: {}", e);
            throw new BadRequestAlertException("Could Not get the Test Name Properly", ENTITY_NAME, "testNameInvalid");
        }
    }
}
