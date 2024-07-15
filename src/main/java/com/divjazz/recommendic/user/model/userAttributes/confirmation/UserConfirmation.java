package com.divjazz.recommendic.user.model.userAttributes.confirmation;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;


@MappedSuperclass
public abstract class UserConfirmation extends Auditable {
    private String key;


    public UserConfirmation(User admin) {
        this.key = UUID.randomUUID().toString();
    }
    protected UserConfirmation(){}

    public String getKey() {
        return key;
    }
}
