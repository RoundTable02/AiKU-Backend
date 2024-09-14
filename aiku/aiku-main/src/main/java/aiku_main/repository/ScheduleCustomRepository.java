package aiku_main.repository;

import org.springframework.data.repository.query.Param;

public interface ScheduleCustomRepository {

    boolean isScheduleOwner(Long memberId, Long scheduleId);
    boolean hasMemberRunScheduleInTeam(Long mebmerId, Long teamId);
}
