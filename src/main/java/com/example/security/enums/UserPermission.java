package com.example.security.enums;

public enum UserPermission {
    // Klijent
    UPDATE_PROFILE,
    REQUEST_AD,
    CHANGE_SERVICE_PACKAGE,
    VIEW_OWN_ADS,
    
    // Zaposleni
    CREATE_AD,
    VIEW_ALL_ADS,
    VIEW_CLIENT_ADS,
    VIEW_OWN_CREATED_ADS,
    
    // Administrator
    VIEW_ALL_USERS,
    MANAGE_EMPLOYEES,
    MONITOR_SYSTEM,
    MANAGE_PERMISSIONS,
    DELETE_AD,
    EDIT_AD,
    BAN_USER,
    
    // Neautentifikovani korisnici
    REQUEST_REGISTRATION,
    LOGIN,
    VIEW_PUBLIC_OFFERS

}
