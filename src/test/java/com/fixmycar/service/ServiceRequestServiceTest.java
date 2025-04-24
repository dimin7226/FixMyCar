package com.fixmycar.service;

import com.fixmycar.cache.InMemoryCache;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.*;
import com.fixmycar.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceRequestServiceTest {

    @Mock private ServiceRequestRepository requestRepo;
    @Mock private CarRepository carRepo;
    @Mock private CustomerRepository customerRepo;
    @Mock private ServiceCenterRepository serviceCenterRepo;
    @Mock private InMemoryCache<Long, ServiceRequest> cache;

    @InjectMocks
    private ServiceRequestService service;

    private ServiceRequest request;
    private Car car = new Car(1L);
    private Customer customer = new Customer(1L);
    private ServiceCenter center = new ServiceCenter(1L, "FixIt", "Main St", "12345");

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        request = ServiceRequest.builder()
                .id(1L)
                .description("Fix brakes")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .car(car)
                .customer(customer)
                .serviceCenter(center)
                .build();
    }

    @Test
    void getRequestById_fromCache() {
        when(cache.get(1L)).thenReturn(request);
        var result = service.getRequestById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void saveRequest_setsDefaultsAndCaches() {
        request.setCreatedAt(null);
        request.setStatus(null);

        when(carRepo.findById(1L)).thenReturn(Optional.of(car));
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceCenterRepo.findById(1L)).thenReturn(Optional.of(center));
        when(requestRepo.save(any())).thenReturn(request);

        var result = service.saveRequest(request);

        assertEquals("PENDING", result.getStatus());
        assertNotNull(result.getCreatedAt());
        verify(cache).put(request.getId(), request);
    }

    @Test
    void updateRequest_modifiesAndCaches() {
        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepo.save(any())).thenReturn(request);

        ServiceRequest update = new ServiceRequest();
        update.setDescription("Changed desc");
        update.setStatus("DONE");

        var result = service.updateRequest(1L, update);
        assertEquals("DONE", result.getStatus());
        verify(cache).put(1L, result);
    }

    @Test
    void deleteRequest_removesAndEvicts() {
        service.deleteRequest(1L);
        verify(requestRepo).deleteById(1L);
        verify(cache).evict(1L);
    }

    @Test
    void getRequestsByCarAttributes_returnsMatchingRequests() {
        when(requestRepo.findByCarAttributes("Toyota", "Camry", 2020)).thenReturn(List.of(request));

        List<ServiceRequest> result = service.getRequestsByCarAttributes("Toyota", "Camry", 2020);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(requestRepo).findByCarAttributes("Toyota", "Camry", 2020);
    }

    @Test
    void getRequestsByCarId_returnsRequests() {
        when(requestRepo.findByCarId(1L)).thenReturn(List.of(request));

        List<ServiceRequest> result = service.getRequestsByCarId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(requestRepo).findByCarId(1L);
    }

    @Test
    void getRequestsByServiceCenterId_returnsRequests() {
        when(requestRepo.findByServiceCenterId(1L)).thenReturn(List.of(request));

        List<ServiceRequest> result = service.getRequestsByServiceCenterId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(requestRepo).findByServiceCenterId(1L);
    }

    @Test
    void getAllRequests_returnsAll() {
        when(requestRepo.findAll()).thenReturn(List.of(request));

        List<ServiceRequest> result = service.getAllRequests();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(requestRepo).findAll();
    }


    @Test
    void createServiceRequest_savesAndCaches() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(carRepo.findById(1L)).thenReturn(Optional.of(car));
        when(serviceCenterRepo.findById(1L)).thenReturn(Optional.of(center));
        ServiceRequest saved = ServiceRequest.builder()
                .id(1L)
                .description("Check engine")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .car(car)
                .customer(customer)
                .serviceCenter(center)
                .build();

        when(requestRepo.save(any())).thenReturn(saved);

        var result = service.createServiceRequest(1L, 1L, 1L, "Check engine");

        assertEquals("Check engine", result.getDescription());
        assertEquals("PENDING", result.getStatus());
        verify(cache).put(result.getId(), result);
    }

    @Test
    void updateStatus_setsNewStatus() {
        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepo.save(any())).thenReturn(request);
        var result = service.updateStatus(1L, "DONE");
        assertEquals("DONE", result.getStatus());
        verify(cache).put(1L, request);
    }

    @Test
    void getByCustomerId_delegates() {
        when(requestRepo.findByCustomerId(1L)).thenReturn(List.of(request));
        assertEquals(1, service.getRequestsByCustomerId(1L).size());
    }

    @Test
    void testStatusEqualsPending() {
        ServiceRequest request = new ServiceRequest();
        request.setStatus("PENDING");
        // тестируем код для "PENDING"
    }

    @Test
    void testStatusNotPending() {
        ServiceRequest request = new ServiceRequest();
        request.setStatus("DONE");
        // тестируем код для другого статуса
    }
}
