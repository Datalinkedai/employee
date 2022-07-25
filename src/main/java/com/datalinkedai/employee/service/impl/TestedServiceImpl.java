package com.datalinkedai.employee.service.impl;

import com.datalinkedai.employee.domain.Tested;
import com.datalinkedai.employee.repository.TestedRepository;
import com.datalinkedai.employee.service.TestedService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Tested}.
 */
@Service
public class TestedServiceImpl implements TestedService {

    private final Logger log = LoggerFactory.getLogger(TestedServiceImpl.class);

    private final TestedRepository testedRepository;

    public TestedServiceImpl(TestedRepository testedRepository) {
        this.testedRepository = testedRepository;
    }

    @Override
    public Mono<Tested> save(Tested tested) {
        log.debug("Request to save Tested : {}", tested);
        return testedRepository.save(tested);
    }

    @Override
    public Mono<Tested> update(Tested tested) {
        log.debug("Request to save Tested : {}", tested);
        return testedRepository.save(tested);
    }

    @Override
    public Mono<Tested> partialUpdate(Tested tested) {
        log.debug("Request to partially update Tested : {}", tested);

        return testedRepository
            .findById(tested.getId())
            .map(existingTested -> {
                if (tested.getTestName() != null) {
                    existingTested.setTestName(tested.getTestName());
                }
                if (tested.getTimeToComplete() != null) {
                    existingTested.setTimeToComplete(tested.getTimeToComplete());
                }
                if (tested.getTotalQuestions() != null) {
                    existingTested.setTotalQuestions(tested.getTotalQuestions());
                }
                if (tested.getRandomize() != null) {
                    existingTested.setRandomize(tested.getRandomize());
                }
                if (tested.getPassingPrcnt() != null) {
                    existingTested.setPassingPrcnt(tested.getPassingPrcnt());
                }
                if (tested.getExpiryMonths() != null) {
                    existingTested.setExpiryMonths(tested.getExpiryMonths());
                }

                return existingTested;
            })
            .flatMap(testedRepository::save);
    }

    @Override
    public Flux<Tested> findAll() {
        log.debug("Request to get all Testeds");
        return testedRepository.findAll();
    }

    public Mono<Long> countAll() {
        return testedRepository.count();
    }

    @Override
    public Mono<Tested> findOne(String id) {
        log.debug("Request to get Tested : {}", id);
        return testedRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Tested : {}", id);
        return testedRepository.deleteById(id);
    }
}
