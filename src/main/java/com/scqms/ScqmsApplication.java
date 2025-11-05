package com.scqms;

import com.scqms.entity.Cab;
import com.scqms.entity.Driver;
import com.scqms.entity.Employee;
import com.scqms.enums.CabStatus;
import com.scqms.repository.CabRepository;
import com.scqms.repository.DriverRepository;
import com.scqms.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class ScqmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScqmsApplication.class, args);
    }

    // Seed sample data (4 cabs + drivers, 1 admin, 1 employee)
    @Bean
    CommandLineRunner runner(CabRepository cabRepo, DriverRepository driverRepo, EmployeeRepository empRepo) {
        return args -> {
            if (cabRepo.count() == 0) {
                for (int i = 1; i <= 4; i++) {
                    Cab c = new Cab();
                    c.setCabNumber("TN01AB100" + i);
                    c.setDriverName("Driver" + i);
                    c.setStatus(CabStatus.AVAILABLE);
                    c.setLastUpdated(LocalDateTime.now());
                    cabRepo.save(c);

                    Driver d = new Driver();
                    d.setUsername("driver" + i);
                    // password should be encoded; for seeding we'll set plaintext and AuthService encodes when creating
                    d.setPassword("driverpass" + i);
                    d.setCab(c);
                    driverRepo.save(d);
                }
            }
            if (empRepo.count() == 0) {
                Employee admin = new Employee();
                admin.setUsername("admin");
                admin.setPassword("adminpass");
                admin.setRole("ADMIN");
                empRepo.save(admin);

                Employee emp = new Employee();
                emp.setUsername("employee");
                emp.setPassword("emppass");
                emp.setRole("EMPLOYEE");
                empRepo.save(emp);
            }
        };
    }
}
