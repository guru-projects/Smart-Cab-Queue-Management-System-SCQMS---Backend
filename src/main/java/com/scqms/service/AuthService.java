package com.scqms.service;

import com.scqms.entity.Driver;
import com.scqms.entity.Employee;
import com.scqms.repository.DriverRepository;
import com.scqms.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // try employee table first (covers admin & employee)
        Employee emp = employeeRepository.findByUsername(username).orElse(null);
        if (emp != null) {
            String role = emp.getRole() == null ? "EMPLOYEE" : emp.getRole();
            return new User(emp.getUsername(), passwordEncoder.encode(emp.getPassword()),
                    List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        }

        Driver d = driverRepository.findByUsername(username).orElse(null);
        if (d != null) {
            return new User(d.getUsername(), passwordEncoder.encode(d.getPassword()),
                    List.of(new SimpleGrantedAuthority("ROLE_DRIVER")));
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
