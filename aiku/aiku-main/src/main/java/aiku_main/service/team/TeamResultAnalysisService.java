package aiku_main.service.team;

import aiku_main.dto.team.result.betting_odds.TeamBettingResult;
import aiku_main.dto.team.result.betting_odds.TeamBettingResultDto;
import aiku_main.dto.team.result.late_time.TeamLateTimeResult;
import aiku_main.dto.team.result.late_time.TeamLateTimeResultDto;
import aiku_main.dto.team.result.racing_odds.TeamRacingResult;
import aiku_main.dto.team.result.racing_odds.TeamRacingResultDto;
import aiku_main.repository.team.TeamRepository;
import common.domain.team.Team;
import common.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TeamResultAnalysisService {

    private final TeamRepository teamRepository;

    @Transactional
    public void analyzeLateTimeResult(Long teamId) {
        Team team = teamRepository.findTeamWithResult(teamId).orElseThrow();

        List<TeamLateTimeResult> result = teamRepository.getTeamLateTimeResult(team.getId()); //1.지각 총 시간 내림차순, 2.스케줄 총 개수 내림차순
        TeamLateTimeResultDto teamLateTimeResultDto = new TeamLateTimeResultDto(team.getId(), result);

        team.setTeamLateResult(ObjectMapperUtil.toJson(teamLateTimeResultDto));
    }

    @Transactional
    public void analyzeBettingResult(Long teamId) {
        Team team = teamRepository.findTeamWithResult(teamId).orElseThrow();

        List<TeamBettingResult> results = teamRepository.getBettingWinOddsResult(teamId); //1.확률 내림차순, 2.베팅 총 개수 내림차순
        TeamBettingResultDto result = new TeamBettingResultDto(team.getId(), results);

        team.setTeamBettingResult(ObjectMapperUtil.toJson(result));
    }

    @Transactional
    public void analyzeRacingResult(Long teamId){
        Team team = teamRepository.findTeamWithResult(teamId).orElseThrow();

        List<TeamRacingResult> results = teamRepository.getRacingWinOddsResult(teamId);//1.확률 내림차순, 2.레이싱 총 개수 내림차순
        TeamRacingResultDto resultDto = new TeamRacingResultDto(team.getId(), results);

        team.setTeamRacingResult(ObjectMapperUtil.toJson(resultDto));
    }
}
