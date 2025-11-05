package com.scqms.service;

import com.scqms.entity.Booking;
import com.scqms.entity.Cab;
import com.scqms.repository.BookingRepository;
import com.scqms.repository.CabRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CabRepository cabRepository;

    public Booking createBooking(Long employeeId) {
        Booking b = new Booking();
        b.setEmployeeId(employeeId);
        b.setCreatedAt(LocalDateTime.now());
        b.setStatus("QUEUED");
        return bookingRepository.save(b);
    }

    // assign next queued booking to next available cab
    public Booking tryAssignNext() {
        List<Booking> queued = bookingRepository.findByStatusOrderByCreatedAtAsc("QUEUED");
        if (queued.isEmpty()) return null;

        Cab cab = cabRepository.findFirstByStatus(com.scqms.enums.CabStatus.AVAILABLE).orElse(null);
        if (cab == null) return null;

        Booking toAssign = queued.get(0);
        toAssign.setCab(cab);
        toAssign.setStatus("ASSIGNED");
        bookingRepository.save(toAssign);

        cab.setStatus(com.scqms.enums.CabStatus.BUSY);
        cab.setLastUpdated(LocalDateTime.now());
        cabRepository.save(cab);

        return toAssign;
    }
}
