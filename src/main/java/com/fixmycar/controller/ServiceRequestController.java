package com.fixmycar.controller;

import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.ServiceRequest;
import com.fixmycar.service.ServiceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home/requests")
@RequiredArgsConstructor
public class ServiceRequestController {
    private final ServiceRequestService requestService;

    @GetMapping
    public List<ServiceRequest> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить заявку по ID",
            description = "Возвращает заявку по указанному ID")
    @ApiResponse(responseCode = "200", description = "Заявка найдена")
    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
    public ResponseEntity<ServiceRequest> getRequestById(@PathVariable Long id) {
        ServiceRequest request = requestService.getRequestById(id);
        if (request == null) {
            throw new ResourceNotFoundException("Service request not found with id " + id);
        }
        return ResponseEntity.ok(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить заявку", description = "Удаляет заявку по ID")
    @ApiResponse(responseCode = "204", description = "Заявка успешно удалена")
    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Получить заявки клиента",
            description = "Возвращает все заявки по ID клиента")
    @ApiResponse(responseCode = "200", description = "Заявки найдены")
    @ApiResponse(responseCode = "404", description = "Клиент не найден")
    public List<ServiceRequest> getRequestsByCustomerId(@PathVariable Long customerId) {
        return requestService.getRequestsByCustomerId(customerId);
    }

    @GetMapping("/car/{carId}")
    @Operation(summary = "Получить заявки машины",
            description = "Возвращает все заявки по ID машины")
    @ApiResponse(responseCode = "200", description = "Заявки найдены")
    @ApiResponse(responseCode = "404", description = "Машина не найдена")
    public List<ServiceRequest> getRequestsByCarId(@PathVariable Long carId) {
        return requestService.getRequestsByCarId(carId);
    }

    @GetMapping("/service-center/{serviceCenterId}")
    @Operation(summary = "Получить заявки сервисного центра",
            description = "Возвращает все заявки по ID сервисного центра")
    @ApiResponse(responseCode = "200", description = "Заявки найдены")
    @ApiResponse(responseCode = "404", description = "Сервисный центр не найден")
    public List<ServiceRequest> getRequestsByServiceCenterId(@PathVariable Long serviceCenterId) {
        return requestService.getRequestsByServiceCenterId(serviceCenterId);
    }

    @PostMapping
    @Operation(summary = "Создать заявку", description = "Создает новую заявку на обслуживание")
    @ApiResponse(responseCode = "200", description = "Заявка успешно создана")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<ServiceRequest> createCompleteRequest(
            @RequestParam Long customerId,
            @RequestParam Long carId,
            @RequestParam Long serviceCenterId,
            @RequestParam String description) {
        if (customerId == null || carId == null || serviceCenterId == null) {
            throw new ValidationException("Customer ID, Car ID, and Service Center ID are required");
        }
        ServiceRequest serviceRequest = requestService.createServiceRequest(customerId,
                carId, serviceCenterId, description);
        return ResponseEntity.ok(serviceRequest);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Обновить статус заявки",
            description = "Обновляет статус заявки по ID")
    @ApiResponse(responseCode = "200", description = "Статус заявки обновлен")
    @ApiResponse(responseCode = "404", description = "Заявка не найдена")
    public ResponseEntity<ServiceRequest> updateStatus(
            @PathVariable Long id, @RequestParam String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new ValidationException("Status cannot be empty");
        }
        ServiceRequest updatedRequest = requestService.updateStatus(id, status);
        return ResponseEntity.ok(updatedRequest);
    }
}
