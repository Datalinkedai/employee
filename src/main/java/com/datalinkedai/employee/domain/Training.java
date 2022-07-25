package com.datalinkedai.employee.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Training.
 */
@Document(collection = "training")
public class Training implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull(message = "must not be null")
    @Field("start_date")
    private LocalDate startDate;

    @Field("start_time")
    private Instant startTime;

    @Field("end_time")
    private Instant endTime;

    @NotNull(message = "must not be null")
    @Field("end_date")
    private LocalDate endDate;

    @Field("type")
    private String type;

    @Field("repeats")
    private Boolean repeats;

    @Field("candidateList")
    @JsonIgnoreProperties(value = { "internalUser" }, allowSetters = true)
    private Candidate candidateList;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Training id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public Training startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Training startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public Training endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public Training endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return this.type;
    }

    public Training type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRepeats() {
        return this.repeats;
    }

    public Training repeats(Boolean repeats) {
        this.setRepeats(repeats);
        return this;
    }

    public void setRepeats(Boolean repeats) {
        this.repeats = repeats;
    }

    public Candidate getCandidateList() {
        return this.candidateList;
    }

    public void setCandidateList(Candidate candidate) {
        this.candidateList = candidate;
    }

    public Training candidateList(Candidate candidate) {
        this.setCandidateList(candidate);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Training)) {
            return false;
        }
        return id != null && id.equals(((Training) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Training{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", type='" + getType() + "'" +
            ", repeats='" + getRepeats() + "'" +
            "}";
    }
}
