package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.certification.Assignment;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.userAttributes.credential.AdminCredential;
import com.divjazz.recommendic.user.model.userAttributes.credential.PatientCredential;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "admin")
public final class Admin extends User{
    @OneToMany
    private Set<Assignment> assignment;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "admin")
    private AdminCredential adminCredential;

    protected Admin(){}


    public Admin(
                 UserName userName,
                 String email,
                 String phoneNumber,
                 Gender gender,
                 Address address) {
        super(userName,email,phoneNumber,gender,address);
    }

    public Set<Assignment> getAssignment() {
        return assignment;
    }

    public void setAssignment(Set<Assignment> assignment) {
        this.assignment = assignment;
    }

    public AdminCredential getAdminCredential() {
        return adminCredential;
    }

    public void setAdminCredential(AdminCredential adminCredential) {
        this.adminCredential = adminCredential;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority("ADMIN"));
    }

    @Override
    public String getPassword() {
        return null;
    }
}
