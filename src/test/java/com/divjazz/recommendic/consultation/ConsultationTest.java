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
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
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
                new UserCredential(faker.text().text(20)),
                new Role(1L,"ROLE_TEST", "")
        );
        patient = new Patient(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20)),
                new Role(1L,"ROLE_TEST", "")

        );
        consultant.getUserPrincipal().setEnabled(true);
        consultant.setSpecialization(new MedicalCategoryEntity());
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
        patient.addMedicalCategory(new MedicalCategoryEntity(1L, "cardiology", "sdecss"));
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
        given(appointmentService.getAppointmentByAppointmentId(anyString())).willThrow(EntityNotFoundException.class);

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> consultationService.startConsultation("FSFSFSF"));
    }

    @Test
    void givenAppointmentExistsAndConsultationWithThatAppointmentAlreadyExists() {
        var appointmentToReturn = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .status(AppointmentStatus.CONFIRMED)
                .consultationChannel(ConsultationChannel.ONLINE)
                .build();
        given(appointmentService.getAppointmentByAppointmentId(anyString())).willReturn(appointmentToReturn);
        given(consultationRepository.existsByAppointment_AppointmentId(anyString())).willReturn(true);

        assertThatExceptionOfType(ConsultationAlreadyStartedException.class)
                .isThrownBy(() -> consultationService.startConsultation("ffjkfjbkfk"));
    }

    @Test
    void givenValidAppointmentIdShouldStartTheConsultation() {
        var appointmentToReturn = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .status(AppointmentStatus.CONFIRMED)
                .consultationChannel(ConsultationChannel.ONLINE)
                .build();
        var consultationToReturn = Consultation.builder()
                .channel(appointmentToReturn.getConsultationChannel())
                .consultationStatus(ConsultationStatus.ONGOING)
                .appointment(appointmentToReturn)
                .startedAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now())
                .summary("")
                .build();
        given(appointmentService.getAppointmentByAppointmentId(anyString())).willReturn(appointmentToReturn);
        given(consultationRepository.existsByAppointment_AppointmentId(anyString())).willReturn(false);
        given(consultationRepository.save(any(Consultation.class))).willReturn(consultationToReturn);
        var result = consultationService.startConsultation("fkdnldkfnldkf");

        assertThat(result.status()).isEqualTo(ConsultationStatus.ONGOING.toString());
        assertThat(result.channel()).isEqualTo(appointmentToReturn.getConsultationChannel().toString());
    }


}
