package com.divjazz.recommendic.appointment.repository;

import com.divjazz.recommendic.appointment.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Transactional(readOnly = true)
    Set<Schedule> findAllByConsultant_UserId(String consultantId);

    Optional<Schedule> findByScheduleId(String scheduleId);

    @Query("""
            SELECT s.consultant.userId
                FROM Schedule s
                JOIN s.consultant
                WHERE s.scheduleId = :scheduleId
            """)
    Optional<String> getScheduleForDeletionReturningConsultantId(String scheduleId);

    @Modifying
    void deleteScheduleByScheduleId(String scheduleId);

    @Query(value = """
            SELECT count(*) > 0
            FROM schedule_slot
            WHERE (
                    (start_time <= end_time and cast(:startTime as time) between start_time and end_time)
                  OR
                    (start_time > end_time AND (cast(:startTime as time) >= start_time) OR cast(:startTime as time) <= end_time )
                  OR
                    (start_time <= end_time and cast(:endTime as time) between start_time and end_time)
                  OR
                    (start_time > end_time AND (cast(:endTime as time) >= start_time) OR cast(:endTime as time) <= end_time )
                )
                AND recurrence_rule ->> 'frequency' = :recurrenceFrequency
            
            """, nativeQuery = true)
    boolean existsByStartTimeAndEndTimeAndRecurrenceRule_Frequency(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("recurrenceFrequency") String recurrenceRuleFrequency);



    @Query(value = """
            SELECT count(*) > 0
                FROM schedule_slot
                WHERE (
                    (start_time <= end_time and cast(:startTime as time) between start_time and end_time)
                  OR
                    (start_time > end_time AND (cast(:startTime as time) >= start_time) OR cast(:startTime as time) <= end_time )
                  OR
                    (start_time <= end_time and cast(:endTime as time) between start_time and end_time)
                  OR
                    (start_time > end_time AND (cast(:endTime as time) >= start_time) OR cast(:endTime as time) <= end_time )
                )
                AND recurrence_rule -> 'weekDays' @> cast(:weekdays as jsonb)
            """, nativeQuery = true)
    boolean existsByWeekDaysAndStartTimeAndEndTime(@Param("weekdays") String weekdays, @Param("startTime") String startTime,@Param("endTime") String endTime);
}
