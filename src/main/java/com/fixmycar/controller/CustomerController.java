package com.fixmycar.controller;

import com.fixmycar.exception.BadRequestException;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Car;
import com.fixmycar.model.Customer;
import com.fixmycar.service.CustomerService;
import com.fixmycar.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/home/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Controller", description = "API для управления клиентами")
public class CustomerController {
    private final CustomerService customerService;
    private final CarService carService;

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить клиента по ID",
            description = "Возвращает клиента по указанному ID")
    @ApiResponse(responseCode = "200", description = "Клиент найден")
    @ApiResponse(responseCode = "404", description = "Клиент не найден")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id " + id));
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    @Operation(summary = "Создать клиента", description = "Создает нового клиента")
    @ApiResponse(responseCode = "200", description = "Клиент успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        if (customerService.existsByEmail(customer.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (customerService.existsByPhone(customer.getPhone())) {
            throw new BadRequestException("Phone number already exists");
        }
        Customer createdCustomer = customerService.saveOrUpdateCustomer(customer);
        return ResponseEntity.ok(createdCustomer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные о клиенте",
            description = "Обновляет данные о клиенте по ID")
    @ApiResponse(responseCode = "200", description = "Данные о клиенте обновлены")
    @ApiResponse(responseCode = "404", description = "Данные о клиенте не найдены")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id,
                                                   @RequestBody Customer customer) {
        Customer existing = customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));

        if (customerService.existsByEmailAndIdNot(customer.getEmail(), id)) {
            throw new BadRequestException("Email already exists");
        }
        if (customerService.existsByPhoneAndIdNot(customer.getPhone(), id)) {
            throw new BadRequestException("Phone number already exists");
        }

        // Обновляем данные существующего клиента
        existing.setFirstName(customer.getFirstName());
        existing.setLastName(customer.getLastName());
        existing.setEmail(customer.getEmail());
        existing.setPhone(customer.getPhone());

        Customer updatedCustomer = customerService.saveOrUpdateCustomer(existing);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить клиента", description = "Удаляет клиента по ID")
    @ApiResponse(responseCode = "204", description = "Клиент успешно удален")
    @ApiResponse(responseCode = "404", description = "Клиент не найден")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/cars")
    @Operation(summary = "Получить машины пользователя")
    @ApiResponse(responseCode = "200", description = "Автомобили найдены")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<List<Car>> getUserCars(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));

        // Нужно, чтобы CarService имел метод getCarsByCustomerId(Long customerId)
        List<Car> cars = carService.getCarsByCustomerId(id);
        return ResponseEntity.ok(cars);
    }
}