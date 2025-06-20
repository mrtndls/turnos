package com.unla.grupo16.models.enums;

public enum RoleType {

    ADMIN,
    CLIENTE;

    public String getPrefixedName() {
        return "ROLE_" + this.name();
    }
}
