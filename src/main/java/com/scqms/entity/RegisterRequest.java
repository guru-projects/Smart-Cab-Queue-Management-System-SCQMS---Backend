package com.scqms.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String name;
    private String email;       // for employee
    private String username;    // optional
    private String mobile;      // for driver
    private String cabNumber;   // for driver
    private String password;
    private String confirmPassword;
}
