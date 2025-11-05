package com.scqms.controller;

import com.scqms.entity.Booking;
import com.scqms.service.BookingService;
import com.scqms.service.CabService; // ✅ Import this
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final CabService cabService;  // ✅ Add this field

    @PostMapping("/create/{employeeId}")
    public ResponseEntity<?> create(@PathVariable Long employeeId) {
        Booking b = bookingService.createBooking(employeeId);
        bookingService.tryAssignNext();
        return ResponseEntity.ok(b);
    }

    @PostMapping("/assign-next")
    public ResponseEntity<?> assignNext() {
        Booking b = bookingService.tryAssignNext();
        if (b == null) return ResponseEntity.ok("No assignment possible right now");
        return ResponseEntity.ok(b);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(bookingService.getBookingsByEmployee(employeeId));
    }

    @PutMapping("/update-status/{cabId}")
    public ResponseEntity<?> updateStatus(@PathVariable Long cabId, @RequestParam String status) {
        return ResponseEntity.ok(cabService.updateStatus(cabId, status)); // ✅ Now resolves
    }

    @PutMapping("/complete/{bookingId}")
    public ResponseEntity<?> completeBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.completeBooking(bookingId));
    }
}
