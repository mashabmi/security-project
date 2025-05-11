package com.example.security.security.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.security.enums.ClientType;
import com.example.security.enums.ServicesPackage;
import com.example.security.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

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
    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password, ClientType clientType, String name, String lastName, String companyName, String pib, String address, String city,
    String country, String phoneNumber, ServicesPackage servicesPackage, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
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
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                                .collect(Collectors.toList());

        return new UserDetailsImpl(user.getId(), 
                                user.getUsername(), 
                                user.getEmail(),
                                user.getPassword(),
                                user.getClientType(),
                                user.getName(),
                                user.getLastName(),
                                user.getCompanyName(),
                                user.getPib(),
                                user.getAddress(),
                                user.getCity(),
                                user.getCountry(),
                                user.getPhoneNumber(),
                                user.getServicesPackage(),
                                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

	public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCompanyName() {
      return companyName;
    }

    public String getPib() {
      return pib;
    }

	public ClientType getClientType() {
        return clientType;
    }

    public ServicesPackage getServicesPackage() {
        return servicesPackage;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
        return true;
        if (o == null || getClass() != o.getClass())
        return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

}
