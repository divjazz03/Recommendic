package com.divjazz.recommendic.user.model.userAttributes;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
public enum Role {

    PATIENT("ROLE_PATIENT", "PATIENT"),
    CONSULTANT("ROLE_CONSULTANT","CONSULTANT"),
    ADMIN("ROLE_ADMIN","ADMIN"),
    SUPER_ADMIN("ROLE_SUPER_ADMIN","SUPER_ADMIN"),
    SYSTEM("ROLE_SYSTEM","SYSTEM");



    private final String name;
    private final String permissions;

    Role(String name, String permissions) {
        this.name = name;
        this.permissions = permissions;
    }


}
