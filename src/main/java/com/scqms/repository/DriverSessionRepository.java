package com.scqms.repository;

import com.scqms.entity.Driver;
import com.scqms.entity.DriverSession;
import com.scqms.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DriverSessionRepository extends JpaRepository<DriverSession, Long> {

    @Query("SELECT s FROM DriverSession s WHERE s.driver.id = :driverId AND s.status = 'IN_USE'")
    Optional<DriverSession> findActiveByDriver(Long driverId);
}
