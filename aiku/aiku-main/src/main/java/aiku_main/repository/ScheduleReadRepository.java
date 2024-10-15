package aiku_main.repository;

import aiku_main.application_event.domain.ScheduleArrivalMember;
import aiku_main.dto.*;
import common.domain.ExecStatus;

import java.util.List;

public interface ScheduleReadRepository{

    List<ScheduleMemberResDto> getScheduleMembersWithMember(Long scheduleId);
    List<TeamScheduleListEachResDto> getTeamSchedules(Long teamId, Long memberId, SearchDateCond dateCond, int page);
    List<MemberScheduleListEachResDto> getMemberSchedules(Long memberId, SearchDateCond dateCond, int page);

    int countTeamScheduleByScheduleStatus(Long teamId, ExecStatus scheduleStatus, SearchDateCond dateCond);
    int countMemberScheduleByScheduleStatus(Long memberId, ExecStatus scheduleStatus, SearchDateCond dateCond);

    List<ScheduleArrivalMember> getScheduleArrivalResults(Long scheduleId);
}
