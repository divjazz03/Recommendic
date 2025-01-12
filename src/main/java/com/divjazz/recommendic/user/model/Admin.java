package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "admin")
public class Admin extends User {
    @OneToMany(mappedBy = "adminAssigned", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Assignment> assignment;


    protected Admin() {
    }


    public Admin(
            UserName userName,
            String email,
            String phoneNumber,
            Gender gender,
            Address address,
            Role role, UserCredential userCredential) {
        super(userName, email, phoneNumber, gender, address, role, userCredential);
        super.setUserType(UserType.ADMIN);
        assignment = new HashSet<>(20);
    }

    public Set<Assignment> getAssignment() {
        return assignment;
    }

    public void setAssignment(Set<Assignment> assignment) {
        this.assignment.addAll(assignment);
    }

    public void addAssignment(Assignment assignment) {
        this.assignment.add(assignment);
        assignment.setAdminAssigned(this);
    }

    public void removeAssignment(Assignment assignment) {
        assignment.setAdminAssigned(null);
        this.assignment.remove(assignment);
    }


}
