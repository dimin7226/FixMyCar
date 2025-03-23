package com.fixmycar.controller;

import com.fixmycar.exception.InvalidInputException;
import com.fixmycar.model.Customer;
import com.fixmycar.service.CustomerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        if (id <= 0) {
            throw new InvalidInputException("Customer ID must be a positive number");
        }
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        if (customer == null) {
            throw new InvalidInputException("Customer details cannot be null");
        }
        return ResponseEntity.ok(customerService.saveCustomer(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id,
                                                   @RequestBody Customer customer) {
        if (id <= 0) {
            throw new InvalidInputException("Customer ID must be a positive number");
        }
        if (customer == null) {
            throw new InvalidInputException("Customer details cannot be null");
        }
        customer.setId(id);
        return ResponseEntity.ok(customerService.saveCustomer(customer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (id <= 0) {
            throw new InvalidInputException("Customer ID must be a positive number");
        }
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email cannot be null or empty");
        }
        return ResponseEntity.ok(customerService.findByEmail(email));
    }
}
