package com.fixmycar.repository;

import com.fixmycar.model.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findByVin(String vin);

    List<Car> findByCustomerId(Long customerId);

    List<Car> findByServiceCentersId(Long serviceCenterId);
    
    /**
     * Find cars by brand and model parameters
     */
    List<Car> findByBrandAndModel(String brand, String model);
    
    /**
     * Find cars associated with a service center by service center's name
     * Using JPQL query with JOIN to filter by nested entity
     */
    @Query("SELECT c FROM Car c JOIN c.serviceCenters sc WHERE sc.name = :serviceCenterName")
    List<Car> findByServiceCentersName(@Param("serviceCenterName") String serviceCenterName);
    
    /**
     * Find cars by brand and model with native SQL query
     */
    @Query(value = "SELECT * FROM car c WHERE c.brand = :brand AND c.model = :model", nativeQuery = true)
    List<Car> findByBrandAndModelNative(@Param("brand") String brand, @Param("model") String model);
    
    /**
     * Find cars by service center ID with eager loading of customer and service centers
     * Using EntityGraph for optimized loading of related entities
     */
    @EntityGraph(attributePaths = {"customer", "serviceCenters"})
    List<Car> findWithCustomerAndServiceCentersByServiceCentersId(Long serviceCenterId);
}
