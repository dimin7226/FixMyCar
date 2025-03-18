package com.fixmycar.controller;

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
//import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<Car> createCar(@RequestBody Car car, @PathVariable Long customerId) {
        return ResponseEntity.ok(carService.assignToCustomer(car, customerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car car) {
        car.setId(id);
        return ResponseEntity.ok(carService.saveCar(car));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    public List<Car> getCarsByCustomerId(@PathVariable Long customerId) {
        return carService.getCarsByCustomerId(customerId);
    }

    @GetMapping("/service-center/{serviceCenterId}")
    public List<Car> getCarsByServiceCenterId(@PathVariable Long serviceCenterId) {
        return carService.getCarsByServiceCenterId(serviceCenterId);
    }

    /*@PostMapping("/{carId}/customer/{customerId}")
    public ResponseEntity<Car> assignToCustomer(@PathVariable Long carId,
    @PathVariable Long customerId) {
        return ResponseEntity.ok(carService.assignToCustomer(carId, customerId));
    }

    @PostMapping("/{carId}/service-center/{serviceCenterId}")
    public ResponseEntity<Car> addToServiceCenter(@PathVariable Long carId,
    @PathVariable Long serviceCenterId) {
        return ResponseEntity.ok(carService.addToServiceCenter(carId, serviceCenterId));
    }

    @DeleteMapping("/{carId}/service-center/{serviceCenterId}")
    public ResponseEntity<Car> removeFromServiceCenter(@PathVariable Long carId,
    @PathVariable Long serviceCenterId) {
        return ResponseEntity.ok(carService.removeFromServiceCenter(carId, serviceCenterId));
    }*/
}