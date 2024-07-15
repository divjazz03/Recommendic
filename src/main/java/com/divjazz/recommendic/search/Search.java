package com.divjazz.recommendic.search;

import com.divjazz.recommendic.user.model.Patient;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.UUID;

@Entity
public class Search {

    @Id
    private UUID searchId;

    private String query;

    @ManyToOne
    private Patient ownerOfSearch;

    protected Search(){
    }
    public Search(UUID searchId, String query){
        this.searchId = searchId;
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setOwnerOfSearch(Patient ownerOfSearch) {
        this.ownerOfSearch = ownerOfSearch;
    }

    public Patient getOwnerOfSearch() {
        return ownerOfSearch;
    }
}
