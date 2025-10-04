package com.hanguyen.demo_spring_bai1.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class UserUpdateRequest {

    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    private String password;

    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstname;

    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastname;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dod;

    public LocalDate getDod() {
        return dod;
    }

    public void setDod(LocalDate dod) {
        this.dod = dod;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
