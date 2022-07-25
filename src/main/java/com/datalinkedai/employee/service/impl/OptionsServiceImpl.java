package com.datalinkedai.employee.service.impl;

import com.datalinkedai.employee.domain.Options;
import com.datalinkedai.employee.repository.OptionsRepository;
import com.datalinkedai.employee.service.OptionsService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Options}.
 */
@Service
public class OptionsServiceImpl implements OptionsService {

    private final Logger log = LoggerFactory.getLogger(OptionsServiceImpl.class);

    private final OptionsRepository optionsRepository;

    public OptionsServiceImpl(OptionsRepository optionsRepository) {
        this.optionsRepository = optionsRepository;
    }

    @Override
    public Mono<Options> save(Options options) {
        log.debug("Request to save Options : {}", options);
        return optionsRepository.save(options);
    }

    @Override
    public Mono<Options> update(Options options) {
        log.debug("Request to save Options : {}", options);
        return optionsRepository.save(options);
    }

    @Override
    public Mono<Options> partialUpdate(Options options) {
        log.debug("Request to partially update Options : {}", options);

        return optionsRepository
            .findById(options.getId())
            .map(existingOptions -> {
                if (options.getOptionName() != null) {
                    existingOptions.setOptionName(options.getOptionName());
                }

                return existingOptions;
            })
            .flatMap(optionsRepository::save);
    }

    @Override
    public Flux<Options> findAll() {
        log.debug("Request to get all Options");
        return optionsRepository.findAll();
    }

    public Mono<Long> countAll() {
        return optionsRepository.count();
    }

    @Override
    public Mono<Options> findOne(String id) {
        log.debug("Request to get Options : {}", id);
        return optionsRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Options : {}", id);
        return optionsRepository.deleteById(id);
    }
}
