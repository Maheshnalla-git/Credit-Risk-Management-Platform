package com.crm.customerservice.service;

import com.crm.customerservice.entity.Customer;
import com.crm.customerservice.exception.ResourceNotFoundException;
import com.crm.customerservice.repository.CustomerRepository;
import com.crm.customerservice.request.CustomerRequest;
import com.crm.customerservice.response.CustomerDetailsResponse;
import com.crm.customerservice.response.CustomerResponse;
import com.crm.customerservice.response.LoanResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    // Read gateway URL from application.properties
    @Value("${gateway.base-url}")
    private String gatewayBaseUrl;

    // Will be initialized after Spring injects gatewayBaseUrl
    private WebClient webClient;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // ✅ Create WebClient once after properties are loaded
    @PostConstruct
    public void init() {
        this.webClient = WebClient.create(gatewayBaseUrl);
    }

    // Forward: Request -> Entity -> DB
    // Return : DB -> Entity -> Response
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        return toResponse(customer);
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));

        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());

        Customer saved = customerRepository.save(existing);
        return toResponse(saved);
    }

    public void deleteCustomer(Long id) {
        // Optional: you can check existence first, but deleteById is fine for now
        customerRepository.deleteById(id);
    }

    // ✅ Customer + Loan (calling loan-service via API Gateway)
    public CustomerDetailsResponse getCustomerDetails(Long id) {

        // 1) Get customer from DB
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));

        // 2) Get loan from Loan Service (through gateway)
        LoanResponse loan = webClient.get()
                .uri("/loans/customer/" + id)
                .retrieve()
                .bodyToMono(LoanResponse.class)
                .block();

        // 3) Combine both
        CustomerDetailsResponse res = new CustomerDetailsResponse();
        res.setCustomer(toResponse(customer));
        res.setLoan(loan);

        return res;
    }

    // Convert Entity -> Response (Return path helper)
    private CustomerResponse toResponse(Customer customer) {
        CustomerResponse res = new CustomerResponse();
        res.setId(customer.getId());
        res.setName(customer.getName());
        res.setEmail(customer.getEmail());
        res.setPhone(customer.getPhone());
        return res;
    }
}