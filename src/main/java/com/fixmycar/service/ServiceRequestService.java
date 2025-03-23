package com.fixmycar.service;

import com.fixmycar.exception.ValidationException;
import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.model.ServiceRequest;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.CustomerRepository;
import com.fixmycar.repository.ServiceCenterRepository;
import com.fixmycar.repository.ServiceRequestRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {
    private final ServiceRequestRepository requestRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final ServiceCenterRepository serviceCenterRepository;

    private Car findCarById(Long id) {
        if (id == null) {
            throw new ValidationException("Invalid car ID", 
                    "id", null, "Car ID cannot be null");
        }
        
        return carRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Car not found", 
                        "id", id, "Car with this ID does not exist"));
    }

    private Customer findCustomerById(Long id) {
        if (id == null) {
            throw new ValidationException("Invalid customer ID", 
                    "id", null, "Customer ID cannot be null");
        }
        
        return customerRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Customer not found", 
                        "id", id, "Customer with this ID does not exist"));
    }

    private ServiceCenter findServiceCenterById(Long id) {
        if (id == null) {
            throw new ValidationException("Invalid service center ID", 
                    "id", null, "Service center ID cannot be null");
        }
        
        return serviceCenterRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Service center not found", 
                        "id", id, "Service center with this ID does not exist"));
    }

    private void updateEntityReferences(ServiceRequest request, ServiceRequest requestDetails) {
        if (requestDetails.getCar() != null && requestDetails.getCar().getId() != null) {
            request.setCar(findCarById(requestDetails.getCar().getId()));
        }

        if (requestDetails.getCustomer() != null && requestDetails.getCustomer().getId() != null) {
            request.setCustomer(findCustomerById(requestDetails.getCustomer().getId()));
        }

        if (requestDetails.getServiceCenter() != null
                && requestDetails.getServiceCenter().getId() != null) {
            request.setServiceCenter(findServiceCenterById(requestDetails
                    .getServiceCenter().getId()));
        }
    }

    public List<ServiceRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    public ServiceRequest getRequestById(Long id) {
        if (id == null) {
            throw new ValidationException("Invalid request ID", 
                    "id", null, "Request ID cannot be null");
        }
        
        return requestRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Service request not found", 
                        "id", id, "Service request with this ID does not exist"));
    }

    public ServiceRequest saveRequest(ServiceRequest request) {
        if (request == null) {
            throw new ValidationException("Service request cannot be null", 
                    "request", null, "Service request object is required");
        }
        
        // Validate request data
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new ValidationException("Invalid service request data", 
                    "description", request.getDescription(), "Description cannot be empty");
        }
        
        if (request.getCar() == null || request.getCar().getId() == null) {
            throw new ValidationException("Invalid service request data", 
                    "car", null, "Car is required for service request");
        }
        
        if (request.getCustomer() == null || request.getCustomer().getId() == null) {
            throw new ValidationException("Invalid service request data", 
                    "customer", null, "Customer is required for service request");
        }
        
        if (request.getServiceCenter() == null || request.getServiceCenter().getId() == null) {
            throw new ValidationException("Invalid service request data", 
                    "serviceCenter", null, "Service center is required for service request");
        }
        
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            request.setStatus("PENDING");
        }
        
        if (request.getCreatedAt() == null) {
            request.setCreatedAt(LocalDateTime.now());
        }

        updateEntityReferences(request, request);
        return requestRepository.save(request);
    }

    public ServiceRequest updateRequest(Long id, ServiceRequest requestDetails) {
        if (id == null) {
            throw new ValidationException("Invalid request ID", 
                    "id", null, "Request ID cannot be null");
        }
        
        if (requestDetails == null) {
            throw new ValidationException("Service request details cannot be null", 
                    "requestDetails", null, "Service request details are required");
        }
        
        ServiceRequest request = getRequestById(id);
        
        // Validate request data
        if (requestDetails.getDescription() == null || requestDetails.getDescription().trim().isEmpty()) {
            throw new ValidationException("Invalid service request data", 
                    "description", requestDetails.getDescription(), "Description cannot be empty");
        }
        
        if (requestDetails.getStatus() == null || requestDetails.getStatus().trim().isEmpty()) {
            throw new ValidationException("Invalid service request data", 
                    "status", requestDetails.getStatus(), "Status cannot be empty");
        }

        request.setDescription(requestDetails.getDescription());
        request.setStatus(requestDetails.getStatus());

        updateEntityReferences(request, requestDetails);
        return requestRepository.save(request);
    }

    public void deleteRequest(Long id) {
        if (id == null) {
            throw new ValidationException("Invalid request ID", 
                    "id", null, "Request ID cannot be null");
        }
        
        // Verify request exists
        getRequestById(id);
        
        requestRepository.deleteById(id);
    }

    public List<ServiceRequest> getRequestsByCustomerId(Long customerId) {
        if (customerId == null) {
            throw new ValidationException("Invalid customer ID", 
                    "customerId", null, "Customer ID cannot be null");
        }
        
        // Verify customer exists
        findCustomerById(customerId);
        
        return requestRepository.findByCustomerId(customerId);
    }

    public List<ServiceRequest> getRequestsByCarId(Long carId) {
        if (carId == null) {
            throw new ValidationException("Invalid car ID", 
                    "carId", null, "Car ID cannot be null");
        }
        
        // Verify car exists
        findCarById(carId);
        
        return requestRepository.findByCarId(carId);
    }

    public List<ServiceRequest> getRequestsByServiceCenterId(Long serviceCenterId) {
        if (serviceCenterId == null) {
            throw new ValidationException("Invalid service center ID", 
                    "serviceCenterId", null, "Service center ID cannot be null");
        }
        
        // Verify service center exists
        findServiceCenterById(serviceCenterId);
        
        return requestRepository.findByServiceCenterId(serviceCenterId);
    }

    public ServiceRequest createRequest(Long carId, Long customerId,
                                        Long serviceCenterId, String description) {
        if (carId == null) {
            throw new ValidationException("Invalid car ID", 
                    "carId", null, "Car ID cannot be null");
        }
        
        if (customerId == null) {
            throw new ValidationException("Invalid customer ID", 
                    "customerId", null, "Customer ID cannot be null");
        }
        
        if (serviceCenterId == null) {
            throw new ValidationException("Invalid service center ID", 
                    "serviceCenterId", null, "Service center ID cannot be null");
        }
        
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Invalid description", 
                    "description", description, "Description cannot be empty");
        }
        
        ServiceRequest request = new ServiceRequest();
        request.setCar(findCarById(carId));
        request.setCustomer(findCustomerById(customerId));
        request.setServiceCenter(findServiceCenterById(serviceCenterId));
        request.setDescription(description);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    public ServiceRequest updateStatus(Long id, String status) {
        if (id == null) {
            throw new ValidationException("Invalid request ID", 
                    "id", null, "Request ID cannot be null");
        }
        
        if (status == null || status.trim().isEmpty()) {
            throw new ValidationException("Invalid status", 
                    "status", status, "Status cannot be empty");
        }
        
        ServiceRequest request = getRequestById(id);
        request.setStatus(status);
        return requestRepository.save(request);
    }
}
