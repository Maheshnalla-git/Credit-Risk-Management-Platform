package com.crm.riskservice.response;

import java.util.List;

public class RiskResponse {
    private String decision;   // APPROVED / REJECTED
    private Integer score;     // example: 720
    private List<String> reasons;

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
}