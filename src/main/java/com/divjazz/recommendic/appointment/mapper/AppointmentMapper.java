package com.divjazz.recommendic.appointment.mapper;

import com.divjazz.recommendic.appointment.dto.AppointmentDTO;
import com.divjazz.recommendic.appointment.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.format.DateTimeFormatter;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface AppointmentMapper {
    @Mapping(target = "patientId", source = "patient.userId")
    @Mapping(target = "consultantId", source = "consultant.userId")

    @Mapping(target = "patientFullName",
            source = "patient.patientProfile.userName.fullName")

    @Mapping(target = "consultantFullName",
            expression = """
                    java(
                        appointment.getConsultant().getProfile().getTitle()
                        + " "
                        + appointment.getConsultant()
                                     .getProfile()
                                     .getUserName()
                                     .getFullName()
                    )
                    """)
    @Mapping(target = "appointmentDate", source = "appointmentDate")
    @Mapping(target = "startDateTime", source = "startDateAndTime")
    @Mapping(target = "endDateTime", source = "endDateAndTime")

    @Mapping(target = "status", source = "status")
    @Mapping(target = "consultationChannel", source = "consultationChannel")
    @Mapping(target = "appointmentHistory", source = "history")
    @Mapping(target = "startTime", source = "schedule.startTime")
    @Mapping(target = "endTime", source = "schedule.endTime")


    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "note", source = "note")

    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")

    AppointmentDTO appointmentToAppointmentDTO (Appointment appointment);

}
