package aiku_main.repository;

import common.domain.ExecStatus;
import common.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleCustomRepository {

    List<Schedule> findByScheduleStatus(ExecStatus scheduleStatus);
}
