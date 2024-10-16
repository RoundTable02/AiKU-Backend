package aiku_main.repository;

import common.domain.ExecStatus;
import common.domain.schedule.Schedule;
import common.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {

    Optional<Schedule> findByIdAndStatus(Long scheduleId, Status status);
    boolean existsByIdAndStatus(Long scheduleId, Status status);
    boolean existsByIdAndIsAutoClose(Long scheduleId, boolean isAutoClose);
    List<Schedule> findByScheduleStatus(ExecStatus scheduleStatus);
    boolean existsByIdAndScheduleStatusAndStatus(Long scheduleId, ExecStatus scheduleStatus, Status status);
}
