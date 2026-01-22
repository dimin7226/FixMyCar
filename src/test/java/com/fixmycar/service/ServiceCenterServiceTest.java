package com.fixmycar.service;

import com.fixmycar.cache.InMemoryCache;
import com.fixmycar.exception.ResourceNotFoundException;
import com.fixmycar.model.ServiceCenter;
import com.fixmycar.repository.ServiceCenterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceCenterServiceTest {

    @Mock
    private ServiceCenterRepository repository;

    @Mock
    private InMemoryCache<Long, ServiceCenter> cache;

    @InjectMocks
    private ServiceCenterService service;

    private ServiceCenter sc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        sc = new ServiceCenter(1L, "FixIt", "Main St", "12345");
    }

    @Test
    void getAllServiceCenters_returnsList() {
        when(repository.findAll()).thenReturn(List.of(sc));

        var result = service.getAllServiceCenters();

        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    void getServiceCenterById_fromCache() {
        when(cache.get(1L)).thenReturn(sc);

        var result = service.getServiceCenterById(1L);

        assertTrue(result.isPresent());
        assertEquals(sc, result.get());
        verify(repository, never()).findById(any());
    }

    @Test
    void getServiceCenterById_fromRepository() {
        when(cache.get(1L)).thenReturn(null);
        when(repository.findById(1L)).thenReturn(Optional.of(sc));

        var result = service.getServiceCenterById(1L);

        assertTrue(result.isPresent());
        verify(cache).put(eq(1L), any(ServiceCenter.class));
    }

    @Test
    void saveServiceCenter_savesAndCaches() {
        when(repository.save(sc)).thenReturn(sc);

        var result = service.saveServiceCenter(sc);

        assertEquals(sc, result);
        verify(repository).save(sc);
        verify(cache).put(eq(1L), any(ServiceCenter.class));
    }

    @Test
    void updateServiceCenter_updatesFields_andCaches() {
        ServiceCenter updated = new ServiceCenter(null, "New Name", "New Addr", "9999");

        when(repository.findById(1L)).thenReturn(Optional.of(sc));
        when(repository.save(any(ServiceCenter.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var result = service.updateServiceCenter(1L, updated);

        assertEquals("New Name", result.getName());
        assertEquals("New Addr", result.getAddress());
        assertEquals("9999", result.getPhone());

        verify(cache, times(2)).put(eq(1L), any(ServiceCenter.class));
    }

    @Test
    void updateServiceCenter_notFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateServiceCenter(2L, sc));
    }

    @Test
    void deleteServiceCenter_removesAndEvicts() {
        service.deleteServiceCenter(1L);

        verify(repository).deleteById(1L);
        verify(cache).evict(1L);
    }
}
