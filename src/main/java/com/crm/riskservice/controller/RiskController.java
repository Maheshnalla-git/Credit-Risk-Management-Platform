package com.crm.riskservice.controller;

import com.crm.riskservice.request.RiskRequest;
import com.crm.riskservice.response.RiskResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/risk")
public class RiskController {

    @PostMapping("/evaluate")
    public RiskResponse evaluate(@Valid @RequestBody RiskRequest request) {

        RiskResponse res = new RiskResponse();

        // ✅ Simple rule for now (you can improve later)
        if (request.getLoanAmount() <= 100000) {
            res.setDecision("APPROVED");
            res.setScore(720);
            res.setReasons(List.of("Loan amount is within safe limit"));
        } else {
            res.setDecision("REJECTED");
            res.setScore(580);
            res.setReasons(List.of("Loan amount too high for current risk policy"));
        }

        return res;
    }
}