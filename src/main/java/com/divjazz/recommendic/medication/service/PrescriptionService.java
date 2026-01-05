package com.divjazz.recommendic.medication.service;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.global.exception.AppBadRequestException;
import com.divjazz.recommendic.global.exception.AuthorizationException;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.medication.constants.DurationType;
import com.divjazz.recommendic.medication.controller.payload.PrescriptionRequest;
import com.divjazz.recommendic.medication.controller.payload.PrescriptionResponse;
import com.divjazz.recommendic.medication.mapper.PrescriptionMapper;
import com.divjazz.recommendic.medication.model.Medication;
import com.divjazz.recommendic.medication.model.Prescription;
import com.divjazz.recommendic.medication.repository.PrescriptionRepository;
import com.divjazz.recommendic.medication.utils.PrescriptionUtils;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AuthUtils authUtils;
    private final PatientService patientService;
    private final ConsultationService consultationService;


    public PrescriptionResponse createPrescription(PrescriptionRequest request) {
        var currentUser = authUtils.getCurrentUser();
        var isSelfReported = currentUser.userType() == UserType.PATIENT && request.prescribedTo().equals(currentUser.userId());
        Patient patient = isSelfReported
                ? patientService.findPatientByUserId(currentUser.userId())
                : patientService.findPatientByUserId(request.prescribedTo());
        Consultation consultation = null;
        if (!isSelfReported) {
            consultation = consultationService.getConsultationById(request.consultationId());
            boolean patientIsPatientOfConsultation =  request.prescribedTo().equals(consultation.getAppointment()
                    .getPatient().getUserId());
            boolean currentUserIsConsultantInConsultation = currentUser.userId().equals(consultation.getAppointment()
                    .getConsultant().getUserId());

            if (!patientIsPatientOfConsultation) {
                throw new AppBadRequestException("The prescribee is not valid for the consultation session");
            }
            if (!currentUserIsConsultantInConsultation) {
                throw new AuthorizationException("You are not authorized to prescribe medication for this consultation session");
            }
        }



        var prescription = Prescription.builder()
                .selfReported(isSelfReported)
                .consultation(consultation)
                .prescribedTo(patient)
                .prescriberId(currentUser.userId())
                .diagnosis(request.diagnosis())
                .build();
        var startDate = LocalDate.now();
        Set<Medication> medications = request.medications().stream()
                .map(medicationRequest -> Medication.builder()
                        .name(medicationRequest.name())
                        .dosage(medicationRequest.dosage())
                        .condition(request.diagnosis())
                        .frequency(medicationRequest.medicationFrequency())
                        .consultationDate(startDate)
                        .prescription(prescription)
                        .instructions(medicationRequest.instructions())
                        .startDate(startDate)
                        .endDate(PrescriptionUtils.getEndDate(startDate,
                                medicationRequest.durationValue(),
                                DurationType.valueOf(medicationRequest.durationType())))
                        .build()
                )
                .collect(Collectors.toSet());
        prescription.setMedications(medications);

        var savedPrescription = prescriptionRepository.save(prescription);

        return PrescriptionMapper.prescriptionToResponse(savedPrescription);

    }


    @Transactional(readOnly = true)
    public Set<PrescriptionResponse> getPrescriptions() {
        var currentUser = authUtils.getCurrentUser();

        return switch (currentUser.userType()) {
            case PATIENT -> {
                var prescriptions = prescriptionRepository.findAllByPrescribedTo_UserId(currentUser.userId());
                yield prescriptions.stream()
                        .map(PrescriptionMapper::prescriptionToResponse)
                        .collect(Collectors.toSet());
            }
            case CONSULTANT -> {
                var prescriptions = prescriptionRepository.findAllByPrescriberId(currentUser.userId());
                yield prescriptions.stream()
                        .map(PrescriptionMapper::prescriptionToResponse)
                        .collect(Collectors.toSet());
            }
            case ADMIN -> null;
        };

    }

    @Transactional(readOnly = true)
    public Set<PrescriptionResponse> getTodayPrescription() {
        var prescriptions = prescriptionRepository.findPrescriptionsCoinciding(LocalDate.now());

        return prescriptions.stream()
                .map(PrescriptionMapper::prescriptionToResponse)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(String prescriptionId) {
        return prescriptionRepository
                .findPrescriptionByPrescriptionId(prescriptionId)
                .map(PrescriptionMapper::prescriptionToResponse)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found"));

    }


}
