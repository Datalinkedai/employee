package com.datalinkedai.employee.exceptions;

public class CandidateNotFoundException extends Exception {

    public CandidateNotFoundException(String candidateId) {
        super("The Candidate is not Found with ID: " + candidateId);
    }   
}
