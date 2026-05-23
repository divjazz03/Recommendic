package com.divjazz.recommendic.appointment.mapper;

import com.divjazz.recommendic.appointment.dto.ScheduleResponseDTO;
import com.divjazz.recommendic.appointment.model.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ScheduleMapper {
    @Mapping(target = "id", source = "scheduleId")
    @Mapping(target = "offset", source = "zoneOffset.id")
    @Mapping(source = "consultationChannels", target = "channels", qualifiedByName = "channelArrToStringSet")
    @Mapping(target = "isActive", source = "active")
    ScheduleResponseDTO toScheduleResponseDTO(Schedule schedule);

    @Named("channelArrToStringSet")
    static Set<String> fromConsultationChannels(String[] consultationChannels) {
        return Arrays.stream(consultationChannels)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }
}
