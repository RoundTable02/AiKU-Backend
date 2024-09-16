package aiku_main.repository;

import common.domain.ExecStatus;
import common.domain.Schedule;
import common.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {

    List<Schedule> findByScheduleStatus(ExecStatus scheduleStatus);
    boolean existsByScheduleIdAndScheduleStatusAndStatus(Long scheduleId, ExecStatus scheduleStatus, Status status);
}
