package com.datalinkedai.employee.web.rest;

import com.datalinkedai.employee.domain.Options;
import com.datalinkedai.employee.repository.OptionsRepository;
import com.datalinkedai.employee.service.OptionsService;
import com.datalinkedai.employee.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * REST controller for managing {@link com.datalinkedai.employee.domain.Options}.
 */
@RestController
@RequestMapping("/api")
public class OptionsResource {

    private final Logger log = LoggerFactory.getLogger(OptionsResource.class);

    private static final String ENTITY_NAME = "options";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OptionsService optionsService;

    private final OptionsRepository optionsRepository;

    public OptionsResource(OptionsService optionsService, OptionsRepository optionsRepository) {
        this.optionsService = optionsService;
        this.optionsRepository = optionsRepository;
    }

    /**
     * {@code POST  /options} : Create a new options.
     *
     * @param options the options to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new options, or with status {@code 400 (Bad Request)} if the options has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/options")
    public Mono<ResponseEntity<Options>> createOptions(@RequestBody Options options) throws URISyntaxException {
        log.debug("REST request to save Options : {}", options);
        if (options.getId() != null) {
            throw new BadRequestAlertException("A new options cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return optionsService
            .save(options)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/options/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /options/:id} : Updates an existing options.
     *
     * @param id the id of the options to save.
     * @param options the options to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated options,
     * or with status {@code 400 (Bad Request)} if the options is not valid,
     * or with status {@code 500 (Internal Server Error)} if the options couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/options/{id}")
    public Mono<ResponseEntity<Options>> updateOptions(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Options options
    ) throws URISyntaxException {
        log.debug("REST request to update Options : {}, {}", id, options);
        if (options.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, options.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return optionsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return optionsService
                    .update(options)
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
     * {@code PATCH  /options/:id} : Partial updates given fields of an existing options, field will ignore if it is null
     *
     * @param id the id of the options to save.
     * @param options the options to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated options,
     * or with status {@code 400 (Bad Request)} if the options is not valid,
     * or with status {@code 404 (Not Found)} if the options is not found,
     * or with status {@code 500 (Internal Server Error)} if the options couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/options/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Options>> partialUpdateOptions(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Options options
    ) throws URISyntaxException {
        log.debug("REST request to partial update Options partially : {}, {}", id, options);
        if (options.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, options.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return optionsRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Options> result = optionsService.partialUpdate(options);

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
     * {@code GET  /options} : get all the options.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of options in body.
     */
    @GetMapping("/options")
    public Mono<List<Options>> getAllOptions() {
        log.debug("REST request to get all Options");
        return optionsService.findAll().collectList();
    }

    /**
     * {@code GET  /options} : get all the options as a stream.
     * @return the {@link Flux} of options.
     */
    @GetMapping(value = "/options", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Options> getAllOptionsAsStream() {
        log.debug("REST request to get all Options as a stream");
        return optionsService.findAll();
    }

    /**
     * {@code GET  /options/:id} : get the "id" options.
     *
     * @param id the id of the options to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the options, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/options/{id}")
    public Mono<ResponseEntity<Options>> getOptions(@PathVariable String id) {
        log.debug("REST request to get Options : {}", id);
        Mono<Options> options = optionsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(options);
    }

    /**
     * {@code DELETE  /options/:id} : delete the "id" options.
     *
     * @param id the id of the options to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/options/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteOptions(@PathVariable String id) {
        log.debug("REST request to delete Options : {}", id);
        return optionsService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }
}
