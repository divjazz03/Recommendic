package com.divjazz.recommendic.appointment.repository;

import com.divjazz.recommendic.appointment.domain.RecurrenceRule;
import com.divjazz.recommendic.appointment.controller.payload.ScheduleDisplay;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduleCustomRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;


    public Set<ScheduleDisplay> findAllScheduleDisplaysByConsultantId(long consultantId, Pageable pageable) {
        var query = """
                SELECT
                id,
                name,
                start_time,
                end_time,
                utf_offset,
                consultation_channel,
                is_recurring,
                recurrence_rule,
                is_active,
                name,
                created_at,
                (    SELECT count(*)
                     from appointment
                     where schedule_slot_id = schedule_slot.id and appointment.status = 'CONFIRMED'
                ) as upcoming_sessions
                FROM schedule_slot
                WHERE consultant_id = :id
                LIMIT :limit
                 """;
        RowMapper<ScheduleDisplay> rowMapper = (rs, rowNum) -> {

            try {
                var recurrenceRuleString = rs.getString("recurrence_rule");
                return new ScheduleDisplay(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getTime("start_time").toString(),
                        rs.getTime("end_time").toString(),
                        rs.getString("utf_offset"),
                        Arrays.stream(((String[]) rs.getArray("consultation_channel").getArray())).collect(Collectors.toSet()),
                        recurrenceRuleString != null ? objectMapper.readValue(rs.getString("recurrence_rule"), RecurrenceRule.class) : null,
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at").toString(),
                        rs.getInt("upcoming_sessions")


                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        };

        return jdbcClient.sql(query)
                .param("id", consultantId)
                .param("limit", pageable.getPageSize())
                .query(rowMapper)
                .set();
    }
}
