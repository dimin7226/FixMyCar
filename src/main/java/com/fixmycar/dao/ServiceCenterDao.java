package com.fixmycar.dao;

import com.fixmycar.model.ServiceCenter;
import java.util.List;
import java.util.Optional;

public interface ServiceCenterDao {
    /**
     * Find all service centers
     * @return List of all service centers
     */
    List<ServiceCenter> findAll();
    
    /**
     * Find a service center by its ID
     * @param id The ID of the service center
     * @return Optional containing the service center if found
     */
    Optional<ServiceCenter> findById(Long id);
    
    /**
     * Find service centers by name
     * @param name The name of the service center
     * @return List of service centers matching the name
     */
    List<ServiceCenter> findByName(String name);
    
    /**
     * Find service centers associated with a car ID
     * @param carId The ID of the car
     * @return List of service centers associated with the car
     */
    List<ServiceCenter> findByCarsId(Long carId);
    
    /**
     * Save a service center entity
     * @param serviceCenter The service center to save
     * @return The saved service center
     */
    ServiceCenter save(ServiceCenter serviceCenter);
    
    /**
     * Delete a service center entity
     * @param serviceCenter The service center to delete
     */
    void delete(ServiceCenter serviceCenter);
    
    /**
     * Delete a service center entity by its ID
     * @param id The ID of the service center to delete
     */
    void deleteById(Long id);
}
