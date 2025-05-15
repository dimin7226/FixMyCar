package com.fixmycar.service;

import com.fixmycar.cache.InMemoryCache;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Customer;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.CustomerRepository;
import com.fixmycar.repository.ServiceRequestRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final InMemoryCache customerCache;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {


        String cacheKey = "albums_id_" + id;
        if (customerCache.containsKey(cacheKey)) {
            return Optional.of((Customer) customerCache.get(cacheKey));
        }
        Customer album = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Не найден альбом с ID = " + id));
        customerCache.put(cacheKey, album);
        return Optional.of(album);
    }

    public Customer saveOrUpdateCustomer(Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);

       // customerCache.put(savedCustomer.getId(), savedCustomer);
        return savedCustomer;
    }

    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    public boolean existsByPhone(String phone) {
        return customerRepository.existsByPhone(phone);
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        return customerRepository.existsByEmailAndIdNot(email, id);
    }

    public boolean existsByPhoneAndIdNot(String phone, Long id) {
        return customerRepository.existsByPhoneAndIdNot(phone, id);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);

        customerCache.clear();
    }
}