package com.divjazz.recommendic.search;

import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface SearchRepository extends JpaRepository<Search, UUID> {
    Optional<List<Search>> findByOwnerOfSearch(User patient);
}
