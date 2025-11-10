package com.scqms.controller;

import com.scqms.config.JwtUtil;
import com.scqms.entity.Employee;
import com.scqms.entity.Driver;
import com.scqms.repository.EmployeeRepository;
import com.scqms.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173") // ✅ Allow frontend
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmployeeRepository employeeRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ EMPLOYEE REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> registerEmployee(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String username = body.get("username");
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
        emp.setUsername(username != null ? username : email); // fallback
        emp.setPassword(passwordEncoder.encode(password));
        emp.setRole("EMPLOYEE");

        employeeRepository.save(emp);
        return ResponseEntity.ok(Map.of("message", "Employee registered successfully!", "email", emp.getEmail()));
    }

    // ✅ EMPLOYEE LOGIN (by email or username)
    @PostMapping("/login")
    public ResponseEntity<?> loginEmployee(@RequestBody Map<String, String> body) {
        String identifier = body.get("email"); // can be email OR username
        String password = body.get("password");

        if (identifier == null || password == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Email/Username and password are required."));

        // Authenticate (Spring will call CustomUserDetailsService)
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, password)
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials."));
        }

        // Lookup employee by email or username
        Optional<Employee> empOpt = employeeRepository.findByEmail(identifier);
        if (empOpt.isEmpty()) empOpt = employeeRepository.findByUsername(identifier);

        if (empOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Employee not found."));

        Employee emp = empOpt.get();
        User user = new User(
                emp.getEmail(),
                emp.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + emp.getRole()))
        );

        String token = jwtUtil.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", emp.getEmail());
        response.put("name", emp.getName());
        response.put("role", emp.getRole());
        response.put("id", emp.getId()); // ✅ add this line

        return ResponseEntity.ok(response);
    }

    // ✅ DRIVER REGISTER
    @PostMapping("/driver/register")
    public ResponseEntity<?> registerDriver(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String mobile = body.get("mobile");
        String password = body.get("password");
        String confirmPassword = body.get("confirmPassword");

        if (mobile == null || password == null || confirmPassword == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Mobile number and password are required."));

        if (!password.equals(confirmPassword))
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match."));

        if (driverRepository.findByMobile(mobile).isPresent())
            return ResponseEntity.badRequest().body(Map.of("error", "Mobile number already registered."));

        Driver driver = new Driver();
        driver.setName(name);
        driver.setMobile(mobile);
        driver.setPassword(passwordEncoder.encode(password));
        driver.setRole("DRIVER");

        driverRepository.save(driver);
        return ResponseEntity.ok(Map.of("message", "Driver registered successfully!", "mobile", mobile));
    }

    // ✅ DRIVER LOGIN (by mobile)
    @PostMapping("/driver/login")
    public ResponseEntity<?> loginDriver(@RequestBody Map<String, String> body) {
        String mobile = body.get("mobile");
        String password = body.get("password");

        if (mobile == null || password == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Mobile and password are required."));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(mobile, password)
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials."));
        }

        Driver driver = driverRepository.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("Driver not found."));

        // ✅ Proper authority object
        User user = new User(
                driver.getMobile(),
                driver.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_DRIVER"))
        );

        String token = jwtUtil.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("mobile", driver.getMobile());
        response.put("name", driver.getName());
        response.put("role", driver.getRole());
        response.put("id", driver.getId());

        return ResponseEntity.ok(response);
    }

}
