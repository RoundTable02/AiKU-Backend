package aiku_main.repository;

import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepositoryCustom {

    Optional<Schedule> findScheduleWithNotArriveScheduleMember(Long scheduleId);

    List<Schedule> findMemberScheduleInTeamWithMember(Long memberId, Long teamId);

    boolean isScheduleOwner(Long memberId, Long scheduleId);
    boolean existScheduleMember(Long memberId, Long scheduleId);
    boolean existPaidScheduleMember(Long memberId, Long scheduleId);
    Long countOfAliveScheduleMember(Long scheduleId);
    Optional<ScheduleMember> findAliveScheduleMember(Long memberId, Long scheduleId);
    Optional<ScheduleMember> findScheduleMemberWithMemberById(Long scheduleMemberId);
    int findPointAmountOfLatePaidScheduleMember(Long scheduleId);
    List<ScheduleMember> findPaidEarlyScheduleMemberWithMember(Long scheduleId);
    List<ScheduleMember> findPaidLateScheduleMemberWithMember(Long scheduleId);
    List<ScheduleMember> findWaitScheduleMemberWithScheduleInTeam(Long memberId, Long teamId);
    Optional<ScheduleMember> findNextScheduleOwner(Long scheduleId, Long prevOwnerScheduleMemberId);
}
