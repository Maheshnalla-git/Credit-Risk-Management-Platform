package com.crm.loanservice.service;

import com.crm.loanservice.client.RiskResponse;
import com.crm.loanservice.entity.Loan;
import com.crm.loanservice.exception.LoanRejectedException;
import com.crm.loanservice.exception.ResourceNotFoundException;
import com.crm.loanservice.repository.LoanRepository;
import com.crm.loanservice.request.LoanRequest;
import com.crm.loanservice.response.LoanResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    // Read gateway URL from application.properties
    @Value("${gateway.base-url}")
    private String gatewayBaseUrl;

    // WebClient initialized after gatewayBaseUrl is loaded
    private WebClient webClient;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @PostConstruct
    public void init() {
        this.webClient = WebClient.create(gatewayBaseUrl);
    }

    // Forward: Request -> (Risk Check) -> Entity -> DB
    // Return : DB -> Entity -> Response
    public LoanResponse createLoan(LoanRequest request) {

        // 1) Call risk-service via API Gateway
        RiskResponse risk = webClient.post()
                .uri("/risk/evaluate")
                .bodyValue(request) // customerId + loanAmount used by risk-service
                .retrieve()
                .bodyToMono(RiskResponse.class)
                .block();

        // 2) If rejected, do NOT save loan
        if (risk != null && "REJECTED".equalsIgnoreCase(risk.getDecision())) {
            throw new LoanRejectedException("Loan rejected by risk-service: " + risk.getReasons());
        }

        // 3) If approved, save loan
        Loan loan = new Loan();
        loan.setCustomerId(request.getCustomerId());
        loan.setLoanAmount(request.getLoanAmount());
        loan.setStatus(request.getStatus());

        Loan saved = loanRepository.save(loan);
        return toResponse(saved);
    }

    public List<LoanResponse> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LoanResponse updateLoanStatus(Long loanId, String status) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + loanId));

        loan.setStatus(status);

        Loan saved = loanRepository.save(loan);
        return toResponse(saved);
    }

    private LoanResponse toResponse(Loan loan) {
        LoanResponse response = new LoanResponse();
        response.setId(loan.getId());
        response.setCustomerId(loan.getCustomerId());
        response.setLoanAmount(loan.getLoanAmount());
        response.setStatus(loan.getStatus());
        return response;
    }
}