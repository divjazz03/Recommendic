package com.divjazz.recommendic.appointment.service;

import com.divjazz.recommendic.appointment.dto.AppointmentCreationRequest;
import com.divjazz.recommendic.appointment.dto.AppointmentDTO;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.exception.AppointmentNotFoundException;
import com.divjazz.recommendic.appointment.exception.ScheduleNotAvailableException;
import com.divjazz.recommendic.appointment.mapper.AppointmentMapper;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.ScheduleSlot;
import com.divjazz.recommendic.appointment.repository.AppointmentRepository;
import com.divjazz.recommendic.appointment.repository.ScheduleSlotRepository;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final ConsultantRepository consultantRepository;
    private final PatientRepository patientRepository;
    private final AuthUtils authUtils;

    public Appointment getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(AppointmentNotFoundException::new);
    }

    @Transactional
    public AppointmentDTO createAppointment(AppointmentCreationRequest appointmentCreationRequest) {
        Stream<ScheduleSlot> scheduleSlotStream = scheduleSlotRepository
                .findAllByConsultant_UserId(appointmentCreationRequest.consultantId());
        Consultant consultant  = consultantRepository
                .findByUserId(appointmentCreationRequest.consultantId())
                .orElseThrow(UserNotFoundException::new);
        Patient patient = patientRepository.findByUserId(authUtils.getCurrentUser().getUserId())
                .orElseThrow(UserNotFoundException::new);

        ScheduleSlot foundScheduleSlot = scheduleSlotStream
                .filter( scheduleSlot -> {
                            var startTime = scheduleSlot.getStartTime();
                            var endTime = scheduleSlot.getEndTime();

                            return startTime
                                    .isEqual(ZonedDateTime.parse(appointmentCreationRequest.startTime(),
                                            DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                                    &&
                                    endTime
                                            .isEqual(ZonedDateTime.parse(appointmentCreationRequest.endTime(),
                                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME));

                        })
                .filter(scheduleSlot -> !scheduleSlot.isBooked())
                .findAny().orElseThrow(() -> new ScheduleNotAvailableException(appointmentCreationRequest.startTime()
                        , appointmentCreationRequest.endTime()));
        Appointment appointment = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .scheduleSlot(foundScheduleSlot)
                .status(AppointmentStatus.REQUESTED)
                .build();

        appointment = appointmentRepository.save(appointment);

        return AppointmentMapper.appointmentToDTO(appointment);
    }

    @Transactional(readOnly = true)
    public Stream<Appointment> getAppointmentsByPatientId(String patientId) {
        return appointmentRepository.findAppointmentsByPatient_UserId(patientId);
    }

    @Transactional(readOnly = true)
    public Stream<Appointment> getAppointmentsByConsultantId(String consultantId) {
        return appointmentRepository.findAppointmentsByConsultant_UserId(consultantId);
    }
}
