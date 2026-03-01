package com.crm.customerservice.response;

import java.util.List;

public class CustomerDetailsResponse {

    private CustomerResponse customer;
    private List<LoanResponse> loans;

    public CustomerResponse getCustomer() { return customer; }
    public void setCustomer(CustomerResponse customer) { this.customer = customer; }

    public List<LoanResponse> getLoans() { return loans; }
    public void setLoans(List<LoanResponse> loans) { this.loans = loans; }
}