package com.datalinkedai.employee.domain;

import com.datalinkedai.employee.domain.enumeration.AnswerType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Questions.
 */
@Document(collection = "questions")
public class Questions implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("question_name")
    private String questionName;

    @Field("answer_type")
    private AnswerType answerType;

    @Field("image_link")
    private byte[] imageLink;

    @Field("image_link_content_type")
    private String imageLinkContentType;

    @Field("optionList")
    @JsonIgnoreProperties(value = { "questions" }, allowSetters = true)
    private Set<Options> optionLists = new HashSet<>();

    @Field("tested")
    @JsonIgnoreProperties(value = { "questionLists" }, allowSetters = true)
    private Tested tested;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Questions id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionName() {
        return this.questionName;
    }

    public Questions questionName(String questionName) {
        this.setQuestionName(questionName);
        return this;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public AnswerType getAnswerType() {
        return this.answerType;
    }

    public Questions answerType(AnswerType answerType) {
        this.setAnswerType(answerType);
        return this;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

    public byte[] getImageLink() {
        return this.imageLink;
    }

    public Questions imageLink(byte[] imageLink) {
        this.setImageLink(imageLink);
        return this;
    }

    public void setImageLink(byte[] imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageLinkContentType() {
        return this.imageLinkContentType;
    }

    public Questions imageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
        return this;
    }

    public void setImageLinkContentType(String imageLinkContentType) {
        this.imageLinkContentType = imageLinkContentType;
    }

    public Set<Options> getOptionLists() {
        return this.optionLists;
    }

    public void setOptionLists(Set<Options> options) {
        if (this.optionLists != null) {
            this.optionLists.forEach(i -> i.setQuestions(null));
        }
        if (options != null) {
            options.forEach(i -> i.setQuestions(this));
        }
        this.optionLists = options;
    }

    public Questions optionLists(Set<Options> options) {
        this.setOptionLists(options);
        return this;
    }

    public Questions addOptionList(Options options) {
        this.optionLists.add(options);
        options.setQuestions(this);
        return this;
    }

    public Questions removeOptionList(Options options) {
        this.optionLists.remove(options);
        options.setQuestions(null);
        return this;
    }

    public Tested getTested() {
        return this.tested;
    }

    public void setTested(Tested tested) {
        this.tested = tested;
    }

    public Questions tested(Tested tested) {
        this.setTested(tested);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Questions)) {
            return false;
        }
        return id != null && id.equals(((Questions) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Questions{" +
            "id=" + getId() +
            ", questionName='" + getQuestionName() + "'" +
            ", answerType='" + getAnswerType() + "'" +
            ", imageLink='" + getImageLink() + "'" +
            ", imageLinkContentType='" + getImageLinkContentType() + "'" +
            "}";
    }
}
