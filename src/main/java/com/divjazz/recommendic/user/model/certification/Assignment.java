package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.Set;
@Entity
public class Assignment extends AbstractEntity<UserId> {

    @OneToMany(targetEntity = Certification.class)
    private Set<Certification> resumes;

    @ManyToOne
    private Admin adminAssigned ;

    protected Assignment(){}

    public Assignment(UserId id, Set<Certification> resumes, Admin adminAssigned) {
        super(id);
        this.resumes = resumes;
        this.adminAssigned = adminAssigned;
    }
}
