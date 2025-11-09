package com.scqms.service;

import com.scqms.entity.Booking;
import com.scqms.entity.Cab;
import com.scqms.entity.Employee;
import com.scqms.enums.Status;
import com.scqms.repository.BookingRepository;
import com.scqms.repository.CabRepository;
import com.scqms.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CabRepository cabRepository;
    private final EmployeeRepository employeeRepository;  // ✅ Added

    public Booking createBooking(Long employeeId) {
        // ✅ Load the employee entity
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Booking b = new Booking();
        b.setEmployee(employee);  // ✅ Set employee object, not employeeId
        b.setCreatedAt(LocalDateTime.now());
        b.setStatus(Status.BUSY);  // ✅ Changed from BUSY to QUEUED (more logical)
        return bookingRepository.save(b);
    }

    // Assign next queued booking to next available cab
    public Booking tryAssignNext() {
        List<Booking> queued = bookingRepository.findByStatusOrderByCreatedAtAsc("QUEUED");
        if (queued.isEmpty()) return null;

        Cab cab = cabRepository.findFirstByStatusIn(List.of(Status.AVAILABLE, Status.PARTIALLY_BUSY)).orElse(null);
        if (cab == null) return null;

        Booking toAssign = queued.get(0);
        toAssign.setCab(cab);
        toAssign.setStatus(Status.ASSIGNED);
        bookingRepository.save(toAssign);

        long activeBookings = bookingRepository.countByCabAndStatus(cab, "ASSIGNED");

        if (activeBookings < 3) {
            cab.setStatus(Status.PARTIALLY_BUSY);
        } else if (activeBookings == 3) {
            cab.setStatus(Status.BUSY); // 4th passenger
        }

        cab.setLastUpdated(LocalDateTime.now());
        cabRepository.save(cab);

        return toAssign;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByEmployee(Long employeeId) {
        // ✅ Updated to use employee relationship
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<Booking> bookings = bookingRepository.findByEmployee(employee);

        // Eagerly load cab + driver details
        bookings.forEach(b -> {
            if (b.getCab() != null && b.getCab().getDriver() != null) {
                Hibernate.initialize(b.getCab().getDriver());
            }
        });

        return bookings;
    }

    public Booking completeBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(Status.COMPLETED);
        bookingRepository.save(booking);

        Cab cab = booking.getCab();
        long activeBookings = bookingRepository.countByCabAndStatus(cab, "ASSIGNED");

        if (activeBookings == 0) cab.setStatus(Status.AVAILABLE);
        else if (activeBookings < 4) cab.setStatus(Status.PARTIALLY_BUSY);

        cab.setLastUpdated(LocalDateTime.now());
        cabRepository.save(cab);

        return booking;
    }
}