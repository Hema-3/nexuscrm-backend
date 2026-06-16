package com.nexuscrm.backend.controller;

import com.nexuscrm.backend.model.Customer;
import com.nexuscrm.backend.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public ResponseEntity<?> getCustomers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Customer> customerPage;

        if (search != null && !search.trim().isEmpty()) {
            customerPage = customerRepository.searchCustomers(search.trim(), pageable);
        } else {
            customerPage = customerRepository.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", customerPage.getContent());
        response.put("count", customerPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        if (customer.getEmail() != null && customerRepository.existsByEmail(customer.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "A customer with this email already exists"));
        }
        return ResponseEntity.ok(customerRepository.save(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
        return customerRepository.findById(id)
                .map(customer -> {
                    // Check if email is being changed to an existing one
                    if (!customer.getEmail().equalsIgnoreCase(customerDetails.getEmail()) &&
                        customerRepository.existsByEmail(customerDetails.getEmail())) {
                        return ResponseEntity.badRequest().body(Map.of("error", "A customer with this email already exists"));
                    }
                    customer.setFullName(customerDetails.getFullName());
                    customer.setCompany(customerDetails.getCompany());
                    customer.setEmail(customerDetails.getEmail());
                    customer.setStatus(customerDetails.getStatus());
                    return ResponseEntity.ok(customerRepository.save(customer));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customerRepository.delete(customer);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
