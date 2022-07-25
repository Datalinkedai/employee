package com.datalinkedai.employee.service.impl;

import com.datalinkedai.employee.domain.Documents;
import com.datalinkedai.employee.repository.DocumentsRepository;
import com.datalinkedai.employee.service.DocumentsService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Documents}.
 */
@Service
public class DocumentsServiceImpl implements DocumentsService {

    private final Logger log = LoggerFactory.getLogger(DocumentsServiceImpl.class);

    private final DocumentsRepository documentsRepository;

    public DocumentsServiceImpl(DocumentsRepository documentsRepository) {
        this.documentsRepository = documentsRepository;
    }

    @Override
    public Mono<Documents> save(Documents documents) {
        log.debug("Request to save Documents : {}", documents);
        return documentsRepository.save(documents);
    }

    @Override
    public Mono<Documents> update(Documents documents) {
        log.debug("Request to save Documents : {}", documents);
        return documentsRepository.save(documents);
    }

    @Override
    public Mono<Documents> partialUpdate(Documents documents) {
        log.debug("Request to partially update Documents : {}", documents);

        return documentsRepository
            .findById(documents.getId())
            .map(existingDocuments -> {
                if (documents.getDocumentType() != null) {
                    existingDocuments.setDocumentType(documents.getDocumentType());
                }
                if (documents.getDocument() != null) {
                    existingDocuments.setDocument(documents.getDocument());
                }
                if (documents.getDocumentContentType() != null) {
                    existingDocuments.setDocumentContentType(documents.getDocumentContentType());
                }
                if (documents.getDocumentLink() != null) {
                    existingDocuments.setDocumentLink(documents.getDocumentLink());
                }
                if (documents.getDocumentExpiry() != null) {
                    existingDocuments.setDocumentExpiry(documents.getDocumentExpiry());
                }
                if (documents.getVerified() != null) {
                    existingDocuments.setVerified(documents.getVerified());
                }

                return existingDocuments;
            })
            .flatMap(documentsRepository::save);
    }

    @Override
    public Flux<Documents> findAll() {
        log.debug("Request to get all Documents");
        return documentsRepository.findAll();
    }

    public Mono<Long> countAll() {
        return documentsRepository.count();
    }

    @Override
    public Mono<Documents> findOne(String id) {
        log.debug("Request to get Documents : {}", id);
        return documentsRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Documents : {}", id);
        return documentsRepository.deleteById(id);
    }
}
