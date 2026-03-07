package com.crm.loanservice.controller;

import com.crm.loanservice.entity.LoanStatus;
import com.crm.loanservice.request.LoanRequest;
import com.crm.loanservice.request.LoanStatusUpdateRequest;
import com.crm.loanservice.response.LoanResponse;
import com.crm.loanservice.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // ✅ POST: Create loan
    @PostMapping
    public LoanResponse createLoan(@Valid @RequestBody LoanRequest request) {
        return loanService.createLoan(request);
    }

    // ✅ GET: Get all loans for a customer
    @GetMapping("/customer/{customerId}")
    public List<LoanResponse> getLoansByCustomerId(@PathVariable Long customerId) {
        return loanService.getLoansByCustomerId(customerId);
    }

    // ✅ GET: Get a single loan by loanId
    @GetMapping("/{loanId}")
    public LoanResponse getLoanById(@PathVariable Long loanId) {
        return loanService.getLoanById(loanId);
    }

    // ✅ PUT: Update loan status
    @PutMapping("/{loanId}/status")
    public LoanResponse updateLoanStatus(@PathVariable Long loanId,
                                         @Valid @RequestBody LoanStatusUpdateRequest request) {
        // If request.getStatus() returns String, convert to enum:
        LoanStatus status = LoanStatus.valueOf(request.getStatus().toUpperCase());
        return loanService.updateLoanStatus(loanId, status);
    }
}