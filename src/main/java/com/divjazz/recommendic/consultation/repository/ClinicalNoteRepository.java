package com.divjazz.recommendic.consultation.repository;

import com.divjazz.recommendic.consultation.model.ClinicalNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClinicalNoteRepository extends JpaRepository<ClinicalNote, Long> {

    Optional<ClinicalNote> findByNoteId(String noteId);

    Optional<ClinicalNote> findTop1ByConsultation_ConsultationIdOrderByVersionDesc(String consultationConsultationId);
}
