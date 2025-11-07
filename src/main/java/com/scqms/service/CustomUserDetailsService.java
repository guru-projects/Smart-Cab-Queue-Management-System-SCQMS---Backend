package com.scqms.service;

import com.scqms.entity.Employee;
import com.scqms.entity.Driver;
import com.scqms.repository.EmployeeRepository;
import com.scqms.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final DriverRepository driverRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Employee emp = employeeRepository.findByEmail(usernameOrEmail).orElse(null);
        if (emp != null) {
            return User.withUsername(emp.getEmail())
                    .password(emp.getPassword())
                    .roles(emp.getRole())
                    .build();
        }

        Driver driver = driverRepository.findByUsername(usernameOrEmail).orElse(null);
        if (driver != null) {
            return User.withUsername(driver.getUsername())
                    .password(driver.getPassword())
                    .roles("DRIVER")
                    .build();
        }

        throw new UsernameNotFoundException("User not found with email/username: " + usernameOrEmail);
    }
}
