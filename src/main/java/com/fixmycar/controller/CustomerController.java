package com.fixmycar.controller;

import com.fixmycar.exception.BadRequestException;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.Customer;
import com.fixmycar.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/home/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Controller", description = "API для управления клиентами")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Получить список клиентов",
            description = "Возвращает клиентов")
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
    @ApiResponse(responseCode = "201", description = "Клиент успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {

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
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @ApiResponse(responseCode = "404", description = "Клиент не найден")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id,
                                                   @Valid @RequestBody Customer customer) {

        Customer existing = customerService.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id "
                        + id));

        if (customerService.existsByEmailAndIdNot(customer.getEmail(), id)) {
            throw new BadRequestException("Email already exists");
        }

        if (customerService.existsByPhoneAndIdNot(customer.getPhone(), id)) {
            throw new BadRequestException("Phone number already exists");
        }

        existing.setFirstName(customer.getFirstName());
        existing.setLastName(customer.getLastName());
        existing.setPhone(customer.getPhone());
        existing.setEmail(customer.getEmail());

        Customer updatedCustomer = customerService.saveOrUpdateCustomer(existing);
        return ResponseEntity.ok(updatedCustomer);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить клиента", description = "Удаляет клиента по ID")
    @ApiResponse(responseCode = "204", description = "Клиент успешно удален")
    @ApiResponse(responseCode = "404", description = "Клиент не найден")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}