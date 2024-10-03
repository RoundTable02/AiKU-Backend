package aiku_main.repository;

import aiku_main.application_event.domain.ScheduleArrivalMember;
import aiku_main.dto.*;
import common.domain.ExecStatus;
import aiku_main.application_event.domain.ScheduleArrivalResult;

import java.util.List;

public interface ScheduleReadRepository{

    List<ScheduleMemberResDto> getScheduleMembersWithMember(Long scheduleId);
    List<TeamScheduleListEachResDto> getTeamScheduleList(Long teamId, Long memberId, SearchDateCond dateCond, int page);
    List<MemberScheduleListEachResDto> getMemberScheduleList(Long memberId, SearchDateCond dateCond, int page);

    int countTeamScheduleByScheduleStatus(Long teamId, ExecStatus scheduleStatus, SearchDateCond dateCond);
    int countMemberScheduleByScheduleStatus(Long memberId, ExecStatus scheduleStatus, SearchDateCond dateCond);

    List<ScheduleArrivalMember> getScheduleArrivalResults(Long scheduleId);
}
