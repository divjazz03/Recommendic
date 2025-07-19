package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.AdminProfile;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Setter
public class Admin extends User {
    @OneToMany(mappedBy = "adminAssigned",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Assignment> assignment;
    @OneToOne(mappedBy = "admin", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference
    private AdminProfile adminProfile;


    protected Admin() {
    }


    public Admin(
            String email,
            Gender gender, UserCredential userCredential) {
        super( email, gender, Role.ADMIN, userCredential, UserType.ADMIN);
        assignment = new HashSet<>(20);
    }
    public void addAssignment(Assignment assignment) {
        this.assignment.add(assignment);
        assignment.setAdminAssigned(this);
    }

    public void removeAssignment(Assignment assignment) {
        assignment.setAdminAssigned(null);
        this.assignment.remove(assignment);
    }

    @Override
    public String toString() {
        return "Admin{" + "userId="+super.getUserId()+
                '}';
    }
}
