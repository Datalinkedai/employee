package com.datalinkedai.employee.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
// import java.util.HashSet;
import java.util.List;
// import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Tested.
 */
@Document(collection = "tested")
public class Tested implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull(message = "must not be null")
    @Field("test_name")
    private String testName;

    @Field("time_to_complete")
    private Duration timeToComplete;

    @NotNull(message = "must not be null")
    @Field("total_questions")
    private Integer totalQuestions;

    @Field("randomize")
    private Boolean randomize;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Field("passing_prcnt")
    private Double passingPrcnt;

    @Field("expiry_months")
    private Double expiryMonths;

    @Field("questionList")
    @JsonIgnoreProperties(value = { "optionLists", "tested" }, allowSetters = true)
    private List<Questions> questionLists = new ArrayList<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Tested id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestName() {
        return this.testName;
    }

    public Tested testName(String testName) {
        this.setTestName(testName);
        return this;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Duration getTimeToComplete() {
        return this.timeToComplete;
    }

    public Tested timeToComplete(Duration timeToComplete) {
        this.setTimeToComplete(timeToComplete);
        return this;
    }

    public void setTimeToComplete(Duration timeToComplete) {
        this.timeToComplete = timeToComplete;
    }

    public Integer getTotalQuestions() {
        return this.totalQuestions;
    }

    public Tested totalQuestions(Integer totalQuestions) {
        this.setTotalQuestions(totalQuestions);
        return this;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Boolean getRandomize() {
        return this.randomize;
    }

    public Tested randomize(Boolean randomize) {
        this.setRandomize(randomize);
        return this;
    }

    public void setRandomize(Boolean randomize) {
        this.randomize = randomize;
    }

    public Double getPassingPrcnt() {
        return this.passingPrcnt;
    }

    public Tested passingPrcnt(Double passingPrcnt) {
        this.setPassingPrcnt(passingPrcnt);
        return this;
    }

    public void setPassingPrcnt(Double passingPrcnt) {
        this.passingPrcnt = passingPrcnt;
    }

    public Double getExpiryMonths() {
        return this.expiryMonths;
    }

    public Tested expiryMonths(Double expiryMonths) {
        this.setExpiryMonths(expiryMonths);
        return this;
    }

    public void setExpiryMonths(Double expiryMonths) {
        this.expiryMonths = expiryMonths;
    }

    public List<Questions> getQuestionLists() {
        return this.questionLists;
    }

    public void setQuestionLists(List<Questions> questions) {
        if (this.questionLists != null) {
            this.questionLists.forEach(i -> i.setTested(null));
        }
        if (questions != null) {
            questions.forEach(i -> i.setTested(this));
        }
        this.questionLists = questions;
    }

    public Tested questionLists(List<Questions> questions) {
        this.setQuestionLists(questions);
        return this;
    }

    public Tested addQuestionList(Questions questions) {
        this.questionLists.add(questions);
        questions.setTested(this);
        return this;
    }

    public Tested removeQuestionList(Questions questions) {
        this.questionLists.remove(questions);
        questions.setTested(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tested)) {
            return false;
        }
        return id != null && id.equals(((Tested) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tested{" +
            "id=" + getId() +
            ", testName='" + getTestName() + "'" +
            ", timeToComplete='" + getTimeToComplete() + "'" +
            ", totalQuestions=" + getTotalQuestions() +
            ", randomize='" + getRandomize() + "'" +
            ", passingPrcnt=" + getPassingPrcnt() +
            ", expiryMonths=" + getExpiryMonths() +
            "}";
    }
}
