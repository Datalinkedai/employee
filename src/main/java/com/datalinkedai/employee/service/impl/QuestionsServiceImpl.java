package com.datalinkedai.employee.service.impl;

import com.datalinkedai.employee.domain.Questions;
import com.datalinkedai.employee.repository.QuestionsRepository;
import com.datalinkedai.employee.service.QuestionsService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Questions}.
 */
@Service
public class QuestionsServiceImpl implements QuestionsService {

    private final Logger log = LoggerFactory.getLogger(QuestionsServiceImpl.class);

    private final QuestionsRepository questionsRepository;

    public QuestionsServiceImpl(QuestionsRepository questionsRepository) {
        this.questionsRepository = questionsRepository;
    }

    @Override
    public Mono<Questions> save(Questions questions) {
        log.debug("Request to save Questions : {}", questions);
        return questionsRepository.save(questions);
    }

    @Override
    public Mono<Questions> update(Questions questions) {
        log.debug("Request to save Questions : {}", questions);
        return questionsRepository.save(questions);
    }

    @Override
    public Mono<Questions> partialUpdate(Questions questions) {
        log.debug("Request to partially update Questions : {}", questions);

        return questionsRepository
            .findById(questions.getId())
            .map(existingQuestions -> {
                if (questions.getQuestionName() != null) {
                    existingQuestions.setQuestionName(questions.getQuestionName());
                }
                if (questions.getAnswerType() != null) {
                    existingQuestions.setAnswerType(questions.getAnswerType());
                }
                if (questions.getImageLink() != null) {
                    existingQuestions.setImageLink(questions.getImageLink());
                }
                if (questions.getImageLinkContentType() != null) {
                    existingQuestions.setImageLinkContentType(questions.getImageLinkContentType());
                }

                return existingQuestions;
            })
            .flatMap(questionsRepository::save);
    }

    @Override
    public Flux<Questions> findAll() {
        log.debug("Request to get all Questions");
        return questionsRepository.findAll();
    }

    public Mono<Long> countAll() {
        return questionsRepository.count();
    }

    @Override
    public Mono<Questions> findOne(String id) {
        log.debug("Request to get Questions : {}", id);
        return questionsRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Questions : {}", id);
        return questionsRepository.deleteById(id);
    }
}
