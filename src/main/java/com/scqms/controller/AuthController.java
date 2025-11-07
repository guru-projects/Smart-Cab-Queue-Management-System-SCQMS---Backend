package com.scqms.controller;

import com.scqms.config.JwtUtil;
import com.scqms.entity.Employee;
import com.scqms.entity.Driver;
import com.scqms.repository.EmployeeRepository;
import com.scqms.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmployeeRepository employeeRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ Register: Employee / Driver / Admin
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = passwordEncoder.encode(body.get("password"));
        String role = body.getOrDefault("role", "EMPLOYEE").toUpperCase();

        switch (role) {
            case "DRIVER" -> {
                Driver driver = new Driver();
                driver.setUsername(username);
                driver.setPassword(password);
                driverRepository.save(driver);
            }
            case "ADMIN" -> {
                Employee admin = new Employee();
                admin.setUsername(username);
                admin.setPassword(password);
                admin.setRole("ADMIN");
                employeeRepository.save(admin);
            }
            default -> {
                Employee emp = new Employee();
                emp.setUsername(username);
                emp.setPassword(password);
                emp.setRole("EMPLOYEE");
                employeeRepository.save(emp);
            }
        }

        return ResponseEntity.ok(Map.of("message", "Registered successfully", "username", username, "role", role));
    }

    // ✅ Login: Works for all roles
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", username);

        // Determine role
        String role = employeeRepository.findByUsername(username)
                .map(Employee::getRole)
                .orElse(driverRepository.findByUsername(username)
                        .map(d -> "DRIVER").orElse("UNKNOWN"));
        response.put("role", role);

        return ResponseEntity.ok(response);
    }
}
