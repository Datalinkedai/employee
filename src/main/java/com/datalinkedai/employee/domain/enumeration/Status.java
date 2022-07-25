package com.datalinkedai.employee.domain.enumeration;

/**
 * The Status enumeration.
 */
public enum Status {
    ACTIVE("active"),
    INACTIVE("inactive"),
    DELETED("deleted");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
