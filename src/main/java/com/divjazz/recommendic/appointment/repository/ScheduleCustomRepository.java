package com.divjazz.recommendic.appointment.repository;

import com.divjazz.recommendic.appointment.domain.RecurrenceRule;
import com.divjazz.recommendic.appointment.controller.payload.ScheduleDisplay;
import com.divjazz.recommendic.global.general.ResponseWithCount;
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


    public ResponseWithCount<ScheduleDisplay> findAllScheduleDisplaysByConsultantId(long consultantId, Pageable pageable) {
        var countQuery = """
                SELECT
                    count(*)
                FROM schedule_slot
                WHERE consultant_id = :id
                """;

        var total = jdbcClient.sql(countQuery)
                .param("id", consultantId)
                .query((rs, rowNum) -> rs.getInt(1))
                .single();

        var query = """
                SELECT
                schedule_id,
                name,
                start_time,
                end_time,
                utf_offset,
                consultation_channel,
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
                OFFSET ((:page + 1) * :limit) - :limit
                """;
        RowMapper<ScheduleDisplay> rowMapper = (rs, rowNum) -> {

            try {
                var recurrenceRuleString = rs.getString("recurrence_rule");
                return new ScheduleDisplay(
                        rs.getString("schedule_id"),
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

        var schedules =  jdbcClient.sql(query)
                .param("id", consultantId)
                .param("limit", pageable.getPageSize())
                .param("page", pageable.getPageNumber())
                .query(rowMapper)
                .set();

        return new ResponseWithCount<>(schedules, total);
    }
}
