package aiku_main.repository;

import aiku_main.dto.ScheduleMemberResDto;
import aiku_main.dto.SearchDateCond;
import aiku_main.dto.TeamScheduleListEachResDto;
import common.domain.ExecStatus;

import java.util.List;

public interface ScheduleReadRepository{
    List<ScheduleMemberResDto> getScheduleMembersWithMember(Long scheduleId);
    List<TeamScheduleListEachResDto> getTeamScheduleList(Long teamId, Long memberId, SearchDateCond dateCond, int page, Long totalCount);

    int countTeamScheduleByScheduleStatus(Long teamId, ExecStatus scheduleStatus, SearchDateCond dateCond);
}
