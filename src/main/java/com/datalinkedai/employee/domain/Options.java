package com.datalinkedai.employee.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Options.
 */
@Document(collection = "options")
public class Options implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("option_name")
    private String optionName;

    @Field("questions")
    @JsonIgnoreProperties(value = { "optionLists", "tested" }, allowSetters = true)
    private Questions questions;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Options id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOptionName() {
        return this.optionName;
    }

    public Options optionName(String optionName) {
        this.setOptionName(optionName);
        return this;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public Questions getQuestions() {
        return this.questions;
    }

    public void setQuestions(Questions questions) {
        this.questions = questions;
    }

    public Options questions(Questions questions) {
        this.setQuestions(questions);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Options)) {
            return false;
        }
        return id != null && id.equals(((Options) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Options{" +
            "id=" + getId() +
            ", optionName='" + getOptionName() + "'" +
            "}";
    }
}
