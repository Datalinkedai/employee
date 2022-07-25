package com.datalinkedai.employee.domain.enumeration;

/**
 * The DocumentType enumeration.
 */
public enum DocumentType {
    IMAGE("image"),
    CERTIFICATE("certificate");

    private final String value;

    DocumentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
