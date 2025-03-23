package com.fixmycar.controller;

import com.fixmycar.exception.ValidationException;
import com.fixmycar.model.Car;
import com.fixmycar.service.CarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {
    private static final Logger logger = LoggerFactory.getLogger(CarController.class);
    private final CarService carService;

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        logger.info("Fetching all cars");
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        logger.info("Fetching car with id: {}", id);
        if (id == null) {
            logger.error("Car ID cannot be null");
            throw new ValidationException("Invalid car ID", "id", null, "Car ID cannot be null");
        }
        Car car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Car>> getCarsByCustomerId(@PathVariable Long customerId) {
        logger.info("Fetching cars for customer with id: {}", customerId);
        if (customerId == null) {
            logger.error("Customer ID cannot be null");
            throw new ValidationException("Invalid customer ID", "customerId", null, "Customer ID cannot be null");
        }
        List<Car> cars = carService.getCarsByCustomerId(customerId);
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/service-center/{serviceCenterId}")
    public ResponseEntity<List<Car>> getCarsByServiceCenterId(@PathVariable Long serviceCenterId) {
        logger.info("Fetching cars for service center with id: {}", serviceCenterId);
        if (serviceCenterId == null) {
            logger.error("Service center ID cannot be null");
            throw new ValidationException("Invalid service center ID", "serviceCenterId", null, "Service center ID cannot be null");
        }
        List<Car> cars = carService.getCarsByServiceCenterId(serviceCenterId);
        return ResponseEntity.ok(cars);
    }

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<Car> createCar(@RequestBody Car car, @PathVariable Long customerId) {
        logger.info("Creating new car for customer with id: {}", customerId);
        if (car == null) {
            logger.error("Car object cannot be null");
            throw new ValidationException("Invalid car data", "car", null, "Car object cannot be null");
        }
        if (customerId == null) {
            logger.error("Customer ID cannot be null");
            throw new ValidationException("Invalid customer ID", "customerId", null, "Customer ID cannot be null");
        }
        validateCarData(car);
        Car createdCar = carService.assignToCustomer(car, customerId);
        return new ResponseEntity<>(createdCar, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car car) {
        logger.info("Updating car with id: {}", id);
        if (id == null) {
            logger.error("Car ID cannot be null");
            throw new ValidationException("Invalid car ID", "id", null, "Car ID cannot be null");
        }
        if (car == null) {
            logger.error("Car object cannot be null");
            throw new ValidationException("Invalid car data", "car", null, "Car object cannot be null");
        }
        validateCarData(car);
        Car updatedCar = carService.updateCarInfo(id, car);
        return ResponseEntity.ok(updatedCar);
    }

    @PutMapping("/{carId}/transfer/customer/{newCustomerId}")
    public ResponseEntity<Car> transferCarOwnership(@PathVariable Long carId, @PathVariable Long newCustomerId) {
        logger.info("Transferring car with id: {} to customer with id: {}", carId, newCustomerId);
        if (carId == null) {
            logger.error("Car ID cannot be null");
            throw new ValidationException("Invalid car ID", "carId", null, "Car ID cannot be null");
        }
        if (newCustomerId == null) {
            logger.error("New customer ID cannot be null");
            throw new ValidationException("Invalid customer ID", "newCustomerId", null, "Customer ID cannot be null");
        }
        Car car = carService.transferOwnership(carId, newCustomerId);
        return ResponseEntity.ok(car);
    }

    @PutMapping("/{carId}/service-center/{serviceCenterId}")
    public ResponseEntity<Car> addToServiceCenter(@PathVariable Long carId, @PathVariable Long serviceCenterId) {
        logger.info("Adding car with id: {} to service center with id: {}", carId, serviceCenterId);
        if (carId == null) {
            logger.error("Car ID cannot be null");
            throw new ValidationException("Invalid car ID", "carId", null, "Car ID cannot be null");
        }
        if (serviceCenterId == null) {
            logger.error("Service center ID cannot be null");
            throw new ValidationException("Invalid service center ID", "serviceCenterId", null, "Service center ID cannot be null");
        }
        Car car = carService.addToServiceCenter(carId, serviceCenterId);
        return ResponseEntity.ok(car);
    }

    @DeleteMapping("/{carId}/service-center/{serviceCenterId}")
    public ResponseEntity<Car> removeFromServiceCenter(@PathVariable Long carId, @PathVariable Long serviceCenterId) {
        logger.info("Removing car with id: {} from service center with id: {}", carId, serviceCenterId);
        if (carId == null) {
            logger.error("Car ID cannot be null");
            throw new ValidationException("Invalid car ID", "carId", null, "Car ID cannot be null");
        }
        if (serviceCenterId == null) {
            logger.error("Service center ID cannot be null");
            throw new ValidationException("Invalid service center ID", "serviceCenterId", null, "Service center ID cannot be null");
        }
        Car car = carService.removeFromServiceCenter(carId, serviceCenterId);
        return ResponseEntity.ok(car);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        logger.info("Deleting car with id: {}", id);
        if (id == null) {
            logger.error("Car ID cannot be null");
            throw new ValidationException("Invalid car ID", "id", null, "Car ID cannot be null");
        }
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    private void validateCarData(Car car) {
        if (car.getBrand() == null || car.getBrand().trim().isEmpty()) {
            logger.error("Car brand cannot be empty");
            throw new ValidationException("Invalid car data", "brand", car.getBrand(), "Car brand cannot be empty");
        }
        if (car.getModel() == null || car.getModel().trim().isEmpty()) {
            logger.error("Car model cannot be empty");
            throw new ValidationException("Invalid car data", "model", car.getModel(), "Car model cannot be empty");
        }
        if (car.getVin() == null || car.getVin().trim().isEmpty()) {
            logger.error("Car VIN cannot be empty");
            throw new ValidationException("Invalid car data", "vin", car.getVin(), "Car VIN cannot be empty");
        }
        if (car.getYear() <= 0) {
            logger.error("Car year must be positive: {}", car.getYear());
            throw new ValidationException("Invalid car data", "year", car.getYear(), "Car year must be positive");
        }
    }
}
