package com.example.security.model;


import com.example.security.enums.UserPermission;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UserPermission name;

    public Permission() {}

    public Permission(UserPermission name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserPermission getPermission() {
        return name;
    }

    public void setPermission(UserPermission name) {
        this.name = name;
    }

}
