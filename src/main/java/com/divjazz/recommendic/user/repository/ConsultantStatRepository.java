package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.userAttributes.ConsultantStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultantStatRepository extends JpaRepository<ConsultantStat, Long> {
    @Query(
            """
                    SELECT s FROM ConsultantStat s
                    WHERE s.consultant.userId = :id
                    """
    )
    Optional<ConsultantStat> findConsultantStatByConsultantId(String id);
}
