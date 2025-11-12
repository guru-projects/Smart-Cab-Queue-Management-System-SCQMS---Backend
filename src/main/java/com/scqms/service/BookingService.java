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
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CabRepository cabRepository;
    private final EmployeeRepository employeeRepository;  // ✅ Added

    public Booking createBooking(Long employeeId, String pickupType) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Cab cab = cabRepository.findFirstByStatusIn(List.of(Status.AVAILABLE, Status.PARTIALLY_BUSY))
                .orElseThrow(() -> new RuntimeException("No available cab found nearby"));

        long activeBookings = bookingRepository.countByCabAndStatus(cab, Status.ASSIGNED);
        if (activeBookings >= 4) throw new RuntimeException("Cab is full (4 passengers already assigned)");

        Booking booking = new Booking();
        booking.setEmployee(employee);
        booking.setCab(cab);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setStatus(Status.ASSIGNED);

        // ✅ Auto assign pickup/drop
        if ("OFFICE".equalsIgnoreCase(pickupType)) {
            booking.setPickupLocation("Guindy Office");
            booking.setDropLocation("Guindy Station");
            cab.setCurrentLocation("OFFICE"); // 👈 Start from office
        } else {
            booking.setPickupLocation("Guindy Station");
            booking.setDropLocation("Guindy Office");
            cab.setCurrentLocation("STATION"); // 👈 Start from station
        }

        bookingRepository.save(booking);

        activeBookings++;
        if (activeBookings == 4) cab.setStatus(Status.BUSY);
        else cab.setStatus(Status.PARTIALLY_BUSY);

        cab.setLastUpdated(LocalDateTime.now());
        cabRepository.save(cab);

        return booking;
    }

    // Assign next queued booking to next available cab
    public Booking tryAssignNext() {
        List<Booking> queued = bookingRepository.findByStatusOrderByCreatedAtAsc(Status.QUEUED);
        if (queued.isEmpty()) return null;

        Cab cab = cabRepository.findFirstByStatusIn(List.of(Status.AVAILABLE, Status.PARTIALLY_BUSY)).orElse(null);
        if (cab == null) return null;

        Booking toAssign = queued.get(0);
        toAssign.setCab(cab);
        toAssign.setStatus(Status.ASSIGNED);
        bookingRepository.save(toAssign);

        long activeBookings = bookingRepository.countByCabAndStatus(cab, Status.ASSIGNED);

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
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<Booking> bookings = bookingRepository.findByEmployee(employee);

        bookings.forEach(b -> {
            if (b.getCab() != null) {
                Hibernate.initialize(b.getCab());
                b.getCab().getCurrentLocation(); // ✅ ensure field is fetched
                if (b.getCab().getDriver() != null) {
                    Hibernate.initialize(b.getCab().getDriver());
                }
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
        long activeBookings = bookingRepository.countByCabAndStatus(cab, Status.ASSIGNED);

        if (activeBookings == 0) cab.setStatus(Status.AVAILABLE);
        else if (activeBookings < 4) cab.setStatus(Status.PARTIALLY_BUSY);

        cab.setLastUpdated(LocalDateTime.now());
        cabRepository.save(cab);

        return booking;
    }

    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == Status.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed booking.");
        }

        booking.setStatus(Status.CANCELLED);
        bookingRepository.save(booking);

        Cab cab = booking.getCab();
        if (cab != null) {
            long activeBookings = bookingRepository.countByCabAndStatus(cab, Status.ASSIGNED);

            if (activeBookings == 0) cab.setStatus(Status.AVAILABLE);
            else if (activeBookings < 4) cab.setStatus(Status.PARTIALLY_BUSY);

            cabRepository.save(cab);
        }

        return booking;
    }

}