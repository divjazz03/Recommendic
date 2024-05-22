package com.divjazz.recommendic.search;

import com.divjazz.recommendic.search.searchAttributes.SearchId;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.util.List;

@Entity
public class Search extends AbstractEntity<SearchId> {

    private String query;

    @ManyToOne
    private Patient ownerOfSearch;

    protected Search(){
    }
    public Search(SearchId searchId, String query){
        super(searchId);
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
