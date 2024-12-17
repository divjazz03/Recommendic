package com.divjazz.recommendic.search.model;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private User ownerOfSearch;

    protected Search(){
    }
    public Search(String query, User ownerOfSearch){
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setOwnerOfSearch(User ownerOfSearch) {
        this.ownerOfSearch = ownerOfSearch;
    }

    public User getOwnerOfSearch() {
        return ownerOfSearch;
    }
}
