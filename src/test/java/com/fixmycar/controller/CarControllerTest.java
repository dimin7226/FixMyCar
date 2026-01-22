package com.fixmycar.controller;

import com.fixmycar.exception.BadRequestException;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.service.CarService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController carController;

    private Car createValidCar() {
        Customer customer = new Customer();
        customer.setId(1L);

        Car car = new Car();
        car.setId(1L);
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setVin("VIN123");
        car.setYear(2020);
        car.setCustomer(customer);

        return car;
    }

    @Test
    void getAllCars_ShouldReturnAllCars() {
        List<Car> cars = Arrays.asList(new Car(), new Car());
        when(carService.getAllCars()).thenReturn(cars);

        List<Car> result = carController.getAllCars();

        assertEquals(2, result.size());
    }

    @Test
    void getCarById_ShouldReturnCar() {
        Car car = createValidCar();
        when(carService.getCarById(1L)).thenReturn(Optional.of(car));

        ResponseEntity<Car> response = carController.getCarById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getCarById_ShouldThrow_WhenNotFound() {
        when(carService.getCarById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carController.getCarById(1L));
    }

    @Test
    void createCar_ShouldReturnCreatedCar() {
        Car car = createValidCar();

        lenient().when(carService.existsByVin("VIN123")).thenReturn(false);
        lenient().when(carService.customerExists(1L)).thenReturn(true);
        when(carService.saveOrUpdateCar(car)).thenReturn(car);

        ResponseEntity<Car> response = carController.createCar(car);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("VIN123", response.getBody().getVin());
    }

    @Test
    void createCar_ShouldThrowBadRequest_WhenYearTooOld() {
        Car car = createValidCar();
        car.setYear(1899);
        int currentYear = Year.now().getValue();
        String exceptionMessage = String.format("Year must be between 1900 and %d", currentYear);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> carController.createCar(car));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void createCar_ShouldThrowBadRequest_WhenYearTooNew() {
        Car car = createValidCar();
        car.setYear(2030);
        int currentYear = Year.now().getValue();
        String exceptionMessage = String.format("Year must be between 1900 and %d", currentYear);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> carController.createCar(car));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void createCar_ShouldThrowBadRequest_WhenVinExists() {
        Car car = createValidCar();

        when(carService.existsByVin("VIN123")).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> carController.createCar(car));
        assertEquals("Car with this VIN already exists", exception.getMessage());
    }

    @Test
    void createCar_ShouldThrowBadRequest_WhenCustomerMissing() {
        Car car = createValidCar();
        car.setCustomer(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> carController.createCar(car));
        assertEquals("Customer with specified ID does not exist", exception.getMessage());
    }

    @Test
    void createCar_ShouldThrowBadRequest_WhenCustomerIdMissing() {
        Car car = createValidCar();
        car.getCustomer().setId(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> carController.createCar(car));
        assertEquals("Customer with specified ID does not exist", exception.getMessage());
    }

    @Test
    void updateCar_ShouldReturnUpdatedCar() {
        Car carFromDb = createValidCar(); // нормальный год
        Car updatedDetails = createValidCar();
        updatedDetails.setVin("VIN123"); // любые данные

        Car updatedCar = new Car();
        updatedCar.setId(1L);

        when(carService.getCarById(1L)).thenReturn(Optional.of(carFromDb));
        when(carService.saveOrUpdateCar(any(Car.class))).thenReturn(updatedCar);

        ResponseEntity<Car> response = carController.updateCar(1L, updatedDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCar, response.getBody());
        verify(carService).saveOrUpdateCar(any(Car.class));
    }

    @Test
    void updateCar_ShouldThrowBadRequest_WhenYearTooOld() {
        Car updatedDetails = createValidCar();
        updatedDetails.setYear(1899); // меньше 1900
        int currentYear = Year.now().getValue();
        String exceptionMessage = String.format("Year must be between 1900 and %d", currentYear);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> carController.createCar(updatedDetails));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void updateCar_ShouldThrowBadRequest_WhenCustomerNotExists() {
        Car carFromDb = createValidCar();
        Car updatedDetails = createValidCar();
        updatedDetails.setCustomer(null); // пустой клиент -> BadRequest

        when(carService.getCarById(1L)).thenReturn(Optional.of(carFromDb));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carController.updateCar(1L, updatedDetails));
        assertEquals("Customer with specified ID does not exist", exception.getMessage());
    }

    @Test
    void updateCar_ShouldThrowResourceNotFound_WhenCarNotFound() {
        Car updatedDetails = createValidCar();
        when(carService.getCarById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> carController.updateCar(1L, updatedDetails));
    }

    @Test
    void deleteCar_ShouldReturnNoContent() {
        doNothing().when(carService).deleteCar(1L);

        ResponseEntity<Void> response = carController.deleteCar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(carService).deleteCar(1L);
    }
}
