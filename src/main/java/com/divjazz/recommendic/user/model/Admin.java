package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.UserType;
import com.divjazz.recommendic.user.model.certification.Assignment;
import com.divjazz.recommendic.user.model.userAttributes.*;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Entity
public class Admin extends AbstractEntity<UserId> {

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User adminUser;
    @OneToMany
    @JoinColumn(name = "assignment_id")
    private Set<Assignment> assignment;
    protected Admin() {
    }

    public Admin(UserId id, User adminUser) {
        super(id);
        this.adminUser = adminUser;
    }

    public User getAdminUser() {
        return adminUser;
    }

    public Set<Assignment> getAssignment() {
        return assignment;
    }

    @Override
    public String toString(){
        return "Admin: name -> " + adminUser.getUserNameObject().getFullName() +
        "email -> " + adminUser.getEmail() +
        "gender -> " + adminUser.getGender().name();
    }
}
