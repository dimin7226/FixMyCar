package com.fixmycar.service;

import com.fixmycar.cache.InMemoryCache;
import com.fixmycar.dto.CarDto;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.repository.CarRepository;
import com.fixmycar.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final InMemoryCache carCache;

    public CarDto carDto(CarDto entity) {
        CarDto dto = new CarDto();
        dto.setBrand(entity.getBrand());
        dto.setModel(entity.getModel());
        dto.setVin(entity.getVin());
        dto.setYear(entity.getYear());
        return dto;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Optional<Car> getCarById(Long id) {


        String cacheKey = "albums_id_" + id;
        if (carCache.containsKey(cacheKey)) {
            return Optional.of((Car) carCache.get(cacheKey));
        }
        Car album = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Не найден альбом с ID = " + id));
        carCache.put(cacheKey, album);
        return Optional.of(album);
    }

    public Car saveOrUpdateCar(Car car) {
        if (car.getCustomer() == null || car.getCustomer().getId() == null) {
            throw new ResourceNotFoundException("User ID is required");
        }

        Customer customer = customerRepository.findById(car.getCustomer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id "
                        + car.getCustomer().getId()));
        car.setCustomer(customer);

        Car savedCar = carRepository.save(car);

       // carCache.put(savedCar.getId(), savedCar);
        return savedCar;
    }

    public boolean existsByVinAndIdNot(String vin, Long id) {
        return carRepository.existsByVinAndIdNot(vin, id);
    }

    public boolean customerExists(Long customerId) {
        return customerRepository.existsById(customerId);
    }

    public boolean existsByVin(String vin) {
        return carRepository.existsByVin(vin);
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);

        carCache.clear();
    }
}