package com.fixmycar.service;

import com.fixmycar.exception.ValidationException;
import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.model.ServiceRequest;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.CustomerRepository;
import com.fixmycar.repository.ServiceRequestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        if (id == null) {
            throw new ValidationException("Invalid customer ID", 
                    "id", null, "Customer ID cannot be null");
        }
        
        return customerRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Customer not found", 
                        "id", id, "Customer with this ID does not exist"));
    }

    public Customer saveCustomer(Customer customer) {
        // Validate customer data
        if (customer == null) {
            throw new ValidationException("Customer cannot be null", 
                    "customer", null, "Customer object is required");
        }
        
        if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
            throw new ValidationException("Invalid customer data", 
                    "firstName", customer.getFirstName(), "First name cannot be empty");
        }
        
        if (customer.getLastName() == null || customer.getLastName().trim().isEmpty()) {
            throw new ValidationException("Invalid customer data", 
                    "lastName", customer.getLastName(), "Last name cannot be empty");
        }
        
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new ValidationException("Invalid customer data", 
                    "email", customer.getEmail(), "Email cannot be empty");
        }
        
        if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
            throw new ValidationException("Invalid customer data", 
                    "phone", customer.getPhone(), "Phone cannot be empty");
        }
        
        // Check if email is already in use
        if (customer.getId() == null) {
            customerRepository.findByEmail(customer.getEmail())
                .ifPresent(existingCustomer -> {
                    throw new ValidationException("Email already in use", 
                            "email", customer.getEmail(), 
                            "This email is already registered to another customer");
                });
        }
        
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        if (id == null) {
            throw new ValidationException("Invalid customer ID", 
                    "id", null, "Customer ID cannot be null");
        }
        
        if (customerDetails == null) {
            throw new ValidationException("Customer details cannot be null", 
                    "customerDetails", null, "Customer details are required");
        }
        
        Customer customer = getCustomerById(id);
        
        // Validate customer data
        if (customerDetails.getFirstName() == null || customerDetails.getFirstName().trim().isEmpty()) {
            throw new ValidationException("Invalid customer data", 
                    "firstName", customerDetails.getFirstName(), "First name cannot be empty");
        }
        
        if (customerDetails.getLastName() == null || customerDetails.getLastName().trim().isEmpty()) {
            throw new ValidationException("Invalid customer data", 
                    "lastName", customerDetails.getLastName(), "Last name cannot be empty");
        }
        
        if (customerDetails.getEmail() == null || customerDetails.getEmail().trim().isEmpty()) {
            throw new ValidationException("Invalid customer data", 
                    "email", customerDetails.getEmail(), "Email cannot be empty");
        }
        
        if (customerDetails.getPhone() == null || customerDetails.getPhone().trim().isEmpty()) {
            throw new ValidationException("Invalid customer data", 
                    "phone", customerDetails.getPhone(), "Phone cannot be empty");
        }
        
        // Check if email is already in use by another customer
        customerRepository.findByEmail(customerDetails.getEmail())
            .ifPresent(existingCustomer -> {
                if (!existingCustomer.getId().equals(id)) {
                    throw new ValidationException("Email already in use", 
                            "email", customerDetails.getEmail(), 
                            "This email is already registered to another customer");
                }
            });

        customer.setFirstName(customerDetails.getFirstName());
        customer.setLastName(customerDetails.getLastName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());

        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (id == null) {
            throw new ValidationException("Invalid customer ID", 
                    "id", null, "Customer ID cannot be null");
        }
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Customer not found", 
                        "id", id, "Customer with this ID does not exist"));

        List<ServiceRequest> serviceRequests = customer.getServiceRequests();
        if (serviceRequests != null && !serviceRequests.isEmpty()) {
            serviceRequestRepository.deleteAll(serviceRequests);
        }

        List<Car> cars = customer.getCars();
        if (cars != null && !cars.isEmpty()) {
            for (Car car : cars) {
                // Удаляем все заявки, связанные с автомобилем
                List<ServiceRequest> carServiceRequests = car.getServiceRequests();
                if (carServiceRequests != null && !carServiceRequests.isEmpty()) {
                    serviceRequestRepository.deleteAll(carServiceRequests);
                }
                // Удаляем автомобиль
                carRepository.delete(car);
            }
        }
        customerRepository.delete(customer);
    }

    public Customer findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Invalid email", 
                    "email", email, "Email cannot be empty");
        }
        
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Customer not found", 
                        "email", email, "No customer found with this email"));
    }
}
