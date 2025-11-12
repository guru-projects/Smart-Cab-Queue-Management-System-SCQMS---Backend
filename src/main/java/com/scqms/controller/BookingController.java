package com.scqms.controller;

import com.scqms.entity.Booking;
import com.scqms.entity.Cab;
import com.scqms.entity.Employee;
import com.scqms.enums.Status;
import com.scqms.repository.CabRepository;
import com.scqms.repository.EmployeeRepository;
import com.scqms.service.BookingService;
import com.scqms.service.CabService; // ✅ Import this
import lombok.RequiredArgsConstructor;
import com.scqms.repository.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final CabService cabService;  // ✅ Add this field
    private final BookingRepository bookingRepository;
    private final CabRepository cabRepository;
    private final EmployeeRepository employeeRepository;


    @PostMapping("/create/{employeeId}")
    public ResponseEntity<?> createBooking(
            @PathVariable Long employeeId,
            @RequestBody(required = false) Map<String, String> payload) {

        String pickupType = payload != null && payload.containsKey("pickupType")
                ? payload.get("pickupType")
                : "STATION";

        Booking booking = bookingService.createBooking(employeeId, pickupType);
        return ResponseEntity.ok(booking);
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

    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        Booking cancelled = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(cancelled);
    }
}
