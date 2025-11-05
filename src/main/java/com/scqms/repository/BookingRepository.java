package com.scqms.repository;

import com.scqms.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatusOrderByCreatedAtAsc(String status);
    List<Booking> findByEmployeeId(Long employeeId);
}
