package com.divjazz.recommendic.medication;

import com.divjazz.recommendic.medication.constants.DurationType;
import com.divjazz.recommendic.medication.controller.payload.ConsultantPrescriptionResponse;
import com.divjazz.recommendic.medication.controller.payload.MedicationRequest;
import com.divjazz.recommendic.medication.controller.payload.PrescriptionRequest;
import com.divjazz.recommendic.medication.controller.payload.PatientPrescriptionResponse;
import com.divjazz.recommendic.medication.model.Medication;
import com.divjazz.recommendic.medication.model.Prescription;
import com.divjazz.recommendic.medication.repository.PrescriptionRepository;
import com.divjazz.recommendic.medication.service.PrescriptionService;
import com.divjazz.recommendic.medication.utils.PrescriptionUtils;
import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.service.PatientService;
import net.datafaker.Faker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PrescriptionTest {

    public static final Faker FAKER = new Faker();
    private static final String authenticatedPatientId = "%s-%s".formatted("PT",UUID.randomUUID().toString());
    private static final String authenticatedConsultantId = "%s-%s".formatted("CST",UUID.randomUUID().toString());
    private static final UserDTO authenticatedPatient = new UserDTO(
            1L,
            authenticatedPatientId,
            Gender.FEMALE,
            LocalDateTime.now(),
            UserType.PATIENT,
            UserStage.ACTIVE_USER,
            new UserPrincipal("patient@email.test",
                    new UserCredential("passwprd"),
                    new Role("PATIENT", "ROLE_PATIENT"))
    );
    private static final UserDTO authenticatedConsultant = new UserDTO(
            2L,
            authenticatedConsultantId,
            Gender.MALE,
            LocalDateTime.now(),
            UserType.CONSULTANT,
            UserStage.ACTIVE_USER,
            new UserPrincipal("consultant@email.test",
                    new UserCredential("password"),
                    new Role("CONSULTANT", "ROLE_CONSULTANT"))
    );
    @Mock
    private AuthUtils authUtils;
    @Mock
    private PatientService patientService;
    @Mock
    private PrescriptionRepository prescriptionRepository;
    @InjectMocks
    private PrescriptionService prescriptionService;

    @Captor
    private ArgumentCaptor<Prescription> prescriptionArgumentCaptor;

    private static Stream<Arguments> getValidPrescriptionRequestsByPatient() {
        var patientPrescribedMedications = Set.of(new MedicationRequest(
                "Paracetamol",
                "2 tablets",
                "Thrice Daily",
                1,
                "WEEK",
                "Take after meal"
        ));
        var validPatientPrescription = new PrescriptionRequest(
                null,
                authenticatedPatientId,
                "Headache",
                patientPrescribedMedications,
                "Please make sure to take them after meals"
        );

        return Stream.of(Arguments.arguments(
                validPatientPrescription

        ));
    }

    private static Stream<Arguments> getValidPrescriptionRequestsByConsultant() {
        var consultantPrescribedMedications = Set.of(new MedicationRequest(
                "Paracetamol",
                "2 tablets",
                "Thrice Daily",
                1,
                "WEEK",
                "Take after meal. Take at least 8 hours apart"
        ));
        var validConsultantPrescription = new PrescriptionRequest(
                "950j9405j4j54hj4950954",
                "PT-some-patient-id",
                "Headache",
                consultantPrescribedMedications,
                "Please make sure to take them after meals"
        );

        return Stream.of(Arguments.arguments(
                validConsultantPrescription

        ));
    }

    @ParameterizedTest
    @MethodSource("getValidPrescriptionRequestsByConsultant")
    void shouldCreatePrescriptionWhenPrescribedByConsultant(PrescriptionRequest prescriptionRequest) {
        Patient patient = new Patient("patient@email.test",
                Gender.FEMALE,
                null,
                null);
        patient.setUserId(prescriptionRequest.prescribedTo());
        given(authUtils.getCurrentUser()).willReturn(authenticatedConsultant);
        given(patientService.findPatientByUserId(anyString())).willReturn(patient);
        given(prescriptionRepository.save(any(Prescription.class))).willReturn(
                Prescription.builder()
                        .prescriptionId("%s-%s".formatted("PRX", UUID.randomUUID()))
                        .selfReported(true)
                        .prescriberId(authenticatedConsultantId)
                        .prescribedTo(patient)
                        .diagnosis(prescriptionRequest.diagnosis())
                        .medications(prescriptionRequest.medications()
                                .stream().map(medicationRequest -> Medication.builder()
                                        .medicationId("%s-%s".formatted("MDC", UUID.randomUUID()))
                                        .name(medicationRequest.name())
                                        .dosage(medicationRequest.dosage())
                                        .frequency(medicationRequest.medicationFrequency())
                                        .startDate(LocalDate.now())
                                        .endDate(PrescriptionUtils.getEndDate(LocalDate.now(),
                                                medicationRequest.durationValue(),
                                                DurationType.valueOf(medicationRequest.durationType())))
                                        .instructions(medicationRequest.instructions())
                                        .build()).collect(Collectors.toSet()))
                        .build()
        );

        ConsultantPrescriptionResponse response = (ConsultantPrescriptionResponse) prescriptionService.createPrescription(prescriptionRequest);

        then(prescriptionRepository).should(times(1))
                .save(prescriptionArgumentCaptor.capture());

        Prescription prescription = prescriptionArgumentCaptor.getValue();
        assertThat(prescription.isSelfReported()).isFalse();
        assertThat(response.medications()).hasSizeGreaterThan(0);
        assertThat(response.prescriberId()).isEqualTo(authenticatedConsultantId);

    }

    @ParameterizedTest
    @MethodSource("getValidPrescriptionRequestsByPatient")
    void shouldCreatePrescriptionWhenSelfReported(PrescriptionRequest prescriptionRequest) {
        Patient patient = new Patient(authenticatedPatient.userPrincipal().getEmail(),
                authenticatedPatient.gender(),
                authenticatedPatient.userPrincipal().getUserCredential(),
                authenticatedPatient.userPrincipal().getRole());

        given(authUtils.getCurrentUser()).willReturn(authenticatedPatient);
        given(patientService.findPatientByUserId(authenticatedPatient.userId())).willReturn(patient);
        given(prescriptionRepository.save(any(Prescription.class))).willReturn(
                Prescription.builder()
                        .prescriptionId("%s-%s".formatted("PRX-", UUID.randomUUID()))
                        .selfReported(true)
                        .prescriberId(authenticatedPatientId)
                        .prescribedTo(patient)
                        .diagnosis(prescriptionRequest.diagnosis())
                        .medications(prescriptionRequest.medications()
                                .stream().map(medicationRequest -> Medication.builder()
                                        .medicationId("%s-%s".formatted("MDC", UUID.randomUUID()))
                                        .name(medicationRequest.name())
                                        .dosage(medicationRequest.dosage())
                                        .frequency(medicationRequest.medicationFrequency())
                                        .startDate(LocalDate.now())
                                        .endDate(PrescriptionUtils.getEndDate(LocalDate.now(), medicationRequest.durationValue(), DurationType.valueOf(medicationRequest.durationType())))
                                        .instructions(medicationRequest.instructions())
                                        .build()).collect(Collectors.toSet()))
                        .build()
        );

        PatientPrescriptionResponse response = (PatientPrescriptionResponse) prescriptionService.createPrescription(prescriptionRequest);

        then(prescriptionRepository).should(times(1))
                .save(prescriptionArgumentCaptor.capture());

        Prescription prescription = prescriptionArgumentCaptor.getValue();
        assertThat(prescription.isSelfReported()).isTrue();
        assertThat(response.medications()).hasSizeGreaterThan(0);
        assertThat(response.prescriberId()).isEqualTo(authenticatedPatientId);

    }

    @Test
    void shouldGetPrescriptionIfCurrentUserIsPatient() {
        Patient patient = new Patient("patient@email.test",
                Gender.FEMALE,
                null,
                null);
        given(authUtils.getCurrentUser()).willReturn(authenticatedPatient);
        given(prescriptionRepository.findAllByPrescribedTo_UserId(anyString())).willReturn(Set.of(Prescription.builder()
                .prescriptionId("%s-%s".formatted("PRX", UUID.randomUUID()))
                .selfReported(true)
                .prescriberId(authenticatedConsultantId)
                .prescribedTo(patient)
                .diagnosis("Diagnosis")
                .medications(Set.of(Medication.builder()
                                .medicationId("%s-%s".formatted("MDC", UUID.randomUUID()))
                                .name("Paracetamol")
                                .dosage("2 tablets")
                                .frequency("Twice daily")
                                .startDate(LocalDate.now())
                                .endDate(PrescriptionUtils.getEndDate(LocalDate.now(), 1, DurationType.WEEK))
                                .instructions("Take after meal")
                                .build()))
                .build()));

        var result = prescriptionService.getPrescriptions();
        assertThat(result).hasSizeGreaterThan(0);

        then(prescriptionRepository).should(times(1)).findAllByPrescribedTo_UserId(anyString());

    }
    @Test
    void shouldGetPrescriptionIfCurrentUserIsConsultant() {
        Patient patient = new Patient("patient@email.test",
                Gender.FEMALE,
                null,
                null);
        given(authUtils.getCurrentUser()).willReturn(authenticatedConsultant);
        given(prescriptionRepository.findAllByPrescriberId(anyString())).willReturn(Set.of(Prescription.builder()
                .prescriptionId("%s-%s".formatted("PRX", UUID.randomUUID()))
                .selfReported(true)
                .prescriberId(authenticatedConsultantId)
                .prescribedTo(patient)
                .diagnosis("Diagnosis")
                .medications(Set.of(Medication.builder()
                                .medicationId("%s-%s".formatted("MDC", UUID.randomUUID()))
                                .name("Paracetamol")
                                .dosage("2 tablets")
                                .frequency("Twice daily")
                                .startDate(LocalDate.now())
                                .endDate(PrescriptionUtils.getEndDate(LocalDate.now(), 1, DurationType.WEEK))
                                .instructions("Take after meal")
                                .build()))
                .build()));

        var result = prescriptionService.getPrescriptions();
        assertThat(result).hasSizeGreaterThan(0);

        then(prescriptionRepository).should(times(1)).findAllByPrescriberId(anyString());

    }
    @Test
    void shouldReturnEmptySetIfNoPrescriptionsFound() {
        given(authUtils.getCurrentUser()).willReturn(authenticatedPatient);
        given(prescriptionRepository.findAllByPrescribedTo_UserId(anyString())).willReturn(Set.of());

        var result = prescriptionService.getPrescriptions();

        assertThat(result).isEmpty();
    }







}