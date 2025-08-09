package aiku_main.repository.schedule;

import common.domain.ExecStatus;
import common.domain.schedule.Schedule;
import common.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {

    Optional<Schedule> findByIdAndStatus(Long scheduleId, Status status);
    List<Schedule> findByScheduleStatus(ExecStatus scheduleStatus);

    boolean existsByIdAndStatus(Long scheduleId, Status status);
    boolean existsByIdAndScheduleStatusAndStatus(Long scheduleId, ExecStatus scheduleStatus, Status status);

}
