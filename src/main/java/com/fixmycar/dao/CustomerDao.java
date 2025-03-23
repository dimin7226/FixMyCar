package com.fixmycar.dao;

import com.fixmycar.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    /**
     * Find all customers
     * @return List of all customers
     */
    List<Customer> findAll();
    
    /**
     * Find a customer by its ID
     * @param id The ID of the customer
     * @return Optional containing the customer if found
     */
    Optional<Customer> findById(Long id);
    
    /**
     * Find a customer by email
     * @param email The email of the customer
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * Save a customer entity
     * @param customer The customer to save
     * @return The saved customer
     */
    Customer save(Customer customer);
    
    /**
     * Delete a customer entity
     * @param customer The customer to delete
     */
    void delete(Customer customer);
    
    /**
     * Delete a customer entity by its ID
     * @param id The ID of the customer to delete
     */
    void deleteById(Long id);
}
