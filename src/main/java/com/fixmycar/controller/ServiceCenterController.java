package com.fixmycar.controller;

import com.fixmycar.exception.BadRequestException;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.service.ServiceCenterService;
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
@RequestMapping("/home/service-centers")
@RequiredArgsConstructor
@Tag(name = "Service Center Controller", description = "API для управления автосервисами")
public class ServiceCenterController {
    private final ServiceCenterService serviceCenterService;

    @GetMapping
    @Operation(summary = "Получить список сервисов",
            description = "Возвращает список автомобилей")
    public List<ServiceCenter> getAllServiceCenters() {
        return serviceCenterService.getAllServiceCenters();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить сервисный центр по ID",
            description = "Возвращает сервисный центр по указанному ID")
    @ApiResponse(responseCode = "200", description = "Сервисный центр найден")
    @ApiResponse(responseCode = "404", description = "Сервисный центр не найден")
    public ResponseEntity<ServiceCenter> getServiceCenterById(@PathVariable Long id) {
        ServiceCenter serviceCenter = serviceCenterService.getServiceCenterById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Service center not found with id " + id));
        return ResponseEntity.ok(serviceCenter);
    }

    @PostMapping
    @Operation(summary = "Создать сервисный центр", description = "Создает новый сервисный центр")
    @ApiResponse(responseCode = "201", description = "Сервисный центр успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<ServiceCenter> createServiceCenter(
            @Valid @RequestBody ServiceCenter serviceCenter) {
        // Проверка уникальности названия
        if (serviceCenterService.existsByName(serviceCenter.getName())) {
            throw new BadRequestException("Service center with this name already exists");
        }
        // Проверка уникальности адреса
        if (serviceCenterService.existsByAddress(serviceCenter.getAddress())) {
            throw new BadRequestException("Service center with this address already exists");
        }
        // Проверка уникальности телефона
        if (serviceCenterService.existsByPhone(serviceCenter.getPhone())) {
            throw new BadRequestException("Service center with this phone number already exists");
        }
        
        ServiceCenter createdServiceCenter = serviceCenterService.saveServiceCenter(serviceCenter);
        return ResponseEntity.ok(createdServiceCenter);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные о сервисном центре",
            description = "Обновляет данные о сервисном центре по ID")
    @ApiResponse(responseCode = "200", description = "Данные о сервисном центре обновлены")
    @ApiResponse(responseCode = "404", description = "Данные о сервисном центре не найдены")
    public ResponseEntity<ServiceCenter> updateServiceCenter(
            @PathVariable Long id, @Valid @RequestBody ServiceCenter serviceCenterDetails) {
        ServiceCenter serviceCenter = serviceCenterService.getServiceCenterById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service center not found with id " + id));

        // Проверка уникальности названия (если изменилось)
        if (!serviceCenter.getName().equals(serviceCenterDetails.getName())
                && serviceCenterService.existsByName(serviceCenterDetails.getName())) {
            throw new BadRequestException("Service center with this name already exists");
        }
        // Проверка уникальности адреса (если изменился)
        if (!serviceCenter.getAddress().equals(serviceCenterDetails.getAddress())
                && serviceCenterService.existsByAddress(serviceCenterDetails.getAddress())) {
            throw new BadRequestException("Service center with this address already exists");
        }
        // Проверка уникальности телефона (если изменился)
        if (!serviceCenter.getPhone().equals(serviceCenterDetails.getPhone())
                && serviceCenterService.existsByPhone(serviceCenterDetails.getPhone())) {
            throw new BadRequestException("Service center with this phone number already exists");
        }

        serviceCenter.setName(serviceCenterDetails.getName());
        serviceCenter.setAddress(serviceCenterDetails.getAddress());
        serviceCenter.setPhone(serviceCenterDetails.getPhone());
        if (serviceCenterDetails.getServiceRequests() != null) {
            serviceCenter.setServiceRequests(serviceCenterDetails.getServiceRequests());
        }
        ServiceCenter updatedServiceCenter = serviceCenterService.saveServiceCenter(serviceCenter);
        return ResponseEntity.ok(updatedServiceCenter);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить сервисный центр", description = "Удаляет сервисный центр по ID")
    @ApiResponse(responseCode = "204", description = "Сервисный центр успешно удален")
    @ApiResponse(responseCode = "404", description = "Сервисный центр не найден")
    public ResponseEntity<Void> deleteServiceCenter(@PathVariable Long id) {
        serviceCenterService.deleteServiceCenter(id);
        return ResponseEntity.noContent().build();
    }
}