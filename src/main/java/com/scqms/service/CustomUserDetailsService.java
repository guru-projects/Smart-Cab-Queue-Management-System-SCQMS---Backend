package com.scqms.service;

import com.scqms.entity.Employee;
import com.scqms.entity.Driver;
import com.scqms.repository.EmployeeRepository;
import com.scqms.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ✅ 1. Try Employee (email or username)
        Optional<Employee> empOpt = employeeRepository.findByEmail(username);

        if (empOpt.isEmpty()) {
            empOpt = employeeRepository.findByUsername(username);
        }

        // ✅ 2. If not found, try Driver (mobile)
        if (empOpt.isEmpty()) {
            Optional<Driver> driverOpt = driverRepository.findByMobile(username);

            if (driverOpt.isEmpty()) {
                throw new UsernameNotFoundException("User not found with email, username, or mobile: " + username);
            }

            Driver driver = driverOpt.get();

            // ✅ Return Spring User for DRIVER with proper authority
            return new org.springframework.security.core.userdetails.User(
                    driver.getMobile(),
                    driver.getPassword(),
                    Collections.singleton(new SimpleGrantedAuthority(
                            driver.getRole().startsWith("ROLE_") ? driver.getRole() : "ROLE_" + driver.getRole()
                    ))
            );
        }

        // ✅ 3. Return Spring User for EMPLOYEE / ADMIN
        Employee emp = empOpt.get();
        String identifier = emp.getEmail() != null ? emp.getEmail() : emp.getUsername();
        String role = emp.getRole() != null ? emp.getRole() : "EMPLOYEE";

        return new org.springframework.security.core.userdetails.User(
                identifier,
                emp.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(
                        role.startsWith("ROLE_") ? role : "ROLE_" + role
                ))
        );
    }
}
