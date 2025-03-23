package com.fixmycar.dao;

import com.fixmycar.model.Car;
import java.util.List;
import java.util.Optional;

public interface CarDao {
    /**
     * Find all cars
     * @return List of all cars
     */
    List<Car> findAll();
    
    /**
     * Find a car by its ID
     * @param id The ID of the car
     * @return Optional containing the car if found
     */
    Optional<Car> findById(Long id);
    
    /**
     * Find a car by its VIN
     * @param vin The Vehicle Identification Number
     * @return Optional containing the car if found
     */
    Optional<Car> findByVin(String vin);
    
    /**
     * Find cars by customer ID
     * @param customerId The ID of the customer
     * @return List of cars associated with the customer
     */
    List<Car> findByCustomerId(Long customerId);
    
    /**
     * Find cars by service center ID
     * @param serviceCenterId The ID of the service center
     * @return List of cars associated with the service center
     */
    List<Car> findByServiceCentersId(Long serviceCenterId);
    
    /**
     * Find cars by service center name
     * @param serviceCenterName The name of the service center
     * @return List of cars associated with the service center
     */
    List<Car> findByServiceCentersName(String serviceCenterName);
    
    /**
     * Find cars by brand and model
     * @param brand The brand of the car
     * @param model The model of the car
     * @return List of cars matching the brand and model
     */
    List<Car> findByBrandAndModel(String brand, String model);
    
    /**
     * Find cars by brand and model using native SQL query
     * @param brand The brand of the car
     * @param model The model of the car
     * @return List of cars matching the brand and model
     */
    List<Car> findByBrandAndModelNative(String brand, String model);
    
    /**
     * Find cars by service center ID with eager loading of customer and service centers
     * @param serviceCenterId The ID of the service center
     * @return List of cars with eagerly loaded relationships
     */
    List<Car> findWithCustomerAndServiceCentersByServiceCentersId(Long serviceCenterId);
    
    /**
     * Save a car entity
     * @param car The car to save
     * @return The saved car
     */
    Car save(Car car);
    
    /**
     * Delete a car entity
     * @param car The car to delete
     */
    void delete(Car car);
    
    /**
     * Delete a car entity by its ID
     * @param id The ID of the car to delete
     */
    void deleteById(Long id);
}
