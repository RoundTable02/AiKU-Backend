package aiku_main.repository;

import aiku_main.dto.*;
import common.domain.ExecStatus;

import java.util.List;

public interface ScheduleReadRepository{
    List<ScheduleMemberResDto> getScheduleMembersWithMember(Long scheduleId);
    List<TeamScheduleListEachResDto> getTeamScheduleList(Long teamId, Long memberId, SearchDateCond dateCond, int page, TotalCountDto totalCount);
    List<MemberScheduleListEachResDto> getMemberScheduleList(Long memberId, SearchDateCond dateCond, int page, TotalCountDto totalCount);

    int countTeamScheduleByScheduleStatus(Long teamId, ExecStatus scheduleStatus, SearchDateCond dateCond);
    int countMemberScheduleByScheduleStatus(Long memberId, ExecStatus scheduleStatus, SearchDateCond dateCond);

}
