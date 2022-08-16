package com.datalinkedai.employee.domain.enumeration;

/**
 * The InterviewStatus enumeration.
 */
public enum InterviewStatus {
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    SCHEDULED("scheduled"),
    ONHOLD("onhold");

    private final String value;

    InterviewStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
