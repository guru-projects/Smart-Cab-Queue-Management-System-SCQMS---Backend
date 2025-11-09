package com.scqms.repository;

import com.scqms.entity.Booking;
import com.scqms.entity.Cab;
import com.scqms.entity.Employee;
import com.scqms.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatusOrderByCreatedAtAsc(Status status);
    List<Booking> findByEmployeeId(Long employeeId);

    long countByCabAndStatus(Cab cab, Status assigned);

    List<Booking> findByEmployee(Employee employee);
}
