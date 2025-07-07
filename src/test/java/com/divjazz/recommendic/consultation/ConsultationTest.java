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
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
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
                new UserName("firstname", "lastname"),
                "consultant@test.com",
                "234424424242",
                Gender.MALE,
                new Address("bang", "dsadd", "jouuewn"),
                new UserCredential("password")
        );
        patient = new Patient(
                new UserName("firstname", "lastname"),
                "patient@test.com",
                "234424424242",
                Gender.MALE,
                new Address("bang", "dsadd", "jouuewn"),
                new UserCredential("password")
        );
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
        assertThat(result.consultantName()).isEqualTo(appointmentToReturn.getConsultant().getUserNameObject().getFullName());
    }


}
