package com.divjazz.recommendic.consultation.service;

import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.enums.Status;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.repository.ConsultationRepository;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.PatientService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final ConsultantService consultantService;
    private final PatientService patientService;

    public ConsultationService(ConsultationRepository consultationRepository,
                               ConsultantService consultantService,
                               PatientService patientService) {
        this.consultationRepository = consultationRepository;
        this.consultantService = consultantService;
        this.patientService = patientService;
    }

    public ConsultationResponse initializeConsultation(String consultantId, User currentUser) {
        var patient = patientService.findPatientByUserId(currentUser.getUserId());
        var consultant = consultantService.retrieveConsultantByUserId(consultantId);
        var consultationId = UUID.randomUUID().toString();
        var consultation = new Consultation("",
                consultationId, LocalDateTime.now(),
                patient,
                consultant
                );
        consultationRepository.save(consultation);
        return new ConsultationResponse("", LocalDateTime.now().toString(), patient.getUserName().getFullName(), consultant.getUserName().getFullName(), Status.NOT_STARTED.toString(),consultation.getConsultationId(), false);
    }

    public ConsultationResponse acknowledgeConsultation(String consultationId) {
        var consultation = retrieveConsultationByConsultationId(consultationId);
        consultation.setAccepted(true);
        consultation.setStatus(Status.ONGOING);
        consultationRepository.save(consultation);

        return new ConsultationResponse("",
                LocalDateTime.now().toString(),
                consultation.getPatient().getUserName().getFullName(),
                consultation.getConsultant().getUserName().getFullName(),
                consultation.getStatus().toString(),
                consultation.getConsultationId(),
                consultation.isAccepted());
    }

    public Optional<Consultation> retrieveConsultationByPatientAndConsultant(String patientId, String consultantId) throws UserNotFoundException {
        var patient = patientService.findPatientByUserId(patientId);
        var consultant = consultantService.retrieveConsultantByUserId(consultantId);
        return consultationRepository.findByPatientAndConsultant(patient, consultant);

    }

    public Set<Consultation> retrieveConsultationsByUserId(String userId){
        return consultationRepository.getAllConsultationsWhichContainsTheUserId(userId);
    }

    public Consultation retrieveConsultationByConsultationId(String consultationId) {
        return consultationRepository.getConsultationByConsultationId(consultationId).orElseThrow(() -> new RuntimeException("Not found"));
    }
}
