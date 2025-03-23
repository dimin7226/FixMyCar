package com.fixmycar.service;

import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.model.ServiceRequest;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.CustomerRepository;
import com.fixmycar.repository.ServiceCenterRepository;
import com.fixmycar.repository.ServiceRequestRepository;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final ServiceCenterRepository serviceCenterRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    
    // In-memory cache for cars by service center name
    private final Map<String, List<Car>> serviceCenterCarCache = new ConcurrentHashMap<>();
    
    // In-memory cache for cars by customer ID
    private final Map<Long, List<Car>> customerCarCache = new ConcurrentHashMap<>();
    
    // In-memory cache for cars by ID
    private final Map<Long, Car> carCache = new ConcurrentHashMap<>();

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Long id) {
        // Check if car is in cache
        if (carCache.containsKey(id)) {
            return carCache.get(id);
        }
        
        // If not in cache, fetch from database
        Car car = carRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Машина не найдена"));
                
        // Add to cache
        carCache.put(id, car);
        return car;
    }

    public Car saveCar(Car car) {
        // Проверяем, есть ли у машины клиент
        if (car.getCustomer() != null && car.getCustomer().getId() != null) {
            Customer customer = customerRepository.findById(car.getCustomer().getId())
                    .orElseThrow(() -> new RuntimeException("Клиент не найден"));
            car.setCustomer(customer);
        }
        
        Car savedCar = carRepository.save(car);
        
        // Update cache
        carCache.put(savedCar.getId(), savedCar);
        
        // Invalidate related caches since the data has changed
        if (car.getCustomer() != null) {
            customerCarCache.remove(car.getCustomer().getId());
        }
        for (ServiceCenter sc : car.getServiceCenters()) {
            serviceCenterCarCache.remove(sc.getName());
        }
        
        return savedCar;
    }

    public Car updateCarInfo(Long carId, Car updatedCar) {
        Car existingCar = getCarById(carId);

        existingCar.setBrand(updatedCar.getBrand());
        existingCar.setModel(updatedCar.getModel());
        existingCar.setVin(updatedCar.getVin());
        existingCar.setYear(updatedCar.getYear());

        Car savedCar = carRepository.save(existingCar);
        
        // Update cache
        carCache.put(savedCar.getId(), savedCar);
        
        // Invalidate related caches
        if (savedCar.getCustomer() != null) {
            customerCarCache.remove(savedCar.getCustomer().getId());
        }
        for (ServiceCenter sc : savedCar.getServiceCenters()) {
            serviceCenterCarCache.remove(sc.getName());
        }
        
        return savedCar;
    }

    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        List<ServiceRequest> serviceRequests = car.getServiceRequests();
        if (serviceRequests != null && !serviceRequests.isEmpty()) {
            serviceRequestRepository.deleteAll(serviceRequests);
        }

        carRepository.delete(car);
        
        // Remove from caches
        carCache.remove(id);
        if (car.getCustomer() != null) {
            customerCarCache.remove(car.getCustomer().getId());
        }
        for (ServiceCenter sc : car.getServiceCenters()) {
            serviceCenterCarCache.remove(sc.getName());
        }
    }

    @Transactional
    public Car transferOwnership(Long carId, Long newCustomerId) {
        Car car = getCarById(carId);
        Customer newOwner = customerRepository.findById(newCustomerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found with id: " + newCustomerId));

        // If car already has an owner, remove car from owner's cars list
        if (car.getCustomer() != null) {
            // Invalidate old owner's cache
            customerCarCache.remove(car.getCustomer().getId());
            car.getCustomer().getCars().remove(car);
        }

        // Set new owner
        car.setCustomer(newOwner);

        // Add car to new owner's cars list
        newOwner.getCars().add(car);

        Car savedCar = carRepository.save(car);
        
        // Update caches
        carCache.put(savedCar.getId(), savedCar);
        customerCarCache.remove(newCustomerId);
        
        return savedCar;
    }

    public List<Car> getCarsByCustomerId(Long customerId) {
        // Check if in cache
        if (customerCarCache.containsKey(customerId)) {
            return customerCarCache.get(customerId);
        }
        
        // If not in cache, fetch from database
        List<Car> cars = carRepository.findByCustomerId(customerId);
        
        // Add to cache
        customerCarCache.put(customerId, cars);
        
        return cars;
    }

    public List<Car> getCarsByServiceCenterId(Long serviceCenterId) {
        return carRepository.findWithCustomerAndServiceCentersByServiceCentersId(serviceCenterId);
    }
    
    public List<Car> getCarsByServiceCenterName(String serviceCenterName) {
        // Check if in cache
        if (serviceCenterCarCache.containsKey(serviceCenterName)) {
            return serviceCenterCarCache.get(serviceCenterName);
        }
        
        // If not in cache, fetch from database using the custom query
        List<Car> cars = carRepository.findByServiceCentersName(serviceCenterName);
        
        // Add to cache
        serviceCenterCarCache.put(serviceCenterName, cars);
        
        return cars;
    }

    public Car assignToCustomer(Car car, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found with id: " + customerId));

        car.setCustomer(customer);
        customer.getCars().add(car);

        Car savedCar = carRepository.save(car);
        
        // Update caches
        carCache.put(savedCar.getId(), savedCar);
        customerCarCache.remove(customerId);
        
        return savedCar;
    }

    public Car addToServiceCenter(Long carId, Long serviceCenterId) {
        Car car = getCarById(carId);
        ServiceCenter serviceCenter = serviceCenterRepository.findById(serviceCenterId)
                .orElseThrow(() -> new RuntimeException("Сервисный центр не найден"));

        if (!car.getServiceCenters().contains(serviceCenter)) {
            car.getServiceCenters().add(serviceCenter);
        }

        Car savedCar = carRepository.save(car);
        
        // Update caches
        carCache.put(savedCar.getId(), savedCar);
        // Invalidate service center cache
        serviceCenterCarCache.remove(serviceCenter.getName());
        
        return savedCar;
    }

    public Car removeFromServiceCenter(Long carId, Long serviceCenterId) {
        Car car = getCarById(carId);
        
        ServiceCenter serviceCenter = serviceCenterRepository.findById(serviceCenterId)
                .orElseThrow(() -> new RuntimeException("Сервисный центр не найден"));
        
        car.getServiceCenters().removeIf(sc -> sc.getId().equals(serviceCenterId));

        Car savedCar = carRepository.save(car);
        
        // Update caches
        carCache.put(savedCar.getId(), savedCar);
        // Invalidate service center cache
        serviceCenterCarCache.remove(serviceCenter.getName());
        
        return savedCar;
    }
    
    /**
     * Clear all caches - useful for testing or when data consistency is required
     */
    public void clearCaches() {
        carCache.clear();
        customerCarCache.clear();
        serviceCenterCarCache.clear();
    }
}
