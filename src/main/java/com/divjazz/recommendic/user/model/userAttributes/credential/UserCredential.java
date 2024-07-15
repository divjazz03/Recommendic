package com.divjazz.recommendic.user.model.userAttributes.credential;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;

import java.util.UUID;

@MappedSuperclass
public class UserCredential extends Auditable {
    private String password;

    protected UserCredential(){}

    public UserCredential(String password, UUID referenceId) {
        super(referenceId);
    }

    public String getPassword() {
        return password;
    }
}
