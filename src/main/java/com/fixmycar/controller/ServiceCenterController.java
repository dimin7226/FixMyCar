package com.fixmycar.controller;

import com.fixmycar.exception.InvalidInputException;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.service.ServiceCenterService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home/service-centers")
@RequiredArgsConstructor
public class ServiceCenterController {
    private final ServiceCenterService serviceCenterService;

    @GetMapping
    public List<ServiceCenter> getAllServiceCenters() {
        return serviceCenterService.getAllServiceCenters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceCenter> getServiceCenterById(@PathVariable Long id) {
        if (id <= 0) {
            throw new InvalidInputException("Service center ID must be a positive number");
        }
        return ResponseEntity.ok(serviceCenterService.getServiceCenterById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceCenter> createServiceCenter(
            @RequestBody ServiceCenter serviceCenter) {
        if (serviceCenter == null) {
            throw new InvalidInputException("Service center details cannot be null");
        }
        return ResponseEntity.ok(serviceCenterService.saveServiceCenter(serviceCenter));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceCenter(@PathVariable Long id) {
        if (id <= 0) {
            throw new InvalidInputException("Service center ID must be a positive number");
        }
        serviceCenterService.deleteServiceCenter(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/car/{carId}")
    public List<ServiceCenter> getServiceCentersByCarId(@PathVariable Long carId) {
        if (carId <= 0) {
            throw new InvalidInputException("Car ID must be a positive number");
        }
        return serviceCenterService.getServiceCentersByCarId(carId);
    }
}
