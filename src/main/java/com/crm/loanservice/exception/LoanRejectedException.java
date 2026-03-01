package com.crm.loanservice.exception;

public class LoanRejectedException extends RuntimeException {
    public LoanRejectedException(String message) {
        super(message);
    }
}