package com.fixmycar.controller;

import com.fixmycar.model.Car;
import com.fixmycar.service.CarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(carService.updateCarInfo(id, car));
    }

    @PutMapping("/{carId}/transfer/customer/{newCustomerId}")
    public ResponseEntity<Car> transferCarOwnership(
            @PathVariable Long carId,
            @PathVariable Long newCustomerId) {
        return ResponseEntity.ok(carService.transferOwnership(carId, newCustomerId));
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