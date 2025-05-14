package aiku_main.service.team;

import aiku_main.dto.team.*;
import aiku_main.application_event.publisher.TeamEventPublisher;
import aiku_main.dto.*;
import aiku_main.dto.team.result.betting_odds.TeamBettingResultDto;
import aiku_main.dto.team.result.late_time.TeamLateTimeResultDto;
import aiku_main.exception.MemberNotFoundException;
import aiku_main.exception.TeamException;
import aiku_main.repository.betting.BettingRepository;
import aiku_main.repository.member.MemberRepository;
import aiku_main.repository.racing.RacingRepository;
import aiku_main.repository.schedule.ScheduleRepository;
import aiku_main.repository.team.TeamRepository;
import common.util.ObjectMapperUtil;
import common.domain.member.Member;
import common.domain.team.Team;
import common.domain.team.TeamMember;
import common.domain.team.TeamResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static common.domain.Status.ALIVE;
import static common.response.status.BaseErrorCode.*;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final ScheduleRepository scheduleRepository;
    private final BettingRepository bettingRepository;
    private final RacingRepository racingRepository;
    private final MemberRepository memberRepository;
    private final TeamEventPublisher teamEventPublisher;
    private final ObjectMapperUtil objectMapperUtil;

    @Transactional
    public Long addTeam(Long memberId, TeamAddDto teamDto){
        Member member = findMember(memberId);

        Team team = Team.create(member, teamDto.getGroupName());
        teamRepository.save(team);

        return team.getId();
    }

    @Transactional
    public Long enterTeam(Long memberId, Long teamId) {
        Member member = findMember(memberId);
        Team team = findTeam(teamId);
        checkTeamMember(member.getId(), teamId, false);

        team.addTeamMember(member);

        return team.getId();
    }

    @Transactional
    public Long exitTeam(Long memberId, Long teamId) {
        Member member = findMember(memberId);
        Team team = findTeam(teamId);
        checkTeamMember(memberId, teamId, true);
        checkHasRunSchedule(memberId, teamId);

        Long teamMemberCount = teamRepository.countOfTeamMember(teamId);
        if (teamMemberCount <= 1){
            team.delete();
        }

        TeamMember teamMember = findTeamMember(memberId, teamId);
        team.removeTeamMember(teamMember);

        teamEventPublisher.publishTeamExitEvent(memberId, teamId);

        return team.getId();
    }

    public TeamDetailResDto getTeamDetail(Long memberId, Long teamId) {
        Team team = findTeam(teamId);
        checkTeamMember(memberId, teamId, true);

        List<TeamMemberResDto> teamMemberList = teamRepository.getTeamMemberList(teamId);

        return new TeamDetailResDto(teamId, team.getTeamName(), teamMemberList);
    }

    public DataResDto<List<TeamResDto>> getTeamList(Long memberId, int page) {
        List<TeamResDto> data = teamRepository.getTeamList(memberId, page);

        return new DataResDto<>(page, data);
    }

    public String getTeamLateTimeResult(Long memberId, Long teamId){
        Team team = findTeamWithResult(teamId);
        checkTeamMember(memberId, teamId, true);

        return team.getTeamResult() == null? null : team.getTeamResult().getLateTimeResult();
    }

    public String getTeamBettingResult(Long memberId, Long teamId){
        Team team = findTeamWithResult(teamId);
        checkTeamMember(memberId, teamId, true);

        return team.getTeamResult() == null? null : team.getTeamResult().getTeamBettingResult();
    }

    public String getTeamRacingResult(Long memberId, Long teamId) {
        Team team = findTeamWithResult(teamId);
        checkTeamMember(memberId, teamId, true);

        return team.getTeamResult() == null? null : team.getTeamResult().getTeamRacingResult();
    }
