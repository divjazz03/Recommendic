package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.UserType;
import com.divjazz.recommendic.user.model.userAttributes.*;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;


public class Admin{

    private Set<AdminPassword> password;

    protected Admin() {
    }

}
