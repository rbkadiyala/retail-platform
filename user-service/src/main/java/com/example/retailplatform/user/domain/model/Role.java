package com.example.retailplatform.user.domain.model;

public enum Role {
    ADMIN,
    USER,
    SUPPORT,
    GUEST;
    
    public String getAuthority() {
        return "ROLE_" + this.name(); // Spring Security expects "ROLE_" prefix
    }
}
