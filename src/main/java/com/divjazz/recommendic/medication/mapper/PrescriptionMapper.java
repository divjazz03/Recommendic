package com.divjazz.recommendic.medication.mapper;

import com.divjazz.recommendic.medication.controller.payload.ConsultantPrescriptionResponse;
import com.divjazz.recommendic.medication.controller.payload.PatientPrescriptionResponse;
import com.divjazz.recommendic.medication.dto.MedicationDTO;
import com.divjazz.recommendic.medication.model.Prescription;

import java.util.stream.Collectors;

public final class PrescriptionMapper {


    public static class PatientMapper {

        public static PatientPrescriptionResponse prescriptionToResponse(Prescription prescription) {
            return new PatientPrescriptionResponse(
                    prescription.getPrescriptionId(),
                    prescription.getDiagnosis(),
                    prescription.getCreatedAt().toLocalDate().toString(),
                    prescription.getStatus(),
                    prescription.getNotes(),
                    prescription.getMedications().stream().map(medication -> new MedicationDTO(
                            medication.getMedicationId(),
                            medication.getName(),
                            medication.getDosage(),
                            medication.getFrequency(),
                            medication.getStartDate().toString(),
                            medication.getEndDate().toString(),
                            medication.getInstructions()
                    )).collect(Collectors.toSet()),
                    prescription.getPrescriberId(),
                    prescription.isSelfReported()
            );
        }

    }

    public static class ConsultantMapper {
        public static ConsultantPrescriptionResponse prescriptionToResponse(Prescription prescription) {
            return new ConsultantPrescriptionResponse(
                    prescription.getPrescriptionId(),
                    prescription.getConsultation().getAppointment().getPatient().getPatientProfile().getUserName().getFullName(),
                    prescription.getConsultation().getAppointment().getPatient().getPatientProfile().getAge(),
                    prescription.getConsultation().getAppointment().getPatient().getGender().toString().toLowerCase(),
                    prescription.getDiagnosis(),
                    prescription.getMedications().stream().map(medication -> new MedicationDTO(
                            medication.getMedicationId(),
                            medication.getName(),
                            medication.getDosage(),
                            medication.getFrequency(),
                            medication.getStartDate().toString(),
                            medication.getEndDate().toString(),
                            medication.getInstructions()
                    )).collect(Collectors.toSet()),
                    prescription.getPrescriberId(),
                    prescription.getConsultation().getAppointment().getConsultant().getProfile().getUserName().getFullName(),
                    prescription.getCreatedAt().toLocalDate().toString(),
                    prescription.getStatus(),
                    prescription.getNotes()
            );
        }
    }
}
