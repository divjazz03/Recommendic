package com.divjazz.recommendic.user.model.userAttributes;


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
