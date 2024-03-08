package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.model.userAttributes.*;
import jakarta.persistence.Entity;

@Entity
public class Admin extends User{

    protected Admin() {
    }

    public Admin(UserId id, UserName userName, Email email, PhoneNumber phoneNumber, Gender gender) {
        super(id, userName, email, phoneNumber, gender);
    }
}
