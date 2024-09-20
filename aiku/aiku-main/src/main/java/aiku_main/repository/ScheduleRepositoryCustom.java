package aiku_main.repository;

import common.domain.Schedule;
import common.domain.ScheduleMember;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepositoryCustom {

    List<Schedule> findMemberScheduleInTeamWithMember(Long memberId, Long teamId);

    boolean isScheduleOwner(Long memberId, Long scheduleId);
    boolean existScheduleMember(Long memberId, Long scheduleId);
    Long countOfAliveScheduleMember(Long scheduleId);
    Optional<ScheduleMember> findAliveScheduleMember(Long memberId, Long scheduleId);
    Optional<ScheduleMember> findNextScheduleOwner(Long scheduleId, Long prevOwnerScheduleMemberId);
}
