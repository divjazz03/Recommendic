package com.divjazz.recommendic.search.repository;

import com.divjazz.recommendic.search.model.Search;
import com.divjazz.recommendic.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface SearchRepository extends JpaRepository<Search, UUID> {
    Set<Search> findByOwnerOfSearchId(String id);


}
