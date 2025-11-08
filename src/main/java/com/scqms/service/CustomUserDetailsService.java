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
    public UserDetails loadUserByUsername(String usernameOrEmailOrMobile) throws UsernameNotFoundException {

        // Try to load Employee by email
        Employee emp = employeeRepository.findByEmail(usernameOrEmailOrMobile).orElse(null);
        if (emp != null) {
            return User.withUsername(emp.getEmail())
                    .password(emp.getPassword())
                    .roles(emp.getRole())
                    .build();
        }

        // Try to load Driver by mobile
        Driver driver = driverRepository.findByMobile(usernameOrEmailOrMobile).orElse(null);
        if (driver != null) {
            return User.withUsername(driver.getMobile())
                    .password(driver.getPassword())
                    .roles("DRIVER")
                    .build();
        }

        throw new UsernameNotFoundException("User not found with email or mobile: " + usernameOrEmailOrMobile);
    }
}
