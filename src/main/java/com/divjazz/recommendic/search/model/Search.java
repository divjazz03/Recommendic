package com.divjazz.recommendic.search.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.search.enums.Category;
import com.divjazz.recommendic.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "search")
@Getter
@Setter
public class Search extends Auditable {

    @Column(name = "query")
    private String query;
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Column(name = "owner_id")
    private String ownerOfSearchId;

    protected Search() {
    }

    public Search(String query, Category category, User ownerOfSearch) {
        this.query = query;
        this.ownerOfSearchId = ownerOfSearch.getUserId();
        this.category = category;
    }
}
