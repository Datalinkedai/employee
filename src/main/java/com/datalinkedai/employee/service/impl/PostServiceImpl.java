package com.datalinkedai.employee.service.impl;

import com.datalinkedai.employee.domain.Interview;
import com.datalinkedai.employee.domain.Post;
import com.datalinkedai.employee.repository.PostRepository;
import com.datalinkedai.employee.security.SecurityUtils;
import com.datalinkedai.employee.service.InterviewService;
import com.datalinkedai.employee.service.PostService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Post}.
 */
@Service
public class PostServiceImpl implements PostService {

    private final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Autowired
    private InterviewService interviewService;

    @Override
    public Mono<Post> save(Post post) {
        log.debug("Request to save Post : {}", post);
        return postRepository.save(post);
    }

    @Override
    public Mono<Post> update(Post post) {
        log.debug("Request to save Post : {}", post);
        return postRepository.save(post);
    }

    @Override
    public Mono<Post> partialUpdate(Post post) {
        log.debug("Request to partially update Post : {}", post);

        return postRepository
            .findById(post.getId())
            .map(existingPost -> {
                if (post.getPostName() != null) {
                    existingPost.setPostName(post.getPostName());
                }
                if (post.getDescription() != null) {
                    existingPost.setDescription(post.getDescription());
                }
                if (post.getMinimumExperience() != null) {
                    existingPost.setMinimumExperience(post.getMinimumExperience());
                }
                if (post.getMaximumExperience() != null) {
                    existingPost.setMaximumExperience(post.getMaximumExperience());
                }
                if (post.getRoles() != null) {
                    existingPost.setRoles(post.getRoles());
                }
                if (post.getResponsibility() != null) {
                    existingPost.setResponsibility(post.getResponsibility());
                }
                if (post.getStatus() != null) {
                    existingPost.setStatus(post.getStatus());
                }
                if (post.getTypeOfEmployment() != null) {
                    existingPost.setTypeOfEmployment(post.getTypeOfEmployment());
                }
                if (post.getPostedDate() != null) {
                    existingPost.setPostedDate(post.getPostedDate());
                }

                return existingPost;
            })
            .flatMap(postRepository::save);
    }

    @Override
    public Flux<Post> findAll(Pageable pageable) {
        log.debug("Request to get all Posts");
        return postRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return postRepository.count();
    }

    @Override
    public Mono<Post> findOne(String id) {
        log.debug("Request to get Post : {}", id);
        return postRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Post : {}", id);
        return postRepository.deleteById(id);
    }

    @Override
    public Mono<Interview> applyForJob(String postId) {
        String userLogin = SecurityUtils.getCurrentUserLogin().block();
        Post applyingPost = this.findOne(postId).block();
        Interview interview = new Interview();
        interview.setInterviewName(applyingPost.getPostName()+" "+userLogin);
        interview.setScheduledDate(LocalDate.now().plusDays(2));
        interview.setStartTime(Instant.now().plusSeconds(20));
        interview.endTime(Instant.now().plusSeconds(300));
        System.out.println(interview);
        // Interview interviewMono = Mono.just(interview);
        return interviewService.save(interview);
    }
}
