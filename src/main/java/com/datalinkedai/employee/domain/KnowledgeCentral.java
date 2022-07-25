package com.datalinkedai.employee.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A KnowledgeCentral.
 */
@Document(collection = "knowledge_central")
public class KnowledgeCentral implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Field("average_result")
    private Double averageResult;

    @Field("candidateTaken")
    private Candidate candidateTaken;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public KnowledgeCentral id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAverageResult() {
        return this.averageResult;
    }

    public KnowledgeCentral averageResult(Double averageResult) {
        this.setAverageResult(averageResult);
        return this;
    }

    public void setAverageResult(Double averageResult) {
        this.averageResult = averageResult;
    }

    public Candidate getCandidateTaken() {
        return this.candidateTaken;
    }

    public void setCandidateTaken(Candidate candidate) {
        this.candidateTaken = candidate;
    }

    public KnowledgeCentral candidateTaken(Candidate candidate) {
        this.setCandidateTaken(candidate);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KnowledgeCentral)) {
            return false;
        }
        return id != null && id.equals(((KnowledgeCentral) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KnowledgeCentral{" +
            "id=" + getId() +
            ", averageResult=" + getAverageResult() +
            "}";
    }
}
