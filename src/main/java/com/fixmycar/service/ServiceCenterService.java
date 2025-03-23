package com.fixmycar.service;

import com.fixmycar.exception.ValidationException;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.repository.ServiceCenterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceCenterService {
    private final ServiceCenterRepository serviceCenterRepository;

    public List<ServiceCenter> getAllServiceCenters() {
        return serviceCenterRepository.findAll();
    }

    public ServiceCenter getServiceCenterById(Long id) {
        if (id == null) {
            throw new ValidationException("Invalid service center ID", 
                    "id", null, "Service center ID cannot be null");
        }
        
        return serviceCenterRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Service center not found", 
                        "id", id, "Service center with this ID does not exist"));
    }

    public ServiceCenter saveServiceCenter(ServiceCenter serviceCenter) {
        // Validate service center data
        if (serviceCenter == null) {
            throw new ValidationException("Service center cannot be null", 
                    "serviceCenter", null, "Service center object is required");
        }
        
        if (serviceCenter.getName() == null || serviceCenter.getName().trim().isEmpty()) {
            throw new ValidationException("Invalid service center data", 
                    "name", serviceCenter.getName(), "Service center name cannot be empty");
        }
        
        if (serviceCenter.getAddress() == null || serviceCenter.getAddress().trim().isEmpty()) {
            throw new ValidationException("Invalid service center data", 
                    "address", serviceCenter.getAddress(), "Service center address cannot be empty");
        }
        
        if (serviceCenter.getPhone() == null || serviceCenter.getPhone().trim().isEmpty()) {
            throw new ValidationException("Invalid service center data", 
                    "phone", serviceCenter.getPhone(), "Service center phone cannot be empty");
        }
        
        return serviceCenterRepository.save(serviceCenter);
    }

    public ServiceCenter updateServiceCenter(Long id, ServiceCenter serviceCenterDetails) {
        if (id == null) {
            throw new ValidationException("Invalid service center ID", 
                    "id", null, "Service center ID cannot be null");
        }
        
        if (serviceCenterDetails == null) {
            throw new ValidationException("Service center details cannot be null", 
                    "serviceCenterDetails", null, "Service center details are required");
        }
        
        ServiceCenter serviceCenter = getServiceCenterById(id);
        
        // Validate service center data
        if (serviceCenterDetails.getName() == null || serviceCenterDetails.getName().trim().isEmpty()) {
            throw new ValidationException("Invalid service center data", 
                    "name", serviceCenterDetails.getName(), "Service center name cannot be empty");
        }
        
        if (serviceCenterDetails.getAddress() == null || serviceCenterDetails.getAddress().trim().isEmpty()) {
            throw new ValidationException("Invalid service center data", 
                    "address", serviceCenterDetails.getAddress(), "Service center address cannot be empty");
        }
        
        if (serviceCenterDetails.getPhone() == null || serviceCenterDetails.getPhone().trim().isEmpty()) {
            throw new ValidationException("Invalid service center data", 
                    "phone", serviceCenterDetails.getPhone(), "Service center phone cannot be empty");
        }

        serviceCenter.setName(serviceCenterDetails.getName());
        serviceCenter.setAddress(serviceCenterDetails.getAddress());
        serviceCenter.setPhone(serviceCenterDetails.getPhone());

        return serviceCenterRepository.save(serviceCenter);
    }

    public void deleteServiceCenter(Long id) {
        if (id == null) {
            throw new ValidationException("Invalid service center ID", 
                    "id", null, "Service center ID cannot be null");
        }
        
        // Verify service center exists
        getServiceCenterById(id);
        
        serviceCenterRepository.deleteById(id);
    }

    public List<ServiceCenter> getServiceCentersByCarId(Long carId) {
        if (carId == null) {
            throw new ValidationException("Invalid car ID", 
                    "carId", null, "Car ID cannot be null");
        }
        
        return serviceCenterRepository.findByCarsId(carId);
    }
}
