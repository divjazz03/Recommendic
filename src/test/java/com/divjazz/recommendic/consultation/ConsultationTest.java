package com.divjazz.recommendic.consultation;

import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.consultation.exception.ConsultationAlreadyStartedException;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.model.ConsultationSession;
import com.divjazz.recommendic.consultation.repository.ConsultationRepository;
import com.divjazz.recommendic.consultation.repository.ConsultationSessionRepository;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsultationTest {

    private static final Faker faker = new Faker();

    @Mock
    private AppointmentService appointmentService;
    @Mock
    private ConsultationRepository consultationRepository;
    @Mock
    private ConsultationSessionRepository sessionRepository;
    @Mock
    private AuthUtils authUtils;
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
        consultant.setUserId(UUID.randomUUID().toString());

        patient.getUserPrincipal().setEnabled(true);
        patient.addMedicalCategory(new MedicalCategoryEntity(1L, "Cardiology","cardiology", "sdecss", "dsdsds"));
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
                .isThrownBy(() -> consultationService.startConsultation("FSFSFSF",LocalDateTime.now().toString()));
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
        var userDTO = new UserDTO(1,
                consultant.getUserId(),
                Gender.MALE,
                LocalDateTime.now(),
                UserType.CONSULTANT,
                UserStage.ONBOARDING,
                new UserPrincipal("",
                        new UserCredential("password"),
                        new Role("Admin", "")));
        given(authUtils.getCurrentUser()).willReturn(userDTO);

        assertThatExceptionOfType(ConsultationAlreadyStartedException.class)
                .isThrownBy(() -> consultationService.startConsultation("ffjkfjbkfk",LocalDateTime.now().toString()));
    }

    @Test
    void givenValidAppointmentIdShouldStartTheConsultation() {
        var schedule = Schedule.builder()
                .startTime(LocalTime.now())
                .build();
        var appointmentToReturn = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .status(AppointmentStatus.CONFIRMED)
                .consultationChannel(ConsultationChannel.ONLINE)
                .appointmentDate(LocalDate.now())
                .schedule(schedule)
                .build();
        var consultationToReturn = Consultation.builder()
                .channel(appointmentToReturn.getConsultationChannel())
                .consultationStatus(ConsultationStatus.ONGOING)
                .appointment(appointmentToReturn)
                .startedAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now())
                .summary("")
                .build();
        var userDTO = new UserDTO(1,
                consultant.getUserId(),
                Gender.MALE,
                LocalDateTime.now(),
                UserType.CONSULTANT,
                UserStage.ONBOARDING,
                new UserPrincipal("",
                        new UserCredential("password"),
                        new Role("Admin", "")));
        given(authUtils.getCurrentUser()).willReturn(userDTO);
        given(appointmentService.getAppointmentByAppointmentId(anyString())).willReturn(appointmentToReturn);
        given(consultationRepository.existsByAppointment_AppointmentId(anyString())).willReturn(false);
        given(consultationRepository.save(any(Consultation.class))).willReturn(consultationToReturn);
        given(sessionRepository.save(any(ConsultationSession.class))).willReturn(any());
        var result = consultationService.startConsultation("fkdnldkfnldkf",LocalDateTime.now().toString());

        assertThat(result.status()).isEqualTo(ConsultationStatus.ONGOING.toString());
        assertThat(result.channel()).isEqualTo(appointmentToReturn.getConsultationChannel().toString());
    }


}
