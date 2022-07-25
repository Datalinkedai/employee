package com.datalinkedai.employee.web.rest;

import com.datalinkedai.employee.domain.Interview;
import com.datalinkedai.employee.repository.InterviewRepository;
import com.datalinkedai.employee.service.InterviewService;
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
 * REST controller for managing {@link com.datalinkedai.employee.domain.Interview}.
 */
@RestController
@RequestMapping("/api")
public class InterviewResource {

    private final Logger log = LoggerFactory.getLogger(InterviewResource.class);

    private static final String ENTITY_NAME = "interview";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InterviewService interviewService;

    private final InterviewRepository interviewRepository;

    public InterviewResource(InterviewService interviewService, InterviewRepository interviewRepository) {
        this.interviewService = interviewService;
        this.interviewRepository = interviewRepository;
    }

    /**
     * {@code POST  /interviews} : Create a new interview.
     *
     * @param interview the interview to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new interview, or with status {@code 400 (Bad Request)} if the interview has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/interviews")
    public Mono<ResponseEntity<Interview>> createInterview(@Valid @RequestBody Interview interview) throws URISyntaxException {
        log.debug("REST request to save Interview : {}", interview);
        if (interview.getId() != null) {
            throw new BadRequestAlertException("A new interview cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return interviewService
            .save(interview)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/interviews/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /interviews/:id} : Updates an existing interview.
     *
     * @param id the id of the interview to save.
     * @param interview the interview to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated interview,
     * or with status {@code 400 (Bad Request)} if the interview is not valid,
     * or with status {@code 500 (Internal Server Error)} if the interview couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/interviews/{id}")
    public Mono<ResponseEntity<Interview>> updateInterview(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Interview interview
    ) throws URISyntaxException {
        log.debug("REST request to update Interview : {}, {}", id, interview);
        if (interview.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, interview.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return interviewRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return interviewService
                    .update(interview)
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
     * {@code PATCH  /interviews/:id} : Partial updates given fields of an existing interview, field will ignore if it is null
     *
     * @param id the id of the interview to save.
     * @param interview the interview to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated interview,
     * or with status {@code 400 (Bad Request)} if the interview is not valid,
     * or with status {@code 404 (Not Found)} if the interview is not found,
     * or with status {@code 500 (Internal Server Error)} if the interview couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/interviews/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Interview>> partialUpdateInterview(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Interview interview
    ) throws URISyntaxException {
        log.debug("REST request to partial update Interview partially : {}, {}", id, interview);
        if (interview.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, interview.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return interviewRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Interview> result = interviewService.partialUpdate(interview);

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
     * {@code GET  /interviews} : get all the interviews.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of interviews in body.
     */
    @GetMapping("/interviews")
    public Mono<List<Interview>> getAllInterviews() {
        log.debug("REST request to get all Interviews");
        return interviewService.findAll().collectList();
    }

    /**
     * {@code GET  /interviews} : get all the interviews as a stream.
     * @return the {@link Flux} of interviews.
     */
    @GetMapping(value = "/interviews", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Interview> getAllInterviewsAsStream() {
        log.debug("REST request to get all Interviews as a stream");
        return interviewService.findAll();
    }

    /**
     * {@code GET  /interviews/:id} : get the "id" interview.
     *
     * @param id the id of the interview to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the interview, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/interviews/{id}")
    public Mono<ResponseEntity<Interview>> getInterview(@PathVariable String id) {
        log.debug("REST request to get Interview : {}", id);
        Mono<Interview> interview = interviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(interview);
    }

    /**
     * {@code DELETE  /interviews/:id} : delete the "id" interview.
     *
     * @param id the id of the interview to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/interviews/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteInterview(@PathVariable String id) {
        log.debug("REST request to delete Interview : {}", id);
        return interviewService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }
}
