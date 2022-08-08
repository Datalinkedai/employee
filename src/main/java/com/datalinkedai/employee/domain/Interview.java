package com.datalinkedai.employee.domain;

import com.datalinkedai.employee.domain.enumeration.InterviewStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Interview.
 */
@Document(collection = "interview")
public class Interview extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("interview_name")
    private String interviewName;

    @NotNull(message = "must not be null")
    @Field("scheduled_date")
    private LocalDate scheduledDate;

    @Field("start_time")
    private Instant startTime;

    @Field("end_time")
    private Instant endTime;

    @Field("resceduled")
    private Integer resceduled;

    @Field("reschedule_date")
    private LocalDate rescheduleDate;

    @Field("reschedule_start_time")
    private Instant rescheduleStartTime;

    @Field("reschedule_end_t_ime")
    private Instant rescheduleEndTIme;

    @Field("reschedule_approved")
    private Boolean rescheduleApproved;

    @Field("interview_status")
    private InterviewStatus interviewStatus;

    @Field("interviewBy")
    private Candidate interviewBy;

    @Field("rescheduleApprovedBy")
    private Candidate rescheduleApprovedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Interview id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInterviewName() {
        return this.interviewName;
    }

    public Interview interviewName(String interviewName) {
        this.setInterviewName(interviewName);
        return this;
    }

    public void setInterviewName(String interviewName) {
        this.interviewName = interviewName;
    }

    public LocalDate getScheduledDate() {
        return this.scheduledDate;
    }

    public Interview scheduledDate(LocalDate scheduledDate) {
        this.setScheduledDate(scheduledDate);
        return this;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Interview startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public Interview endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Integer getResceduled() {
        return this.resceduled;
    }

    public Interview resceduled(Integer resceduled) {
        this.setResceduled(resceduled);
        return this;
    }

    public void setResceduled(Integer resceduled) {
        this.resceduled = resceduled;
    }

    public LocalDate getRescheduleDate() {
        return this.rescheduleDate;
    }

    public Interview rescheduleDate(LocalDate rescheduleDate) {
        this.setRescheduleDate(rescheduleDate);
        return this;
    }

    public void setRescheduleDate(LocalDate rescheduleDate) {
        this.rescheduleDate = rescheduleDate;
    }

    public Instant getRescheduleStartTime() {
        return this.rescheduleStartTime;
    }

    public Interview rescheduleStartTime(Instant rescheduleStartTime) {
        this.setRescheduleStartTime(rescheduleStartTime);
        return this;
    }

    public void setRescheduleStartTime(Instant rescheduleStartTime) {
        this.rescheduleStartTime = rescheduleStartTime;
    }

    public Instant getRescheduleEndTIme() {
        return this.rescheduleEndTIme;
    }

    public Interview rescheduleEndTIme(Instant rescheduleEndTIme) {
        this.setRescheduleEndTIme(rescheduleEndTIme);
        return this;
    }

    public void setRescheduleEndTIme(Instant rescheduleEndTIme) {
        this.rescheduleEndTIme = rescheduleEndTIme;
    }

    public Boolean getRescheduleApproved() {
        return this.rescheduleApproved;
    }

    public Interview rescheduleApproved(Boolean rescheduleApproved) {
        this.setRescheduleApproved(rescheduleApproved);
        return this;
    }

    public void setRescheduleApproved(Boolean rescheduleApproved) {
        this.rescheduleApproved = rescheduleApproved;
    }

    public InterviewStatus getInterviewStatus() {
        return this.interviewStatus;
    }

    public Interview interviewStatus(InterviewStatus interviewStatus) {
        this.setInterviewStatus(interviewStatus);
        return this;
    }

    public void setInterviewStatus(InterviewStatus interviewStatus) {
        this.interviewStatus = interviewStatus;
    }

    public Candidate getInterviewBy() {
        return this.interviewBy;
    }

    public void setInterviewBy(Candidate candidate) {
        this.interviewBy = candidate;
    }

    public Interview interviewBy(Candidate candidate) {
        this.setInterviewBy(candidate);
        return this;
    }

    public Candidate getRescheduleApprovedBy() {
        return this.rescheduleApprovedBy;
    }

    public void setRescheduleApprovedBy(Candidate candidate) {
        this.rescheduleApprovedBy = candidate;
    }

    public Interview rescheduleApprovedBy(Candidate candidate) {
        this.setRescheduleApprovedBy(candidate);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Interview)) {
            return false;
        }
        return id != null && id.equals(((Interview) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Interview{" +
            "id=" + getId() +
            ", interviewName='" + getInterviewName() + "'" +
            ", scheduledDate='" + getScheduledDate() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", resceduled=" + getResceduled() +
            ", rescheduleDate='" + getRescheduleDate() + "'" +
            ", rescheduleStartTime='" + getRescheduleStartTime() + "'" +
            ", rescheduleEndTIme='" + getRescheduleEndTIme() + "'" +
            ", rescheduleApproved='" + getRescheduleApproved() + "'" +
            ", interviewStatus='" + getInterviewStatus() + "'" +
            "}";
    }
}
