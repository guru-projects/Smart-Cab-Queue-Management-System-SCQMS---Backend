package com.scqms.repository;

import com.scqms.entity.Booking;
import com.scqms.entity.Cab;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatusOrderByCreatedAtAsc(String status);
    List<Booking> findByEmployeeId(Long employeeId);

    long countByCabAndStatus(Cab cab, String assigned);
}
