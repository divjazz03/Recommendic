package com.divjazz.recommendic.medication.repository;

import com.divjazz.recommendic.medication.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Set<Prescription> findAllByPrescribedTo_UserId(String userId);
    Set<Prescription> findAllByPrescriberId(String prescriberId);
    Optional<Prescription> findPrescriptionByPrescriptionId(String prescriptionId);

    @Query("""
    SELECT p
        FROM Prescription p
        JOIN FETCH p.medications m
        WHERE :date >= m.startDate AND :date <= m.endDate
    """)
    Set<Prescription> findPrescriptionsCoinciding(LocalDate date);
}
