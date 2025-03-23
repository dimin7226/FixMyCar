package com.fixmycar.service;

import com.fixmycar.dao.CarDao;
import com.fixmycar.dao.CustomerDao;
import com.fixmycar.dao.ServiceRequestDao;
import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.model.ServiceRequest;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerDao customerDao;
    private final CarDao carDao;
    private final ServiceRequestDao serviceRequestDao;
    
    // In-memory cache for customers by ID
    private final Map<Long, Customer> customerCache = new ConcurrentHashMap<>();
    
    // In-memory cache for customers by email
    private final Map<String, Customer> customerEmailCache = new ConcurrentHashMap<>();

    public List<Customer> getAllCustomers() {
        return customerDao.findAll();
    }

    public Customer getCustomerById(Long id) {
        // Check if customer is in cache
        if (customerCache.containsKey(id)) {
            return customerCache.get(id);
        }
        
        // If not in cache, fetch from database
        Customer customer = customerDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
                
        // Add to cache
        customerCache.put(id, customer);
        return customer;
    }

    public Customer saveCustomer(Customer customer) {
        Customer savedCustomer = customerDao.save(customer);
        
        // Update caches
        customerCache.put(savedCustomer.getId(), savedCustomer);
        if (savedCustomer.getEmail() != null) {
            customerEmailCache.put(savedCustomer.getEmail(), savedCustomer);
        }
        
        return savedCustomer;
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = getCustomerById(id);
        
        // Store old email for cache invalidation
        String oldEmail = customer.getEmail();

        customer.setFirstName(customerDetails.getFirstName());
        customer.setLastName(customerDetails.getLastName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());

        Customer savedCustomer = customerDao.save(customer);
        
        // Update caches
        customerCache.put(savedCustomer.getId(), savedCustomer);
        if (oldEmail != null) {
            customerEmailCache.remove(oldEmail);
        }
        if (savedCustomer.getEmail() != null) {
            customerEmailCache.put(savedCustomer.getEmail(), savedCustomer);
        }
        
        return savedCustomer;
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<ServiceRequest> serviceRequests = customer.getServiceRequests();
        if (serviceRequests != null && !serviceRequests.isEmpty()) {
            serviceRequestDao.deleteAll(serviceRequests);
        }

        List<Car> cars = customer.getCars();
        if (cars != null && !cars.isEmpty()) {
            for (Car car : cars) {
                // Delete all service requests related to the car
                List<ServiceRequest> carServiceRequests = car.getServiceRequests();
                if (carServiceRequests != null && !carServiceRequests.isEmpty()) {
                    serviceRequestDao.deleteAll(carServiceRequests);
                }
                // Delete the car
                carDao.delete(car);
            }
        }
        
        // Remove from caches
        customerCache.remove(id);
        if (customer.getEmail() != null) {
            customerEmailCache.remove(customer.getEmail());
        }
        
        customerDao.delete(customer);
    }

    public Customer findByEmail(String email) {
        // Check if customer is in cache
        if (customerEmailCache.containsKey(email)) {
            return customerEmailCache.get(email);
        }
        
        // If not in cache, fetch from database
        Customer customer = customerDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
                
        // Add to caches
        customerCache.put(customer.getId(), customer);
        customerEmailCache.put(email, customer);
        
        return customer;
    }
    
    /**
     * Clear all caches - useful for testing or when data consistency is required
     */
    public void clearCaches() {
        customerCache.clear();
        customerEmailCache.clear();
    }
}
