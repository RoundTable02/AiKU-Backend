package aiku_main.repository;

import common.domain.Schedule;
import common.domain.ScheduleMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT CASE WHEN COUNT(sm) > 0 THEN true ELSE false END " +
            "FROM ScheduleMember sm WHERE sm.member.id = :memberId " +
            "AND sm.schedule.id = :scheduleId AND sm.isOwner = true AND sm.status = 'ALIVE'")
    boolean isScheduleOwner(@Param("memberId") Long memberId, @Param("scheduleId") Long scheduleId);
}
