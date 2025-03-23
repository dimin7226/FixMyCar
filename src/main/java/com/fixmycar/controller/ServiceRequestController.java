package com.fixmycar.controller;

import com.fixmycar.exception.InvalidInputException;
import com.fixmycar.model.ServiceRequest;
import com.fixmycar.service.ServiceRequestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home/requests")
@RequiredArgsConstructor
public class ServiceRequestController {
    private final ServiceRequestService requestService;

    @GetMapping
    public List<ServiceRequest> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequest> getRequestById(@PathVariable Long id) {
        if (id <= 0) {
            throw new InvalidInputException("Request ID must be a positive number");
        }
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceRequest> createRequest(@RequestBody ServiceRequest request) {
        if (request == null) {
            throw new InvalidInputException("Service request details cannot be null");
        }
        return ResponseEntity.ok(requestService.saveRequest(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        if (id <= 0) {
            throw new InvalidInputException("Request ID must be a positive number");
        }
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    public List<ServiceRequest> getRequestsByCustomerId(@PathVariable Long customerId) {
        if (customerId <= 0) {
            throw new InvalidInputException("Customer ID must be a positive number");
        }
        return requestService.getRequestsByCustomerId(customerId);
    }

    @GetMapping("/car/{carId}")
    public List<ServiceRequest> getRequestsByCarId(@PathVariable Long carId) {
        if (carId <= 0) {
            throw new InvalidInputException("Car ID must be a positive number");
        }
        return requestService.getRequestsByCarId(carId);
    }

    @GetMapping("/service-center/{serviceCenterId}")
    public List<ServiceRequest> getRequestsByServiceCenterId(@PathVariable Long serviceCenterId) {
        if (serviceCenterId <= 0) {
            throw new InvalidInputException("Service center ID must be a positive number");
        }
        return requestService.getRequestsByServiceCenterId(serviceCenterId);
    }

    @PostMapping("/create")
    public ResponseEntity<ServiceRequest> createCompleteRequest(
            @RequestParam Long carId,
            @RequestParam Long customerId,
            @RequestParam Long serviceCenterId,
            @RequestParam String description) {
        if (carId <= 0) {
            throw new InvalidInputException("Car ID must be a positive number");
        }
        if (customerId <= 0) {
            throw new InvalidInputException("Customer ID must be a positive number");
        }
        if (serviceCenterId <= 0) {
            throw new InvalidInputException("Service center ID must be a positive number");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidInputException("Description cannot be null or empty");
        }
        return ResponseEntity.ok(requestService.createRequest(
                carId, customerId, serviceCenterId, description));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ServiceRequest> updateStatus(
            @PathVariable Long id, @RequestParam String status) {
        if (id <= 0) {
            throw new InvalidInputException("Request ID must be a positive number");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new InvalidInputException("Status cannot be null or empty");
        }
        return ResponseEntity.ok(requestService.updateStatus(id, status));
    }
}
