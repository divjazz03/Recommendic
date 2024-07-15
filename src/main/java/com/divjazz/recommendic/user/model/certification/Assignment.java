package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.Admin;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;
@Entity
public class Assignment extends Auditable {

    @OneToMany(mappedBy = "assignment", fetch = FetchType.LAZY)
    private Set<Certification> resumes;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admin adminAssigned ;

    protected Assignment(){}

    public Assignment(Set<Certification> resumes, Admin adminAssigned) {
        this.resumes = resumes;
        this.adminAssigned = adminAssigned;
    }
}
