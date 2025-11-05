package com.scqms.controller;

import com.scqms.config.JwtUtil;
import com.scqms.dto.AuthRequest;
import com.scqms.dto.AuthResponse;
import com.scqms.entity.Employee;
import com.scqms.repository.DriverRepository;
import com.scqms.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmployeeRepository employeeRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // determine role from employee or driver table
        String role = "EMPLOYEE";
        var empOpt = employeeRepository.findByUsername(request.getUsername());
        if (empOpt.isPresent()) {
            Employee e = empOpt.get();
            role = e.getRole() == null ? "EMPLOYEE" : e.getRole();
        } else if (driverRepository.findByUsername(request.getUsername()).isPresent()) {
            role = "DRIVER";
        }

        String token = jwtUtil.generateToken(request.getUsername(), role);
        return ResponseEntity.ok(new AuthResponse(token, role));
    }
}
