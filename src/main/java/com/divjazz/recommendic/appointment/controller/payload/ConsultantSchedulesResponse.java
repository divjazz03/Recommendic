package com.divjazz.recommendic.appointment.controller.payload;

import com.divjazz.recommendic.appointment.dto.ScheduleWithAppointmentDetail;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record ConsultantSchedulesResponse (
    Set<ScheduleWithAppointmentDetail> schedules,
    ScheduleConsultantProfile profile
) {

    public record ScheduleConsultantProfile (
            String fullName,
            String title,
            double rating,
            String image,
            ConsultationFee fee,
            String location
            ) {
        public record ConsultationFee(int inPerson, int online){}
    }
}
