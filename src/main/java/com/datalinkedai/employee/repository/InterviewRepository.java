package com.datalinkedai.employee.repository;

import com.datalinkedai.employee.domain.Interview;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

// import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Interview entity.
 */
// @SuppressWarnings("unused")
@Repository
public interface InterviewRepository extends ReactiveMongoRepository<Interview, String> {

    @Query(value= "interviewFor: ?0, interview_status: ?1")
    Mono<Interview> getInterviewByCandidateAndStatus(String candidate, String interviewStatus);

    @Query(value= "interviewFor: ?0, interview_status: ?1", count= true)
    Mono<Integer> getCountofInterviewByCandidateId(String candidateId, String interviewStatus);

    Flux<Interview> getInterviewByInterviewBy(String candidateId);

    Flux<Interview> getInterviewByScheduledDate(LocalDate scheduledDate);
}
