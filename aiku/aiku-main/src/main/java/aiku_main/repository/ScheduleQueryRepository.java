package aiku_main.repository;

import common.domain.ExecStatus;
import common.domain.schedule.Schedule;
import common.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleQueryRepository extends JpaRepository<Schedule, Long>, ScheduleQueryRepositoryCustom {

    Optional<Schedule> findByIdAndStatus(Long scheduleId, Status status);
    List<Schedule> findByScheduleStatus(ExecStatus scheduleStatus);
    
    boolean existsByIdAndStatus(Long scheduleId, Status status);
    boolean existsByIdAndIsAutoClose(Long scheduleId, boolean isAutoClose);
    boolean existsByIdAndScheduleStatusAndStatus(Long scheduleId, ExecStatus scheduleStatus, Status status);
}
