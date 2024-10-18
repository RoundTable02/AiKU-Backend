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
    Long countOfScheduleMembers(Long scheduleId);
    Optional<ScheduleMember> findScheduleMember(Long memberId, Long scheduleId);
    Optional<ScheduleMember> findScheduleMemberWithMemberById(Long scheduleMemberId);
    int findPointAmountOfLatePaidScheduleMember(Long scheduleId);
    List<ScheduleMember> findPaidEarlyScheduleMemberWithMember(Long scheduleId);
    List<ScheduleMember> findPaidLateScheduleMemberWithMember(Long scheduleId);
    List<ScheduleMember> findWaitScheduleMemberWithScheduleInTeam(Long memberId, Long teamId);
    List<ScheduleMember> findScheduleMembersWithMember(Long scheduleId);
    Optional<ScheduleMember> findNextScheduleOwnerWithMember(Long scheduleId, Long prevOwnerScheduleMemberId);
}
