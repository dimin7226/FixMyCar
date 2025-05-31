package com.fixmycar.repository;

import com.fixmycar.model.ServiceCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCenterRepository extends JpaRepository<ServiceCenter, Long> {
 //   List<ServiceCenter> findByName(String name);

    boolean existsByName(String name);

    boolean existsByAddress(String address);

    boolean existsByPhone(String phone);
}