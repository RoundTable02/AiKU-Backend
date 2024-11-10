package map.repository;

import common.domain.ExecStatus;
import common.domain.Status;
import common.domain.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleQueryRepository {

    boolean existsByIdAndScheduleStatusAndStatus(Long scheduleId, ExecStatus scheduleStatus, Status status);
}
