package com.scqms.controller;

import com.scqms.entity.Booking;
import com.scqms.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/create/{employeeId}")
    public ResponseEntity<?> create(@PathVariable Long employeeId) {
        Booking b = bookingService.createBooking(employeeId);
        // try assign right away
        bookingService.tryAssignNext();
        return ResponseEntity.ok(b);
    }

    @PostMapping("/assign-next")
    public ResponseEntity<?> assignNext() {
        Booking b = bookingService.tryAssignNext();
        if (b == null) return ResponseEntity.ok("No assignment possible right now");
        return ResponseEntity.ok(b);
    }
}
