package com.divjazz.recommendic.search.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.search.enums.Category;
import com.divjazz.recommendic.user.model.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

@Entity
@Table(name = "search")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Search extends Auditable {

    @Column(name = "query")
    private String query;
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private User ownerOfSearch;

    protected Search() {
    }

    public Search(String query, Category category, User ownerOfSearch) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public Category getCategory() {
        return category;
    }

    public User getOwnerOfSearch() {
        return ownerOfSearch;
    }

    public void setOwnerOfSearch(User ownerOfSearch) {
        this.ownerOfSearch = ownerOfSearch;
    }
}
