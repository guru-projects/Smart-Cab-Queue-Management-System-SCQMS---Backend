package com.scqms.service;

import com.scqms.entity.Employee;
import com.scqms.entity.Driver;
import com.scqms.repository.EmployeeRepository;
import com.scqms.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<Employee> empOpt = employeeRepository.findByEmail(username);

        if (empOpt.isEmpty()) {
            empOpt = employeeRepository.findByUsername(username);
        }

        if (empOpt.isEmpty()) {
            Optional<Driver> driverOpt = driverRepository.findByMobile(username);
            if (driverOpt.isEmpty()) {
                throw new UsernameNotFoundException("User not found with email, username, or mobile: " + username);
            }
            Driver driver = driverOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    driver.getMobile(),
                    driver.getPassword(),
                    Collections.singleton(() -> "ROLE_DRIVER")
            );
        }

        Employee emp = empOpt.get();
        String identifier = emp.getEmail() != null ? emp.getEmail() : emp.getUsername();
        String role = emp.getRole() != null ? emp.getRole() : "EMPLOYEE";

        return new org.springframework.security.core.userdetails.User(
                identifier,
                emp.getPassword(),
                Collections.singleton(() -> "ROLE_" + role)
        );
    }

}
