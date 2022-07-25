package com.datalinkedai.employee.domain;

import com.datalinkedai.employee.domain.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Post.
 */
@Document(collection = "post")
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull(message = "must not be null")
    @Size(min = 5)
    @Field("post_name")
    private String postName;

    @Field("description")
    private String description;

    @Field("minimum_experience")
    private Double minimumExperience;

    @Field("maximum_experience")
    private Double maximumExperience;

    @Field("roles")
    private String roles;

    @Field("responsibility")
    private String responsibility;

    @Field("status")
    private Status status;

    @Field("type_of_employment")
    private String typeOfEmployment;

    @Field("posted_date")
    private Instant postedDate;

    @Field("postedBy")
    private Candidate postedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Post id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostName() {
        return this.postName;
    }

    public Post postName(String postName) {
        this.setPostName(postName);
        return this;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getDescription() {
        return this.description;
    }

    public Post description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMinimumExperience() {
        return this.minimumExperience;
    }

    public Post minimumExperience(Double minimumExperience) {
        this.setMinimumExperience(minimumExperience);
        return this;
    }

    public void setMinimumExperience(Double minimumExperience) {
        this.minimumExperience = minimumExperience;
    }

    public Double getMaximumExperience() {
        return this.maximumExperience;
    }

    public Post maximumExperience(Double maximumExperience) {
        this.setMaximumExperience(maximumExperience);
        return this;
    }

    public void setMaximumExperience(Double maximumExperience) {
        this.maximumExperience = maximumExperience;
    }

    public String getRoles() {
        return this.roles;
    }

    public Post roles(String roles) {
        this.setRoles(roles);
        return this;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getResponsibility() {
        return this.responsibility;
    }

    public Post responsibility(String responsibility) {
        this.setResponsibility(responsibility);
        return this;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    public Status getStatus() {
        return this.status;
    }

    public Post status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTypeOfEmployment() {
        return this.typeOfEmployment;
    }

    public Post typeOfEmployment(String typeOfEmployment) {
        this.setTypeOfEmployment(typeOfEmployment);
        return this;
    }

    public void setTypeOfEmployment(String typeOfEmployment) {
        this.typeOfEmployment = typeOfEmployment;
    }

    public Instant getPostedDate() {
        return this.postedDate;
    }

    public Post postedDate(Instant postedDate) {
        this.setPostedDate(postedDate);
        return this;
    }

    public void setPostedDate(Instant postedDate) {
        this.postedDate = postedDate;
    }

    public Candidate getPostedBy() {
        return this.postedBy;
    }

    public void setPostedBy(Candidate candidate) {
        this.postedBy = candidate;
    }

    public Post postedBy(Candidate candidate) {
        this.setPostedBy(candidate);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }
        return id != null && id.equals(((Post) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Post{" +
            "id=" + getId() +
            ", postName='" + getPostName() + "'" +
            ", description='" + getDescription() + "'" +
            ", minimumExperience=" + getMinimumExperience() +
            ", maximumExperience=" + getMaximumExperience() +
            ", roles='" + getRoles() + "'" +
            ", responsibility='" + getResponsibility() + "'" +
            ", status='" + getStatus() + "'" +
            ", typeOfEmployment='" + getTypeOfEmployment() + "'" +
            ", postedDate='" + getPostedDate() + "'" +
            "}";
    }
}
