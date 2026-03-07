package com.crm.loanservice.client;

import java.util.List;

public class RiskResponse {
    private String decision; // APPROVED or REJECTED
    private Integer score;
    private List<String> reasons;

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
}