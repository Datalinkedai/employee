package com.datalinkedai.employee.domain.enumeration;

/**
 * The AnswerType enumeration.
 */
public enum AnswerType {
    MCQ("mcq"),
    TEXT("text");

    private final String value;

    AnswerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
