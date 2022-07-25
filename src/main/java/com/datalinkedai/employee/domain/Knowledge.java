package com.datalinkedai.employee.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Knowledge.
 */
@Document(collection = "knowledge")
public class Knowledge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Field("result")
    private Double result;

    @NotNull(message = "must not be null")
    @Field("test_taken")
    private Instant testTaken;

    @Field("certificate")
    private byte[] certificate;

    @Field("certificate_content_type")
    private String certificateContentType;

    @Field("tests")
    private Tested tests;

    @Field("candidateTaken")
    private Candidate candidateTaken;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Knowledge id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getResult() {
        return this.result;
    }

    public Knowledge result(Double result) {
        this.setResult(result);
        return this;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    public Instant getTestTaken() {
        return this.testTaken;
    }

    public Knowledge testTaken(Instant testTaken) {
        this.setTestTaken(testTaken);
        return this;
    }

    public void setTestTaken(Instant testTaken) {
        this.testTaken = testTaken;
    }

    public byte[] getCertificate() {
        return this.certificate;
    }

    public Knowledge certificate(byte[] certificate) {
        this.setCertificate(certificate);
        return this;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }

    public String getCertificateContentType() {
        return this.certificateContentType;
    }

    public Knowledge certificateContentType(String certificateContentType) {
        this.certificateContentType = certificateContentType;
        return this;
    }

    public void setCertificateContentType(String certificateContentType) {
        this.certificateContentType = certificateContentType;
    }

    public Tested getTests() {
        return this.tests;
    }

    public void setTests(Tested tested) {
        this.tests = tested;
    }

    public Knowledge tests(Tested tested) {
        this.setTests(tested);
        return this;
    }

    public Candidate getCandidateTaken() {
        return this.candidateTaken;
    }

    public void setCandidateTaken(Candidate candidate) {
        this.candidateTaken = candidate;
    }

    public Knowledge candidateTaken(Candidate candidate) {
        this.setCandidateTaken(candidate);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Knowledge)) {
            return false;
        }
        return id != null && id.equals(((Knowledge) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Knowledge{" +
            "id=" + getId() +
            ", result=" + getResult() +
            ", testTaken='" + getTestTaken() + "'" +
            ", certificate='" + getCertificate() + "'" +
            ", certificateContentType='" + getCertificateContentType() + "'" +
            "}";
    }
}
