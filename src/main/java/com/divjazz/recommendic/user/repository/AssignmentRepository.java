package com.divjazz.recommendic.user.repository;

import com.divjazz.recommendic.user.model.Assignment;
import com.divjazz.recommendic.user.model.certification.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Set<Assignment> getAllByAdminAssigned_UserId(String userId);

    @Query(value = "select c from certification c join assignment a on a.id = c.assignment_id where confirmed = false",nativeQuery = true)
    Set<Certification> getAllCertificationByAssignmentId(Long id);
}
