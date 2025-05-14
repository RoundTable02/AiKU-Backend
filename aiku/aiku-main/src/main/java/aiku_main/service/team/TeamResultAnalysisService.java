package aiku_main.service.team;

import aiku_main.dto.team.result.late_time.TeamLateTimeResult;
import aiku_main.dto.team.result.late_time.TeamLateTimeResultDto;
import aiku_main.exception.TeamException;
import aiku_main.repository.schedule.ScheduleRepository;
import aiku_main.repository.team.TeamRepository;
import common.domain.schedule.Schedule;
import common.domain.team.Team;
import common.domain.team.TeamResult;
import common.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static common.response.status.BaseErrorCode.NO_SUCH_TEAM;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TeamResultAnalysisService {

    private final ScheduleRepository scheduleRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public void analyzeLateTimeResult(Long teamId) {
        Team team = teamRepository.findTeamWithResult(teamId).orElseThrow();

        List<TeamLateTimeResult> teamMembers = teamRepository.getTeamLateTimeResult(team.getId()); //1.지각 총 시간 내림차순, 2.스케줄 총 개수 내림차순
        TeamLateTimeResultDto teamLateTimeResultDto = new TeamLateTimeResultDto(team.getId(), teamMembers);

        team.setTeamLateResult(ObjectMapperUtil.toJson(teamLateTimeResultDto));
    }
}
