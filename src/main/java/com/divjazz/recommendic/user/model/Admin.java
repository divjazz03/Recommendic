package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.certification.Assignment;
import com.divjazz.recommendic.user.model.userAttributes.*;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
public final class Admin extends User{

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "assignment_id")
    private Set<Assignment> assignment;
    protected Admin() {
    }

    public Admin(UUID id,
                 UserName userName,
                 String email,
                 String phoneNumber,
                 Gender gender,
                 Address address) {
        super(id,userName,email,phoneNumber,gender,address);
    }


    public Set<Assignment> getAssignment() {
        return assignment;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ADMIN"));
    }
}
