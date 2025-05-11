package com.example.security.payload.response;

import java.util.List;

import com.example.security.enums.ClientType;
import com.example.security.enums.ServicesPackage;


public class UserInfoResponse {
	private Long id;
	private String username;
	private String email;
	private ClientType clientType;
    private String name;
    private String lastName;
    private String companyName;
    private String pib;
    private String address;
    private String city;
    private String country;
    private String phoneNumber;
	private ServicesPackage servicesPackage;
	private List<String> roles;

	public UserInfoResponse(Long id, String username, String email, ClientType clientType, String name, String lastName, String companyName, String pib, String address, String city,
		 String country, String phoneNumber, ServicesPackage servicesPackage, List<String> roles) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.clientType = clientType;
        this.name = name;
        this.lastName = lastName;
        this.companyName = companyName;
        this.pib = pib;
        this.address = address;
        this.city = city;
        this.country = country;
        this.phoneNumber = phoneNumber;
		this.servicesPackage = servicesPackage;
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

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

	public List<String> getRoles() {
		return roles;
	}
}
