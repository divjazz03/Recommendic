package com.divjazz.recommendic.search;

import com.divjazz.recommendic.search.searchAttributes.SearchId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SearchRepository extends JpaRepository<Search, SearchId> {
}
