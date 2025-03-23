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
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final ServiceCenterRepository serviceCenterRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id).orElseThrow(() ->
                new ValidationException("Car not found", "id", id, "Car with this ID does not exist"));
    }

    public Car saveCar(Car car) {
        // Проверяем, есть ли у машины клиент
        if (car.getCustomer() != null && car.getCustomer().getId() != null) {
            Customer customer = customerRepository.findById(car.getCustomer().getId())
                    .orElseThrow(() -> new ValidationException("Customer not found", 
                            "customerId", car.getCustomer().getId(), 
                            "Customer with this ID does not exist"));
            car.setCustomer(customer);
        } else {
            throw new ValidationException("Customer is required", 
                    "customer", null, "Car must be associated with a customer");
        }

        // Validate car data
        if (car.getBrand() == null || car.getBrand().trim().isEmpty()) {
            throw new ValidationException("Invalid car data", 
                    "brand", car.getBrand(), "Car brand cannot be empty");
        }
        
        if (car.getModel() == null || car.getModel().trim().isEmpty()) {
            throw new ValidationException("Invalid car data", 
                    "model", car.getModel(), "Car model cannot be empty");
        }
        
        if (car.getVin() == null || car.getVin().trim().isEmpty()) {
            throw new ValidationException("Invalid car data", 
                    "vin", car.getVin(), "Car VIN cannot be empty");
        }
        
        if (car.getYear() <= 0) {
            throw new ValidationException("Invalid car data", 
                    "year", car.getYear(), "Car year must be positive");
        }

        return carRepository.save(car);
    }

    public Car updateCarInfo(Long carId, Car updatedCar) {
        Car existingCar = getCarById(carId);

        // Validate updated data
        if (updatedCar.getBrand() == null || updatedCar.getBrand().trim().isEmpty()) {
            throw new ValidationException("Invalid car data", 
                    "brand", updatedCar.getBrand(), "Car brand cannot be empty");
        }
        
        if (updatedCar.getModel() == null || updatedCar.getModel().trim().isEmpty()) {
            throw new ValidationException("Invalid car data", 
                    "model", updatedCar.getModel(), "Car model cannot be empty");
        }
        
        if (updatedCar.getVin() == null || updatedCar.getVin().trim().isEmpty()) {
            throw new ValidationException("Invalid car data", 
                    "vin", updatedCar.getVin(), "Car VIN cannot be empty");
        }
        
        if (updatedCar.getYear() <= 0) {
            throw new ValidationException("Invalid car data", 
                    "year", updatedCar.getYear(), "Car year must be positive");
        }

        existingCar.setBrand(updatedCar.getBrand());
        existingCar.setModel(updatedCar.getModel());
        existingCar.setVin(updatedCar.getVin());
        existingCar.setYear(updatedCar.getYear());

        return carRepository.save(existingCar);
    }

    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Car not found", 
                        "id", id, "Car with this ID does not exist"));

        List<ServiceRequest> serviceRequests = car.getServiceRequests();
        if (serviceRequests != null && !serviceRequests.isEmpty()) {
            serviceRequestRepository.deleteAll(serviceRequests);
        }

        carRepository.delete(car);
    }

    @Transactional
    public Car transferOwnership(Long carId, Long newCustomerId) {
        if (carId == null) {
            throw new ValidationException("Invalid car ID", 
                    "carId", null, "Car ID cannot be null");
        }
        
        if (newCustomerId == null) {
            throw new ValidationException("Invalid customer ID", 
                    "newCustomerId", null, "Customer ID cannot be null");
        }
        
        Car car = getCarById(carId);
        Customer newOwner = customerRepository.findById(newCustomerId)
                .orElseThrow(() -> new ValidationException("Customer not found", 
                        "newCustomerId", newCustomerId, 
                        "Customer with this ID does not exist"));

        // Если у автомобиля уже есть владелец, удаляем автомобиль из его списка
        if (car.getCustomer() != null) {
            car.getCustomer().getCars().remove(car);
        }

        // Устанавливаем нового владельца
        car.setCustomer(newOwner);

        // Добавляем автомобиль в список автомобилей нового владельца
        newOwner.getCars().add(car);

        return carRepository.save(car);
    }

    public List<Car> getCarsByCustomerId(Long customerId) {
        if (customerId == null) {
            throw new ValidationException("Invalid customer ID", 
                    "customerId", null, "Customer ID cannot be null");
        }
        
        // Verify customer exists
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ValidationException("Customer not found", 
                        "customerId", customerId, 
                        "Customer with this ID does not exist"));
                        
        return carRepository.findByCustomerId(customerId);
    }

    public List<Car> getCarsByServiceCenterId(Long serviceCenterId) {
        if (serviceCenterId == null) {
            throw new ValidationException("Invalid service center ID", 
                    "serviceCenterId", null, "Service center ID cannot be null");
        }
        
        // Verify service center exists
        serviceCenterRepository.findById(serviceCenterId)
                .orElseThrow(() -> new ValidationException("Service center not found", 
                        "serviceCenterId", serviceCenterId, 
                        "Service center with this ID does not exist"));
                        
        return carRepository.findByServiceCentersId(serviceCenterId);
    }

    public Car assignToCustomer(Car car, Long customerId) {
        if (car == null) {
            throw new ValidationException("Car cannot be null", 
                    "car", null, "Car object is required");
        }
        
        if (customerId == null) {
            throw new ValidationException("Invalid customer ID", 
                    "customerId", null, "Customer ID cannot be null");
        }
        
        // Validate car data
        if (car.getBrand() == null || car.getBrand().trim().isEmpty()) {
            throw new ValidationException("Invalid car data", 
                    "brand", car.getBrand(), "Car brand cannot be empty");
        }
        
        if (car.getModel() == null || car.getModel().trim().isEmpty()) {
            throw new ValidationException("Invalid car data", 
                    "model", car.getModel(), "Car model cannot be empty");
        }
        
        if (car.getVin() == null || car.getVin().trim().isEmpty()) {
            throw new ValidationException("Invalid car data", 
                    "vin", car.getVin(), "Car VIN cannot be empty");
        }
        
        if (car.getYear() <= 0) {
            throw new ValidationException("Invalid car data", 
                    "year", car.getYear(), "Car year must be positive");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ValidationException("Customer not found", 
                        "customerId", customerId, 
                        "Customer with this ID does not exist"));

        car.setCustomer(customer);
        customer.getCars().add(car);

        return carRepository.save(car);
    }

    public Car addToServiceCenter(Long carId, Long serviceCenterId) {
        if (carId == null) {
            throw new ValidationException("Invalid car ID", 
                    "carId", null, "Car ID cannot be null");
        }
        
        if (serviceCenterId == null) {
            throw new ValidationException("Invalid service center ID", 
                    "serviceCenterId", null, "Service center ID cannot be null");
        }
        
        Car car = getCarById(carId);
        ServiceCenter serviceCenter = serviceCenterRepository.findById(serviceCenterId)
                .orElseThrow(() -> new ValidationException("Service center not found", 
                        "serviceCenterId", serviceCenterId, 
                        "Service center with this ID does not exist"));

        if (!car.getServiceCenters().contains(serviceCenter)) {
            car.getServiceCenters().add(serviceCenter);
        }

        return carRepository.save(car);
    }

    public Car removeFromServiceCenter(Long carId, Long serviceCenterId) {
        if (carId == null) {
            throw new ValidationException("Invalid car ID", 
                    "carId", null, "Car ID cannot be null");
        }
        
        if (serviceCenterId == null) {
            throw new ValidationException("Invalid service center ID", 
                    "serviceCenterId", null, "Service center ID cannot be null");
        }
        
        Car car = getCarById(carId);
        
        // Verify service center exists
        serviceCenterRepository.findById(serviceCenterId)
                .orElseThrow(() -> new ValidationException("Service center not found", 
                        "serviceCenterId", serviceCenterId, 
                        "Service center with this ID does not exist"));

        car.getServiceCenters().removeIf(sc -> sc.getId().equals(serviceCenterId));

        return carRepository.save(car);
    }
}
