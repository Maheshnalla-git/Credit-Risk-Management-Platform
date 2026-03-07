package com.crm.loanservice.service;

import com.crm.loanservice.client.RiskRequest;
import com.crm.loanservice.client.RiskResponse;
import com.crm.loanservice.entity.Loan;
import com.crm.loanservice.entity.LoanStatus;
import com.crm.loanservice.exception.ResourceNotFoundException;
import com.crm.loanservice.repository.LoanRepository;
import com.crm.loanservice.request.LoanRequest;
import com.crm.loanservice.response.LoanResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    @Value("${gateway.base-url}")
    private String gatewayBaseUrl;

    private WebClient webClient;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @PostConstruct
    public void init() {
        this.webClient = WebClient.create(gatewayBaseUrl);
    }

    // POST /loans
    public LoanResponse createLoan(LoanRequest request) {

        // 1) Prepare risk request
        RiskRequest riskReq = new RiskRequest();
        riskReq.setCustomerId(request.getCustomerId());
        riskReq.setLoanAmount(request.getLoanAmount());

        // 2) Call risk-service via gateway
        RiskResponse risk;
        try {
            risk = webClient.post()
                    .uri("/risk/evaluate")
                    .bodyValue(riskReq)
                    .retrieve()
                    .bodyToMono(RiskResponse.class)
                    .timeout(Duration.ofSeconds(2))
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Risk service error: " + e.getStatusCode());
        } catch (Exception e) {
            // service down / timeout
            throw new RuntimeException("Risk service unavailable. Try again later.");
        }

        // 3) If rejected → do not save
        if (risk != null && "REJECTED".equalsIgnoreCase(risk.getDecision())) {
            throw new IllegalArgumentException("Loan rejected by risk-service: " + risk.getReasons());
        }

        // 4) Save loan
        Loan loan = new Loan();
        loan.setCustomerId(request.getCustomerId());
        loan.setLoanAmount(request.getLoanAmount());
        loan.setStatus(LoanStatus.APPROVED);

        Loan saved = loanRepository.save(loan);
        return toResponse(saved);
    }

    // GET /loans/customer/{id}
    public List<LoanResponse> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET /loans/{id}
    public LoanResponse getLoanById(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + loanId));
        return toResponse(loan);
    }

    // PUT /loans/{id}/status
    public LoanResponse updateLoanStatus(Long loanId, LoanStatus status) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + loanId));

        loan.setStatus(status);
        Loan saved = loanRepository.save(loan);
        return toResponse(saved);
    }

    private LoanResponse toResponse(Loan loan) {
        LoanResponse res = new LoanResponse();
        res.setId(loan.getId());
        res.setCustomerId(loan.getCustomerId());
        res.setLoanAmount(loan.getLoanAmount());
        res.setStatus(loan.getStatus().name());
        return res;
    }
}