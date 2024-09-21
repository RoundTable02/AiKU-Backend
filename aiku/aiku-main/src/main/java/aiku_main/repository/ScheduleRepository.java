package aiku_main.repository;

import common.domain.ExecStatus;
import common.domain.Schedule;
import common.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {

    List<Schedule> findByScheduleStatus(ExecStatus scheduleStatus);
    boolean existsByIdAndScheduleStatusAndStatus(Long scheduleId, ExecStatus scheduleStatus, Status status);
}
