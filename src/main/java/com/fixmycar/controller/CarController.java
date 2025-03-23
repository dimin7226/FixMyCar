package com.fixmycar.controller;

import com.fixmycar.exception.InvalidInputException;
import com.fixmycar.model.Car;
import com.fixmycar.service.CarService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @GetMapping
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        validateId(id, "Car ID must be a positive number");
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<Car> createCar(@RequestBody Car car, @PathVariable Long customerId) {
        validateId(customerId, "Customer ID must be a positive number");
        validateRequestBody(car, "Car details cannot be null");
        return ResponseEntity.ok(carService.assignToCustomer(car, customerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car car) {
        validateId(id, "Car ID must be a positive number");
        validateRequestBody(car, "Car details cannot be null");
        return ResponseEntity.ok(carService.updateCarInfo(id, car));
    }

    @PutMapping("/{carId}/transfer/customer/{newCustomerId}")
    public ResponseEntity<Car> transferCarOwnership(
            @PathVariable Long carId,
            @PathVariable Long newCustomerId) {
        validateId(carId, "Car ID must be a positive number");
        validateId(newCustomerId, "New customer ID must be a positive number");
        return ResponseEntity.ok(carService.transferOwnership(carId, newCustomerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        validateId(id, "Car ID must be a positive number");
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    public List<Car> getCarsByCustomerId(@PathVariable Long customerId) {
        validateId(customerId, "Customer ID must be a positive number");
        return carService.getCarsByCustomerId(customerId);
    }

    @GetMapping("/service-center/{serviceCenterId}")
    public List<Car> getCarsByServiceCenterId(@PathVariable Long serviceCenterId) {
        validateId(serviceCenterId, "Service center ID must be a positive number");
        return carService.getCarsByServiceCenterId(serviceCenterId);
    }
    
    private void validateId(Long id, String message) {
        if (id <= 0) {
            throw new InvalidInputException(message);
        }
    }
    
    private <T> void validateRequestBody(T body, String message) {
        if (body == null) {
            throw new InvalidInputException(message);
        }
    }
}
