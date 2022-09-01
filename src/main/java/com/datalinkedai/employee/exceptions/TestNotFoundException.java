package com.datalinkedai.employee.exceptions;

import com.datalinkedai.employee.domain.Tested;

public class TestNotFoundException extends Exception {

    public TestNotFoundException(String testedName) {
        super("The Test is not found with name" + testedName);
    }
}
