package aiku_main.repository;

import common.domain.Schedule;

import java.util.List;

public interface ScheduleRepositoryCustom {

    List<Schedule> findMemberScheduleInTeamWithMember(Long memberId, Long teamId);

    boolean isScheduleOwner(Long memberId, Long scheduleId);
    boolean hasMemberRunScheduleInTeam(Long mebmerId, Long teamId);
}
