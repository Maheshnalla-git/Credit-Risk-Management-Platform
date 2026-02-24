package com.crm.customerservice.controller;

import com.crm.customerservice.response.CustomerDetailsResponse;
import com.crm.customerservice.response.CustomerResponse;
import com.crm.customerservice.request.CustomerRequest;
import com.crm.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Forward: UI -> Controller (CustomerRequest) -> Service -> Repo -> DB
    // Return : DB -> Repo -> Service (CustomerResponse) -> Controller -> UI
    @PostMapping
    public CustomerResponse createCustomer(@Valid @RequestBody CustomerRequest request) {
        return customerService.createCustomer(request);
    }

    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return customerService.updateCustomer(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    // ✅ NEW ENDPOINT: customer + loan details
    @GetMapping("/{id}/details")
    public CustomerDetailsResponse getCustomerDetails(@PathVariable Long id) {
        return customerService.getCustomerDetails(id);
    }
}