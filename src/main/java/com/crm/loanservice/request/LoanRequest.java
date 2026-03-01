package com.crm.loanservice.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class LoanRequest {

    @NotNull(message = "customerId is required")
    private Long customerId;

    @NotNull(message = "loanAmount is required")
    @Positive(message = "loanAmount must be greater than 0")
    private Double loanAmount;

    @NotBlank(message = "status is required")
    private String status;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(Double loanAmount) { this.loanAmount = loanAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}