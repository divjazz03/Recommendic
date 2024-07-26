package com.divjazz.recommendic.search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SearchRepository extends JpaRepository<Search, UUID> {
}
