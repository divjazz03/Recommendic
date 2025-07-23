package com.divjazz.recommendic.consultation;

import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.consultation.exception.ConsultationAlreadyStartedException;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.repository.ConsultationRepository;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.service.PatientService;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsultationTest {

    private static final Faker faker = new Faker();

    @Mock
    private AppointmentService appointmentService;
    @Mock
    private ConsultationRepository consultationRepository;
    @InjectMocks
    private ConsultationService consultationService;

    private Consultant consultant;
    private Patient patient;

    @BeforeEach
    void setup() {
        consultant = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );
        patient = new Patient(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );
        consultant.getUserPrincipal().setEnabled(true);
        consultant.setMedicalCategory(MedicalCategoryEnum.CARDIOLOGY);
        consultant.setUserStage(UserStage.ACTIVE_USER);
        ConsultantProfile consultantProfile = ConsultantProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .locationOfInstitution(faker.location().work())
                .title(faker.job().title())
                .consultant(consultant)
                .build();
        consultant.setProfile(consultantProfile);

        patient.getUserPrincipal().setEnabled(true);
        patient.setMedicalCategories(new String[]{});
        patient.setUserStage(UserStage.ACTIVE_USER);

        PatientProfile patientProfile = PatientProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .patient(patient)
                .build();
        patient.setPatientProfile(patientProfile);
    }

    @Test
    void givenAppointmentIdNotExistShouldThrowEntityNotFoundException() {
        given(appointmentService.getAppointmentById(anyLong())).willThrow(EntityNotFoundException.class);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> consultationService.startConsultation(2L));
    }

    @Test
    void givenAppointmentExistsAndConsultationWithThatAppointmentAlreadyExists() {
        var appointmentToReturn = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .status(AppointmentStatus.CONFIRMED)
                .consultationChannel(ConsultationChannel.VOICE)
                .build();
        given(appointmentService.getAppointmentById(anyLong())).willReturn(appointmentToReturn);
        given(consultationRepository.existsByAppointmentId(anyLong())).willReturn(true);

        assertThatExceptionOfType(ConsultationAlreadyStartedException.class)
                .isThrownBy(() -> consultationService.startConsultation(2L));
    }

    @Test
    void givenValidAppointmentIdShouldStartTheConsultation() {
        var appointmentToReturn = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .status(AppointmentStatus.CONFIRMED)
                .consultationChannel(ConsultationChannel.VOICE)
                .build();
        var consultationToReturn = Consultation.builder()
                .channel(appointmentToReturn.getConsultationChannel())
                .consultationStatus(ConsultationStatus.ONGOING)
                .appointment(appointmentToReturn)
                .startedAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now())
                .summary("")
                .build();
        given(appointmentService.getAppointmentById(anyLong())).willReturn(appointmentToReturn);
        given(consultationRepository.existsByAppointmentId(anyLong())).willReturn(false);
        given(consultationRepository.save(any(Consultation.class))).willReturn(consultationToReturn);
        var result = consultationService.startConsultation(2L);

        assertThat(result.status()).isEqualTo(ConsultationStatus.ONGOING.toString());
        assertThat(result.channel()).isEqualTo(appointmentToReturn.getConsultationChannel().toString());
    }


}
