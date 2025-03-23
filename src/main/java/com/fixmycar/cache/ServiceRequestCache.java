package com.fixmycar.cache;

import com.fixmycar.model.ServiceRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory cache for storing previously requested ServiceRequest data.
 * Uses ConcurrentHashMap for thread-safe operations in a multi-threaded environment.
 */
@Component
public class ServiceRequestCache {
    
    private final Map<Long, List<ServiceRequest>> customerRequestsCache = new ConcurrentHashMap<>();
    private final Map<Long, List<ServiceRequest>> serviceCenterRequestsCache = new ConcurrentHashMap<>();
    private final Map<String, List<ServiceRequest>> customQueryCache = new ConcurrentHashMap<>();
    
    /**
     * Retrieves service requests for a specific customer from the cache.
     * 
     * @param customerId The ID of the customer
     * @return List of ServiceRequest if found in cache, null otherwise
     */
    public List<ServiceRequest> getRequestsByCustomerId(Long customerId) {
        return customerRequestsCache.get(customerId);
    }
    
    /**
     * Stores service requests for a specific customer in the cache.
     * 
     * @param customerId The ID of the customer
     * @param requests List of ServiceRequest to cache
     */
    public void storeRequestsByCustomerId(Long customerId, List<ServiceRequest> requests) {
        customerRequestsCache.put(customerId, requests);
    }
    
    /**
     * Retrieves service requests for a specific service center from the cache.
     * 
     * @param serviceCenterId The ID of the service center
     * @return List of ServiceRequest if found in cache, null otherwise
     */
    public List<ServiceRequest> getRequestsByServiceCenterId(Long serviceCenterId) {
        return serviceCenterRequestsCache.get(serviceCenterId);
    }
    
    /**
     * Stores service requests for a specific service center in the cache.
     * 
     * @param serviceCenterId The ID of the service center
     * @param requests List of ServiceRequest to cache
     */
    public void storeRequestsByServiceCenterId(Long serviceCenterId, List<ServiceRequest> requests) {
        serviceCenterRequestsCache.put(serviceCenterId, requests);
    }
    
    /**
     * Retrieves service requests based on a custom query key from the cache.
     * 
     * @param cacheKey A unique key representing the query parameters
     * @return List of ServiceRequest if found in cache, null otherwise
     */
    public List<ServiceRequest> getRequestsByCustomQueryKey(String cacheKey) {
        return customQueryCache.get(cacheKey);
    }
    
    /**
     * Stores service requests for a custom query in the cache.
     * 
     * @param cacheKey A unique key representing the query parameters
     * @param requests List of ServiceRequest to cache
     */
    public void storeRequestsByCustomQueryKey(String cacheKey, List<ServiceRequest> requests) {
        customQueryCache.put(cacheKey, requests);
    }
    
    /**
     * Clears all cached data.
     */
    public void clearCache() {
        customerRequestsCache.clear();
        serviceCenterRequestsCache.clear();
        customQueryCache.clear();
    }
    
    /**
     * Removes a specific customer's cached requests.
     * 
     * @param customerId The ID of the customer
     */
    public void invalidateCustomerCache(Long customerId) {
        customerRequestsCache.remove(customerId);
    }
    
    /**
     * Removes a specific service center's cached requests.
     * 
     * @param serviceCenterId The ID of the service center
     */
    public void invalidateServiceCenterCache(Long serviceCenterId) {
        serviceCenterRequestsCache.remove(serviceCenterId);
    }
}
