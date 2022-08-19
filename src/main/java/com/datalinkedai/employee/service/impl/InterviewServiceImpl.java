package com.datalinkedai.employee.service.impl;

import com.datalinkedai.employee.domain.Interview;
import com.datalinkedai.employee.repository.InterviewRepository;
import com.datalinkedai.employee.service.InterviewService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Interview}.
 */
@Service
public class InterviewServiceImpl implements InterviewService {

    private final Logger log = LoggerFactory.getLogger(InterviewServiceImpl.class);

    private final InterviewRepository interviewRepository;

    public InterviewServiceImpl(InterviewRepository interviewRepository) {
        this.interviewRepository = interviewRepository;
    }

    @Override
    public Mono<Interview> save(Interview interview) {
        log.debug("Request to save Interview : {}", interview);
        return interviewRepository.save(interview);
    }

    @Override
    public Mono<Interview> update(Interview interview) {
        log.debug("Request to save Interview : {}", interview);
        return interviewRepository.save(interview);
    }

    @Override
    public Mono<Interview> partialUpdate(Interview interview) {
        log.debug("Request to partially update Interview : {}", interview);

        return interviewRepository
            .findById(interview.getId())
            .map(existingInterview -> {
                if (interview.getInterviewName() != null) {
                    existingInterview.setInterviewName(interview.getInterviewName());
                }
                if (interview.getScheduledDate() != null) {
                    existingInterview.setScheduledDate(interview.getScheduledDate());
                }
                if (interview.getStartTime() != null) {
                    existingInterview.setStartTime(interview.getStartTime());
                }
                if (interview.getEndTime() != null) {
                    existingInterview.setEndTime(interview.getEndTime());
                }
                if (interview.getResceduled() != null) {
                    existingInterview.setResceduled(interview.getResceduled());
                }
                if (interview.getRescheduleDate() != null) {
                    existingInterview.setRescheduleDate(interview.getRescheduleDate());
                }
                if (interview.getRescheduleStartTime() != null) {
                    existingInterview.setRescheduleStartTime(interview.getRescheduleStartTime());
                }
                if (interview.getRescheduleEndTime() != null) {
                    existingInterview.setRescheduleEndTime(interview.getRescheduleEndTime());
                }
                if (interview.getRescheduleApproved() != null) {
                    existingInterview.setRescheduleApproved(interview.getRescheduleApproved());
                }
                if (interview.getInterviewStatus() != null) {
                    existingInterview.setInterviewStatus(interview.getInterviewStatus());
                }

                return existingInterview;
            })
            .flatMap(interviewRepository::save);
    }

    @Override
    public Flux<Interview> findAll() {
        log.debug("Request to get all Interviews");
        return interviewRepository.findAll();
    }

    public Mono<Long> countAll() {
        return interviewRepository.count();
    }

    @Override
    public Mono<Interview> findOne(String id) {
        log.debug("Request to get Interview : {}", id);
        return interviewRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Interview : {}", id);
        return interviewRepository.deleteById(id);
    }

    @Override
    public Mono<Integer> getInterviewScheduledForCandidate(String candidateId, String interviewStatus) {
        log.debug("Request to schedule interview for candidate :{} {}", candidateId, interviewStatus);
        return interviewRepository.getCountofInterviewByCandidateId(candidateId, interviewStatus);
    }

    @Override
    public Flux<Interview> getInterviewByInterviewBy(String candidateId) {
        log.debug("Request to get interviewer details");
        return interviewRepository.getInterviewByInterviewBy(candidateId);
    }

    @Override
    public Mono<Interview> getInterviewByCandidateAndStatus(String candidate, String interviewStatus) {
        log.debug("Request interview status for candidate: {} {}", candidate, interviewStatus);
        return interviewRepository.getInterviewByCandidateAndStatus(candidate, interviewStatus);
    }
}
