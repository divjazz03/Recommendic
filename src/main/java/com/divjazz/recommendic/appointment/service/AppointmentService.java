package com.divjazz.recommendic.appointment.service;

import com.divjazz.recommendic.appointment.dto.AppointmentCreationRequest;
import com.divjazz.recommendic.appointment.dto.AppointmentDTO;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.exception.AppointmentBookedException;
import com.divjazz.recommendic.appointment.mapper.AppointmentMapper;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.AppointmentCustomRepository;
import com.divjazz.recommendic.appointment.repository.AppointmentRepository;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import com.divjazz.recommendic.appointment.repository.projection.AppointmentProjection;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.repository.ConsultantProfileRepository;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.PatientProfileRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentCustomRepository appointmentCustomRepository;
    private final ScheduleRepository scheduleRepository;
    private final ConsultantRepository consultantRepository;
    private final PatientRepository patientRepository;
    private final ConsultantProfileRepository consultantProfileRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final AuthUtils authUtils;

    public Appointment getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment with id: %s not found".formatted(appointmentId)));
    }

    @Transactional
    public AppointmentDTO createAppointment(AppointmentCreationRequest appointmentCreationRequest) {
        Stream<Appointment> appointmentStream = getAppointmentsByConsultantId(appointmentCreationRequest.consultantId());
        Schedule schedule = scheduleRepository.findById(appointmentCreationRequest.scheduleId())
                .orElseThrow(() -> new EntityNotFoundException("ScheduleRecurrence was not found"));
        Consultant consultant  = consultantRepository
                .findByUserId(appointmentCreationRequest.consultantId())
                .orElseThrow(() -> new EntityNotFoundException("Consultant with id: %s does not exist"
                        .formatted(appointmentCreationRequest.consultantId())));
        Patient patient = patientRepository.findByUserId(authUtils.getCurrentUser().getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Patient with id: %s does not exist"
                        .formatted(appointmentCreationRequest.consultantId())));
        ConsultantProfile consultantProfile = consultantProfileRepository.findById(consultant.getId())
                .orElseThrow(() -> new EntityNotFoundException("Consultant doesn't have a profile"));
        PatientProfile patientProfile = patientProfileRepository.findById(patient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient doesn't have a profile"));

        boolean appointmentIsOccupiedBooked = appointmentStream
                .anyMatch(appointment -> {
                            var appointmentDate = appointment.getAppointmentDate();
                            return schedule.equals(appointment.getSchedule())
                                    &&
                                    appointmentDate.equals(LocalDate.parse(appointmentCreationRequest.appointmentDate(),
                                            DateTimeFormatter.ISO_DATE_TIME));

                        });

        if (appointmentIsOccupiedBooked) {
            throw new AppointmentBookedException("Date: %s with ScheduleTime: %s".formatted(appointmentCreationRequest.appointmentDate(),
                    appointmentCreationRequest.startTime()));
        }
        Appointment appointment = Appointment.builder()
                .consultant(consultant)
                .patient(patient)
                .schedule(schedule)
                .status(AppointmentStatus.REQUESTED)
                .appointmentDate(LocalDate.now())
                .consultationChannel(ConsultationChannel.valueOf(appointmentCreationRequest.channel().toUpperCase()))
                .build();

        appointment = appointmentRepository.save(appointment);

        return AppointmentMapper.appointmentProjectionToDTO(new AppointmentProjection(appointment,patientProfile,consultantProfile));
    }

    @Transactional(readOnly = true)
    public Stream<Appointment> getAppointmentsByPatientId(String patientId) {
        return appointmentRepository.findAppointmentsByPatient_UserId(patientId);
    }

    @Transactional(readOnly = true)
    public Stream<Appointment> getAppointmentsByConsultantId(String consultantId) {
        return appointmentRepository.findAppointmentsByConsultant_UserId(consultantId);
    }

    public Stream<AppointmentProjection> getAppointmentDetailsByConsultantId(String consultantId) {
        return appointmentCustomRepository.findAppointmentDetailsByConsultantUserId(consultantId);
    }
    public Stream<AppointmentProjection> getAppointmentDetailsByPatientId(String patientId) {
        return appointmentCustomRepository.findAppointmentDetailsByConsultantUserId(patientId);
    }
}
