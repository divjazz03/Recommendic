package com.divjazz.recommendic.consultation;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.repository.ConsultationRepository;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final ConsultantService consultantService;
    private final PatientService patientService;
    private final GeneralUserService userService;

    public ConsultationService(ConsultationRepository consultationRepository, ConsultantService consultantService, PatientService patientService, GeneralUserService userService) {
        this.consultationRepository = consultationRepository;
        this.consultantService = consultantService;
        this.patientService = patientService;
        this.userService = userService;
    }

    public Optional<Consultation> retrieveConsultationByPatientAndConsultant(String patientId, String consultantId) throws UserNotFoundException {
        var patient = patientService.findPatientByUserId(patientId);
        var consultant = consultantService.retrieveConsultantByUserId(consultantId);
        if (patient.isPresent() && consultant.isPresent()){
            return consultationRepository.findByPatientAndConsultant(patient.get(), consultant.get());
        } else {
            throw new UserNotFoundException();
        }
    }

    public Set<Consultation> retrieveConsultationByUserId(String userId){
        var user = userService.retrieveUserByUserId(userId);

    }
}