/*
    private Map<Long, Integer> getPreviousBettingResult(TeamResult teamResult) {
        if(teamResult != null && teamResult.getTeamBettingResult() != null){
            TeamBettingResultDto previousResult = objectMapperUtil.parseJson(teamResult.getTeamBettingResult(), TeamBettingResultDto.class);

            return previousResult.getMembers().stream()
                    .collect(Collectors.toMap(
                            TeamLateTimeResult::getMemberId,
                            teamMember -> teamMember.getRank())
                    );
        }

        return new HashMap<>();
    }

    @Transactional
    public void analyzeRacingResult(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        Team team = findTeamWithResult(schedule.getTeam().getId());

        Map<Long, List<TeamRacingResultMemberDto>> memberRacingsMap = racingRepository.findMemberWithTermRacingsInTeam(team.getId());

        Map<Long, Integer> previousResult = getPreviousRacingResult(team.getTeamResult());

        List<TeamLateTimeResult> teamLateTimeResultMembers = new ArrayList<>();
        memberRacingsMap.forEach((memberId, memberRacingList) -> {
            long count = memberRacingList.stream().filter(TeamRacingResultMemberDto::isWinner).count();
            int analysis = (int) ((double) count / memberRacingList.size() * 100);

            TeamRacingResultMemberDto data = memberRacingList.get(0);
            teamLateTimeResultMembers.add(new TeamLateTimeResult(memberId, data.getNickName(), data.getMemberProfile(), analysis, previousResult.getOrDefault(memberId, -1), data.getIsTeamMember()));
        });

        teamLateTimeResultMembers.sort(Comparator.comparingInt(TeamLateTimeResult::getLateTime).reversed());

        int rank = 1;
        for (TeamLateTimeResult resultMember : teamLateTimeResultMembers) {
            resultMember.setRank(rank++);
        }

        TeamRacingResult teamRacingResult = new TeamRacingResult(team.getId(), teamLateTimeResultMembers);
        team.setTeamRacingResult(objectMapperUtil.toJson(teamRacingResult));
    }

    private Map<Long, Integer> getPreviousRacingResult(TeamResult teamResult) {
        if(teamResult != null && teamResult.getTeamRacingResult() != null){
            TeamRacingResult previousResult = objectMapperUtil.parseJson(teamResult.getTeamRacingResult(), TeamRacingResult.class);

            return previousResult.getMembers().stream()
                    .collect(Collectors.toMap(
                            TeamLateTimeResult::getMemberId,
                            teamMember -> teamMember.getRank())
                    );
        }

        return new HashMap<>();
    }*/

    @Transactional
    public void updateTeamResultOfExitMember(Long memberId, Long teamId) {
        Team team = findTeamWithResult(teamId);
        if (team.getTeamResult() == null) {
            return;
        }

        updateLateTimeResultOfExitMember(memberId, team);
        updateBettingTimeResultOfExitMember(memberId, team);
        updateRacingTimeResultOfExitMember(memberId, team);
    }

    private void updateLateTimeResultOfExitMember(Long memberId, Team team){
        TeamResult teamResult = team.getTeamResult();
        if (teamResult.getLateTimeResult() == null) {
            return;
        }

        TeamLateTimeResultDto result = objectMapperUtil.parseJson(teamResult.getLateTimeResult(), TeamLateTimeResultDto.class);
        result.getMembers().forEach(resultMember -> {
            if (resultMember.getMemberId().equals(memberId)) {
                resultMember.setTeamMember(false);
            }
        });

        team.setTeamLateResult(objectMapperUtil.toJson(result));
    }

    private void updateBettingTimeResultOfExitMember(Long memberId, Team team){
        TeamResult teamResult = team.getTeamResult();
        if (teamResult.getTeamBettingResult() == null) {
            return;
        }

        TeamBettingResultDto result = objectMapperUtil.parseJson(teamResult.getTeamBettingResult(), TeamBettingResultDto.class);
        result.getMembers().forEach(resultMember -> {
            if (resultMember.getMemberId().equals(memberId)) {
                resultMember.setTeamMember(false);
            }
        });

        team.setTeamBettingResult(objectMapperUtil.toJson(result));
    }

    private void updateRacingTimeResultOfExitMember(Long memberId, Team team){
        TeamResult teamResult = team.getTeamResult();
        if (teamResult.getTeamRacingResult() == null) {
            return;
        }

        TeamRacingResult result = objectMapperUtil.parseJson(teamResult.getTeamRacingResult(), TeamRacingResult.class);
        result.getMembers().forEach(resultMember -> {
            if (resultMember.getMemberId().equals(memberId)) {
                resultMember.setTeamMember(false);
            }
        });

        team.setTeamRacingResult(objectMapperUtil.toJson(result));
    }

    private Member findMember(Long memberId){
        return memberRepository.findByIdAndStatus(memberId, ALIVE)
                .orElseThrow(() -> new MemberNotFoundException());
    }

    private Team findTeam(Long teamId){
        return teamRepository.findByIdAndStatus(teamId, ALIVE)
                .orElseThrow(() -> new TeamException(NO_SUCH_TEAM));
    }

    private Team findTeamWithResult(Long teamId){
        return teamRepository.findTeamWithResult(teamId)
                .orElseThrow(() -> new TeamException(NO_SUCH_TEAM));
    }

    private TeamMember findTeamMember(Long memberId, Long teamId){
        return teamRepository.findTeamMember(teamId, memberId)
                .orElseThrow(() -> new TeamException(INTERNAL_SERVER_ERROR));
    }

    private void checkTeamMember(Long memberId, Long teamId, boolean isMember){
        if(teamRepository.existTeamMember(memberId, teamId) != isMember){
            if(isMember){
                throw new TeamException(NOT_IN_TEAM);
            }else {
                throw new TeamException(ALREADY_IN_TEAM);
            }
        }
    }

    private void checkHasRunSchedule(Long memberId, Long teamId) {
        if(scheduleRepository.existRunScheduleOfMemberInTeam(memberId, teamId)){
            throw new TeamException(CAN_NOT_EXIT);
        }
    }
}
