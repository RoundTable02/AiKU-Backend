package aiku_main.repository;

import aiku_main.application_event.domain.ScheduleArrivalMember;
import aiku_main.dto.*;
import aiku_main.dto.schedule.MemberScheduleListEachResDto;
import aiku_main.dto.schedule.ScheduleMemberResDto;
import aiku_main.dto.schedule.SchedulePreviewResDto;
import aiku_main.dto.schedule.TeamScheduleListEachResDto;
import common.domain.ExecStatus;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import common.domain.schedule.ScheduleResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleQueryRepositoryCustom {

    Optional<Schedule> findScheduleWithResult(Long scheduleId);
    Optional<ScheduleMember> findScheduleMember(Long memberId, Long scheduleId);
    Optional<ScheduleMember> findScheduleMemberWithMemberById(Long scheduleMemberId);
    Optional<ScheduleMember> findNextScheduleOwnerWithMember(Long scheduleId, Long prevOwnerScheduleMemberId);
    Optional<ScheduleResult> findScheduleResult(Long scheduleId);
    List<ScheduleMember> findNotArriveScheduleMember(Long scheduleId);
    List<ScheduleMember> findEarlyScheduleMemberWithMember(Long scheduleId);
    List<ScheduleMember> findScheduleMemberListWithMember(Long scheduleId);
    List<ScheduleMember> findWaitScheduleMemberWithScheduleInTeam(Long memberId, Long teamId);
    List<ScheduleMember> findScheduleMembersWithMember(Long scheduleId);

    Optional<Long> findScheduleMemberId(Long memberId, Long scheduleId);
    Optional<Long> findMemberIdOfScheduleMember(Long scheduleMemberId);
    boolean isScheduleOwner(Long memberId, Long scheduleId);
    boolean existScheduleMember(Long memberId, Long scheduleId);
    boolean existPaidScheduleMember(Long memberId, Long scheduleId);
    boolean existRunScheduleOfMemberInTeam(Long memberId, Long teamId);
    Long countOfScheduleMembers(Long scheduleId);
    int findLateScheduleMemberCount(Long scheduleId);
    int countTeamScheduleByScheduleStatus(Long teamId, ExecStatus scheduleStatus, SearchDateCond dateCond);
    int countMemberScheduleByScheduleStatus(Long memberId, ExecStatus scheduleStatus, SearchDateCond dateCond);
    List<LocalDateTime> findScheduleDatesInMonth(Long memberId, int year, int month);
    List<String> findAlarmTokenListOfScheduleMembers(Long scheduleId, Long excludeMemberId);

    SchedulePreviewResDto getSchedulePreview(Long scheduleId);
    List<ScheduleMemberResDto> getScheduleMembersWithBettingInfo(Long memberId, Long scheduleId);
    List<TeamScheduleListEachResDto> getTeamSchedules(Long teamId, Long memberId, SearchDateCond dateCond, int page);
    List<MemberScheduleListEachResDto> getMemberSchedules(Long memberId, SearchDateCond dateCond, int page);
    List<ScheduleArrivalMember> getScheduleArrivalResults(Long scheduleId);
}
