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

    // ✅ EMPLOYEE REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");
        String confirmPassword = body.get("confirmPassword");

        if (email == null || password == null || confirmPassword == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Email, password, and confirmPassword are required."));

        if (!password.equals(confirmPassword))
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match."));

        if (employeeRepository.findByEmail(email).isPresent())
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered."));

        Employee emp = new Employee();
        emp.setName(name);
        emp.setEmail(email);
        emp.setPassword(passwordEncoder.encode(password));
        emp.setRole("EMPLOYEE");

        employeeRepository.save(emp);
        return ResponseEntity.ok(Map.of("message", "Employee registered successfully!", "email", email));
    }

    // ✅ EMPLOYEE LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required."));

        Employee emp = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Authenticate using Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", emp.getEmail());
        response.put("name", emp.getName());
        response.put("role", emp.getRole());

        return ResponseEntity.ok(response);
    }
}
