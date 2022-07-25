package com.datalinkedai.employee.domain;

import com.datalinkedai.employee.domain.enumeration.Status;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Candidate.
 */
@Document(collection = "candidate")
public class Candidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[a-zA-Z]+$")
    @Field("first_name")
    private String firstName;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[a-zA-Z]+$")
    @Field("last_name")
    private String lastName;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^\\d{10}$")
    @Field("phone_number")
    private String phoneNumber;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,40}$")
    @Field("user_name")
    private String userName;

    @Field("eduction_qualification")
    private String eductionQualification;

    @Field("resume_link")
    private String resumeLink;

    @Field("status")
    private Status status;

    @Field("internalUser")
    private User internalUser;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Candidate id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Candidate firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Candidate lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Candidate phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return this.userName;
    }

    public Candidate userName(String userName) {
        this.setUserName(userName);
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEductionQualification() {
        return this.eductionQualification;
    }

    public Candidate eductionQualification(String eductionQualification) {
        this.setEductionQualification(eductionQualification);
        return this;
    }

    public void setEductionQualification(String eductionQualification) {
        this.eductionQualification = eductionQualification;
    }

    public String getResumeLink() {
        return this.resumeLink;
    }

    public Candidate resumeLink(String resumeLink) {
        this.setResumeLink(resumeLink);
        return this;
    }

    public void setResumeLink(String resumeLink) {
        this.resumeLink = resumeLink;
    }

    public Status getStatus() {
        return this.status;
    }

    public Candidate status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getInternalUser() {
        return this.internalUser;
    }

    public void setInternalUser(User user) {
        this.internalUser = user;
    }

    public Candidate internalUser(User user) {
        this.setInternalUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Candidate)) {
            return false;
        }
        return id != null && id.equals(((Candidate) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Candidate{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", userName='" + getUserName() + "'" +
            ", eductionQualification='" + getEductionQualification() + "'" +
            ", resumeLink='" + getResumeLink() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
