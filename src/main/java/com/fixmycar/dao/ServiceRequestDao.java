package com.fixmycar.dao;

import com.fixmycar.model.ServiceRequest;
import java.util.List;
import java.util.Optional;

public interface ServiceRequestDao {
    /**
     * Find all service requests
     * @return List of all service requests
     */
    List<ServiceRequest> findAll();
    
    /**
     * Find a service request by its ID
     * @param id The ID of the service request
     * @return Optional containing the service request if found
     */
    Optional<ServiceRequest> findById(Long id);
    
    /**
     * Find service requests by customer ID
     * @param customerId The ID of the customer
     * @return List of service requests associated with the customer
     */
    List<ServiceRequest> findByCustomerId(Long customerId);
    
    /**
     * Find service requests by car ID
     * @param carId The ID of the car
     * @return List of service requests associated with the car
     */
    List<ServiceRequest> findByCarId(Long carId);
    
    /**
     * Find service requests by service center ID
     * @param serviceCenterId The ID of the service center
     * @return List of service requests associated with the service center
     */
    List<ServiceRequest> findByServiceCenterId(Long serviceCenterId);
    
    /**
     * Save a service request entity
     * @param serviceRequest The service request to save
     * @return The saved service request
     */
    ServiceRequest save(ServiceRequest serviceRequest);
    
    /**
     * Delete a service request entity
     * @param serviceRequest The service request to delete
     */
    void delete(ServiceRequest serviceRequest);
    
    /**
     * Delete a service request entity by its ID
     * @param id The ID of the service request to delete
     */
    void deleteById(Long id);
    
    /**
     * Delete multiple service request entities
     * @param serviceRequests The list of service requests to delete
     */
    void deleteAll(List<ServiceRequest> serviceRequests);
}
