package com.divjazz.recommendic.user.model.userAttributes;

import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.User;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.UUID;

@Entity
public class AdminPassword extends AbstractEntity<UserId>{

    @Id
    private UserId passwordId;

    @ManyToOne
    private User assignedAdmin;

    private String password;

    protected AdminPassword() {
    }

    public AdminPassword(UserId passwordId, User assignedAdmin, String password) {
        this.passwordId = passwordId;
        this.assignedAdmin = assignedAdmin;
        this.password = password;
    }

    public User getAssignedAdmin() {
        return assignedAdmin;
    }

    public void setAssignedAdmin(User assignedAdmin) {
        this.assignedAdmin = assignedAdmin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
