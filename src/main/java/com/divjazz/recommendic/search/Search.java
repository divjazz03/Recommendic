package com.divjazz.recommendic.search;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "search")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Search extends Auditable {

    @Column(name = "query")
    private String query;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private User ownerOfSearch;

    protected Search(){
    }
    public Search(String query){
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setOwnerOfSearch(Patient ownerOfSearch) {
        this.ownerOfSearch = ownerOfSearch;
    }

    public User getOwnerOfSearch() {
        return ownerOfSearch;
    }
}
