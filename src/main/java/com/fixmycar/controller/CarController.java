package com.fixmycar.controller;

import com.fixmycar.exception.BadRequestException;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Car;
import com.fixmycar.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home/cars")
@RequiredArgsConstructor
@Tag(name = "Car Controller", description = "API для управления автомобилями")
public class CarController {
    private final CarService carService;

    @GetMapping
    @Operation(summary = "Получить список автомобилей",
            description = "Возвращает автомобили")
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить машину по ID",
            description = "Возвращает машины по указанному ID")
    @ApiResponse(responseCode = "200", description = "Машина найдена")
    @ApiResponse(responseCode = "404", description = "Машина не найдена")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Car not found with id " + id));
        return ResponseEntity.ok(car);
    }

    @PostMapping
    @Operation(summary = "Создать машину", description = "Создает новую машину")
    @ApiResponse(responseCode = "200", description = "Машина успешно создана")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<Car> createCar(@Valid @RequestBody Car car) {

        // Год выпуска
        if (car.getYear() < 1980 || car.getYear() > 2025) {
            throw new BadRequestException("Year must be between 1980 and 2025");
        }

        // VIN должен быть уникальным
        if (carService.existsByVin(car.getVin())) {
            throw new BadRequestException("Car with this VIN already exists");
        }

        // Клиент должен существовать
        if (car.getCustomer() == null || car.getCustomer().getId() == null ||
                !carService.customerExists(car.getCustomer().getId())) {
            throw new BadRequestException("Customer with specified ID does not exist");
        }

        Car createdCar = carService.saveOrUpdateCar(car);
        return ResponseEntity.ok(createdCar);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Массовое создание/обновление автомобилей",
            description = "Создает или обновляет список автомобилей за одну операцию с возможностью фильтрации по году")
    @ApiResponse(responseCode = "200", description = "Автомобили успешно обработаны")
    @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе")
    public ResponseEntity<List<Car>> processTransactionsBulk(
            @RequestParam(required = false) Integer yearFilter,
            @RequestBody List<@Valid Car> cars) {

        Predicate<Car> yearFilterPredicate = (Car car) ->
                yearFilter == null || car.getYear() == yearFilter;

        List<Car> processedTransactions = cars.stream()
                .filter(yearFilterPredicate)
                .map(carService::saveOrUpdateCar)
                .collect(Collectors.toList());

        return ResponseEntity.ok(processedTransactions);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные о машине",
            description = "Обновляет данные о машине по ID")
    @ApiResponse(responseCode = "200", description = "Данные о машине обновлены")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @ApiResponse(responseCode = "404", description = "Данные о машине не найдены")
    public ResponseEntity<Car> updateCar(@PathVariable Long id,
                                         @Valid @RequestBody Car carDetails) {

        if (carDetails.getYear() < 1980 || carDetails.getYear() > 2025) {
            throw new BadRequestException("Year must be between 1980 and 2025");
        }

        // Проверка существования клиента
        if (carDetails.getCustomer() == null || carDetails.getCustomer().getId() == null ||
                !carService.customerExists(carDetails.getCustomer().getId())) {
            throw new BadRequestException("Customer with specified ID does not exist");
        }

        // Проверка на уникальность VIN (если VIN уже есть у другой машины)
        if (carService.existsByVinAndIdNot(carDetails.getVin(), id)) {
            throw new BadRequestException("VIN already exists for another car");
        }

        Car car = carService.getCarById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id " + id));

        car.setBrand(carDetails.getBrand());
        car.setModel(carDetails.getModel());
        car.setVin(carDetails.getVin());
        car.setYear(carDetails.getYear());
        car.setCustomer(carDetails.getCustomer());

        Car updatedCar = carService.saveOrUpdateCar(car);
        return ResponseEntity.ok(updatedCar);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить машину", description = "Удаляет машину по ID")
    @ApiResponse(responseCode = "204", description = "Машина успешно удалена")
    @ApiResponse(responseCode = "404", description = "Машина не найдена")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
