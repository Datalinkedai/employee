package com.datalinkedai.employee.service.impl;

import com.datalinkedai.employee.domain.Training;
import com.datalinkedai.employee.repository.TrainingRepository;
import com.datalinkedai.employee.service.TrainingService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Training}.
 */
@Service
public class TrainingServiceImpl implements TrainingService {

    private final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingRepository trainingRepository;

    public TrainingServiceImpl(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Override
    public Mono<Training> save(Training training) {
        log.debug("Request to save Training : {}", training);
        return trainingRepository.save(training);
    }

    @Override
    public Mono<Training> update(Training training) {
        log.debug("Request to save Training : {}", training);
        return trainingRepository.save(training);
    }

    @Override
    public Mono<Training> partialUpdate(Training training) {
        log.debug("Request to partially update Training : {}", training);

        return trainingRepository
            .findById(training.getId())
            .map(existingTraining -> {
                if (training.getStartDate() != null) {
                    existingTraining.setStartDate(training.getStartDate());
                }
                if (training.getStartTime() != null) {
                    existingTraining.setStartTime(training.getStartTime());
                }
                if (training.getEndTime() != null) {
                    existingTraining.setEndTime(training.getEndTime());
                }
                if (training.getEndDate() != null) {
                    existingTraining.setEndDate(training.getEndDate());
                }
                if (training.getType() != null) {
                    existingTraining.setType(training.getType());
                }
                if (training.getRepeats() != null) {
                    existingTraining.setRepeats(training.getRepeats());
                }

                return existingTraining;
            })
            .flatMap(trainingRepository::save);
    }

    @Override
    public Flux<Training> findAll() {
        log.debug("Request to get all Trainings");
        return trainingRepository.findAll();
    }

    public Mono<Long> countAll() {
        return trainingRepository.count();
    }

    @Override
    public Mono<Training> findOne(String id) {
        log.debug("Request to get Training : {}", id);
        return trainingRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Training : {}", id);
        return trainingRepository.deleteById(id);
    }
}
