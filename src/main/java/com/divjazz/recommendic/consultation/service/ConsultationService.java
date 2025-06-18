package com.divjazz.recommendic.consultation.service;

import com.divjazz.recommendic.consultation.dto.ConsultationResponse;
import com.divjazz.recommendic.consultation.enums.Status;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.repository.ConsultationRepository;
import com.divjazz.recommendic.general.PageResponse;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.PatientService;
import org.springframework.data.domain.Pageable;
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
    private final AuthUtils authUtils;

    public ConsultationService(ConsultationRepository consultationRepository,
                               ConsultantService consultantService,
                               PatientService patientService, AuthUtils authUtils) {
        this.consultationRepository = consultationRepository;
        this.consultantService = consultantService;
        this.patientService = patientService;
        this.authUtils = authUtils;
    }

    public ConsultationResponse initializeConsultation(String consultantId, User currentUser) {
        var patient = (Patient) currentUser;
        var consultant = consultantService.retrieveConsultantByUserId(consultantId);
        var consultationId = UUID.randomUUID().toString();
        var consultation = new Consultation("",
                consultationId, LocalDateTime.now(),
                patient,
                consultant
        );
        consultationRepository.save(consultation);
        return new ConsultationResponse("", LocalDateTime.now().toString(), patient.getUserNameObject().getFullName(), consultant.getUserNameObject().getFullName(), Status.NOT_STARTED.toString(), consultation.getConsultationId(), false);
    }

    public ConsultationResponse acknowledgeConsultation(String consultationId) {
        var consultation = retrieveConsultationByConsultationId(consultationId);
        consultation.setAccepted(true);
        consultation.setStatus(Status.ONGOING);
        consultationRepository.save(consultation);

        return new ConsultationResponse("",
                LocalDateTime.now().toString(),
                consultation.getPatient().getUserNameObject().getFullName(),
                consultation.getConsultant().getUserNameObject().getFullName(),
                consultation.getStatus().toString(),
                consultation.getConsultationId(),
                consultation.isAccepted());
    }

    public Optional<Consultation> retrieveConsultationByPatientAndConsultant(String patientId, String consultantId) throws UserNotFoundException {
        var patient = patientService.findPatientByUserId(patientId);
        var consultant = consultantService.retrieveConsultantByUserId(consultantId);
        return consultationRepository.findByPatientAndConsultant(patient, consultant);

    }

    public PageResponse<ConsultationResponse> retrieveConsultationsOfConsultant(Pageable pageable) throws UserNotFoundException {
        Consultant consultant = (Consultant) authUtils.getCurrentUser();
        var consultations = consultationRepository.findAllByConsultantIdAndAccepted(consultant.getUserId(),false, pageable);
        return PageResponse.from(consultations.map(consultation ->
                new ConsultationResponse(
                        consultation.getDiagnosis(),
                        consultation.getConsultationTime().toString(),
                        consultation.getPatient().getUserNameObject().getFullName(),
                        consultant.getUserNameObject().getFullName(),
                        String.valueOf(consultation.getId()),
                        consultation.getStatus().name(),
                        consultation.isAccepted()
                        )));

    }
    public Set<Consultation> retrieveConsultationsByUserId(String userId) {
        return consultationRepository.getAllConsultationsWhichContainsTheUserId(userId);
    }

    public Consultation retrieveConsultationByConsultationId(String consultationId) {
        return consultationRepository.getConsultationByConsultationId(consultationId).orElseThrow(() -> new RuntimeException("Not found"));
    }
}
