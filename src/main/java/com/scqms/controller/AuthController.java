package com.scqms.controller;

import com.scqms.entity.Employee;
import com.scqms.repository.EmployeeRepository;
import com.scqms.service.AuthService;
import com.scqms.service.CustomUserDetailsService;
import com.scqms.config.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          EmployeeRepository employeeRepository,
                          PasswordEncoder passwordEncoder,
                          AuthService authService,
                          CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.userDetailsService = userDetailsService;
    }

    // ✅ Register new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Employee employee) {
        if (employeeRepository.findByUsername(employee.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already exists"));
        }

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeRepository.save(employee);

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    // ✅ Login and generate JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // ✅ Load user details using the correct service
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // ✅ Generate JWT
            String token = jwtUtil.generateToken(userDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", username);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }
}
