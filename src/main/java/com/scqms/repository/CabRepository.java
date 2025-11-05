package com.scqms.repository;

import com.scqms.entity.Cab;
import com.scqms.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CabRepository extends JpaRepository<Cab, Long> {
    List<Cab> findByStatus(Status status);
    Optional<Cab> findFirstByStatus(Status status);
}
