package aiku_main.repository;

import aiku_main.application_event.domain.ScheduleArrivalMember;
import aiku_main.dto.MemberScheduleListEachResDto;
import aiku_main.dto.ScheduleMemberResDto;
import aiku_main.dto.SearchDateCond;
import aiku_main.dto.TeamScheduleListEachResDto;
import common.domain.ExecStatus;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;

import java.util.List;
import java.util.Optional;

public interface ScheduleQueryRepositoryCustom {

    List<Schedule> findMemberScheduleInTeamWithMember(Long memberId, Long teamId);

    Optional<ScheduleMember> findScheduleMember(Long memberId, Long scheduleId);
    Optional<ScheduleMember> findScheduleMemberWithMemberById(Long scheduleMemberId);
    Optional<ScheduleMember> findNextScheduleOwnerWithMember(Long scheduleId, Long prevOwnerScheduleMemberId);
    List<ScheduleMember> findNotArriveScheduleMember(Long scheduleId);
    List<ScheduleMember> findPaidEarlyScheduleMemberWithMember(Long scheduleId);
    List<ScheduleMember> findPaidLateScheduleMemberWithMember(Long scheduleId);
    List<ScheduleMember> findWaitScheduleMemberWithScheduleInTeam(Long memberId, Long teamId);
    List<ScheduleMember> findScheduleMembersWithMember(Long scheduleId);

    boolean isScheduleOwner(Long memberId, Long scheduleId);
    boolean existScheduleMember(Long memberId, Long scheduleId);
    boolean existPaidScheduleMember(Long memberId, Long scheduleId);
    Long countOfScheduleMembers(Long scheduleId);
    int findPointAmountOfLatePaidScheduleMember(Long scheduleId);
    int countTeamScheduleByScheduleStatus(Long teamId, ExecStatus scheduleStatus, SearchDateCond dateCond);
    int countMemberScheduleByScheduleStatus(Long memberId, ExecStatus scheduleStatus, SearchDateCond dateCond);

    List<ScheduleMemberResDto> getScheduleMembersWithBettingInfo(Long memberId, Long scheduleId);
    List<TeamScheduleListEachResDto> getTeamSchedules(Long teamId, Long memberId, SearchDateCond dateCond, int page);
    List<MemberScheduleListEachResDto> getMemberSchedules(Long memberId, SearchDateCond dateCond, int page);
    List<ScheduleArrivalMember> getScheduleArrivalResults(Long scheduleId);
}
