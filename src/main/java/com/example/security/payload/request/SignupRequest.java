package com.example.security.payload.request;

import java.util.Set;

import com.example.security.enums.ClientType;
import com.example.security.enums.ServicesPackage;
import com.example.security.validation.PasswordMatching;
import com.example.security.validation.StrongPassword;

import jakarta.validation.constraints.*;

@PasswordMatching(
    password = "password",
    confirmPassword = "confirmPassword",
    message = "Password and Confirm Password must be matched!"
)
public class SignupRequest {
    @Size(min = 3, max = 20)
    private String name;

    @Size(min = 3, max = 20)
    private String lastName;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    
    @NotBlank
    @StrongPassword
    private String password;

    private String confirmPassword;

    private String address;

    private String city;

    private String country;

    private String phoneNumber;

    private String companyName;

    private String pib;

    private Set<String> roles;

    private ClientType clientType;

    private ServicesPackage servicesPackage;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
  
    public String getUsername() {
      return username;
    }
  
    public void setUsername(String username) {
      this.username = username;
    }
  
    public String getEmail() {
      return email;
    }
  
    public void setEmail(String email) {
      this.email = email;
    }
  
    public String getPassword() {
      return password;
    }
  
    public void setPassword(String password) {
      this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPib() {
        return pib;
    }

    public void setPib(String pib) {
        this.pib = pib;
    }
  
    public Set<String> getRole() {
      return this.roles;
    }
  
    public void setRole(Set<String> role) {
        this.roles = role;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public ServicesPackage getServicesPackage() {
        return servicesPackage;
    }

    public void setServicesPackage(ServicesPackage servicesPackage) {
        this.servicesPackage = servicesPackage;
    }

}
