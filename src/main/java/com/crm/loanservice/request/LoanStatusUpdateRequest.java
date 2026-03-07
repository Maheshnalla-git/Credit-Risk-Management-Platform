package com.crm.loanservice.request;

import jakarta.validation.constraints.NotBlank;

public class LoanStatusUpdateRequest {

    @NotBlank(message = "status is required")
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}