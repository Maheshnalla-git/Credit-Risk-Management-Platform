package com.crm.customerservice.response;

public class CustomerDetailsResponse {

    private CustomerResponse customer;
    private LoanResponse loan;

    public CustomerResponse getCustomer() { return customer; }
    public void setCustomer(CustomerResponse customer) { this.customer = customer; }

    public LoanResponse getLoan() { return loan; }
    public void setLoan(LoanResponse loan) { this.loan = loan; }
}