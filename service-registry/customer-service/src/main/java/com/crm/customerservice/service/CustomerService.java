package com.crm.customerservice.service;

import com.crm.customerservice.entity.Customer;
import com.crm.customerservice.repository.CustomerRepository;
import com.crm.customerservice.request.CustomerRequest;
import com.crm.customerservice.response.CustomerResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));
        return toResponse(customer);
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));

        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());

        Customer saved = customerRepository.save(existing);
        return toResponse(saved);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
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