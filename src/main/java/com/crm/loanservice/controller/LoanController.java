package com.crm.loanservice.controller;

import com.crm.loanservice.request.LoanRequest;
import com.crm.loanservice.response.LoanResponse;
import com.crm.loanservice.service.LoanService;
import org.springframework.web.bind.annotation.*;
import com.crm.loanservice.request.LoanStatusUpdateRequest;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // POST: UI -> Controller -> Service -> Repository -> DB
    @PostMapping
    public LoanResponse createLoan(@Valid @RequestBody LoanRequest request) {
        return loanService.createLoan(request);
    }

    // GET: UI -> Controller -> Service -> Repository -> DB
    @GetMapping("/customer/{customerId}")
    public List<LoanResponse> getLoansByCustomerId(@PathVariable Long customerId) {
        return loanService.getLoansByCustomerId(customerId);
    }

    @PutMapping("/{loanId}/status")
    public LoanResponse updateLoanStatus(@PathVariable Long loanId,
                                         @RequestBody LoanStatusUpdateRequest request) {
        return loanService.updateLoanStatus(loanId, request.getStatus());
    }
}