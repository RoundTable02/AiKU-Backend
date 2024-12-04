package aiku_main.service;

import aiku_main.dto.team.*;
import aiku_main.application_event.publisher.TeamEventPublisher;
import aiku_main.dto.*;
import aiku_main.exception.MemberNotFoundException;
import aiku_main.exception.TeamException;
import aiku_main.repository.*;
import aiku_main.repository.dto.TeamBettingResultMemberDto;
import aiku_main.repository.dto.TeamRacingResultMemberDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.domain.member.Member;
import common.domain.schedule.Schedule;
import common.domain.team.Team;
import common.domain.team.TeamMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static common.domain.Status.ALIVE;
import static common.response.status.BaseErrorCode.*;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TeamService {

    private final TeamQueryRepository teamQueryRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final BettingQueryRepository bettingQueryRepository;
    private final RacingQueryRepository racingQueryRepository;
    private final MemberRepository memberRepository;
    private final TeamEventPublisher teamEventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long addTeam(Long memberId, TeamAddDto teamDto){
        Member member = findMember(memberId);

        Team team = Team.create(member, teamDto.getGroupName());
        teamQueryRepository.save(team);

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

        Long teamMemberCount = teamQueryRepository.countOfTeamMember(teamId);
        if (teamMemberCount <= 1){
            team.delete();
        }

        TeamMember teamMember = findTeamMember(memberId, teamId);
        team.removeTeamMember(teamMember);

        teamEventPublisher.publishTeamExitEvent(member, team);

        return team.getId();
    }

    public TeamDetailResDto getTeamDetail(Long memberId, Long teamId) {
        Team team = findTeam(teamId);
        checkTeamMember(memberId, teamId, true);

        List<TeamMemberResDto> teamMemberList = teamQueryRepository.getTeamMemberList(teamId);

        return new TeamDetailResDto(teamId, team.getTeamName(), teamMemberList);
    }

    public DataResDto<List<TeamResDto>> getTeamList(Long memberId, int page) {
        List<TeamResDto> data = teamQueryRepository.getTeamList(memberId, page);

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

    @Transactional
    public void analyzeLateTimeResult(Long scheduleId) {
        Schedule schedule = scheduleQueryRepository.findById(scheduleId).orElseThrow();
        Team team = findTeamWithResult(schedule.getTeam().getId());

        List<TeamMemberResult> lateTeamMemberRanking = teamQueryRepository.getTeamLateTimeResult(team.getId());

        Map<Long, Integer> previousResultMembers = getPreviousLateTimeResult(team);
        int rank = 1;
        for (TeamMemberResult resultMember : lateTeamMemberRanking) {
            resultMember.setRank(rank++);
            resultMember.setPreviousRank(previousResultMembers.getOrDefault(resultMember.getMemberId(), -1));
        }

        TeamLateTimeResult teamLateTimeResult = new TeamLateTimeResult(team.getId(), lateTeamMemberRanking);

        try {
            team.setTeamLateResult(objectMapper.writeValueAsString(teamLateTimeResult));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Long, Integer> getPreviousLateTimeResult(Team team) {
        if(team.getTeamResult() != null && team.getTeamResult().getLateTimeResult() != null){
            try {
                TeamLateTimeResult previousResult = objectMapper.readValue(team.getTeamResult().getLateTimeResult(), TeamLateTimeResult.class);
                return previousResult.getMembers().stream()
                        .collect(Collectors.toMap(TeamMemberResult::getMemberId, teamMember -> teamMember.getRank()));
            } catch (JsonMappingException e) {
                throw new TeamException(CAN_NOT_PROCESS_JSON);
            } catch (JsonProcessingException e) {
                throw new TeamException(CAN_NOT_PROCESS_JSON);
            }
        }
        return new HashMap<>();
    }

    @Transactional
    public void analyzeBettingResult(Long scheduleId) {
        Schedule schedule = scheduleQueryRepository.findById(scheduleId).orElseThrow();
        Team team = findTeamWithResult(schedule.getTeam().getId());

        Map<Long, List<TeamBettingResultMemberDto>> memberBettingsMap = bettingQueryRepository.findMemberTermBettingsInTeam(team.getId());

        Map<Long, Integer> previousResult = getPreviousBettingResult(team);

        List<TeamMemberResult> teamMemberResults = new ArrayList<>();
        memberBettingsMap.forEach((memberId, memberBettingList) -> {
            long count = memberBettingList.stream().filter(TeamBettingResultMemberDto::isWinner).count();
            int analysis = (int) ((double)count/memberBettingList.size() * 100);

            TeamBettingResultMemberDto data = memberBettingList.get(0);
            teamMemberResults.add(new TeamMemberResult(memberId, data.getNickName(), data.getMemberProfile(), analysis, previousResult.getOrDefault(memberId, -1), data.getIsTeamMember()));
        });

        teamMemberResults.sort(Comparator.comparingInt(TeamMemberResult::getAnalysis).reversed());

        int rank = 1;
        for (TeamMemberResult resultMember : teamMemberResults) {
            resultMember.setRank(rank++);
        }

        TeamBettingResult teamBettingResult = new TeamBettingResult(team.getId(), teamMemberResults);
        try {
            team.setTeamBettingResult(objectMapper.writeValueAsString(teamBettingResult));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Long, Integer> getPreviousBettingResult(Team team) {
        if(team.getTeamResult() != null && team.getTeamResult().getTeamBettingResult() != null){
            try {
                TeamBettingResult previousResult = objectMapper.readValue(team.getTeamResult().getTeamBettingResult(), TeamBettingResult.class);
                return previousResult.getMembers().stream()
                        .collect(Collectors.toMap(TeamMemberResult::getMemberId, teamMember -> teamMember.getRank()));
            } catch (JsonMappingException e) {
                throw new TeamException(CAN_NOT_PROCESS_JSON);
            } catch (JsonProcessingException e) {
                throw new TeamException(CAN_NOT_PROCESS_JSON);
            }
        }
        return new HashMap<>();
    }

    @Transactional
    public void analyzeRacingResult(Long scheduleId) {
        Schedule schedule = scheduleQueryRepository.findById(scheduleId).orElseThrow();
        Team team = findTeamWithResult(schedule.getTeam().getId());

        Map<Long, List<TeamRacingResultMemberDto>> memberRacingsMap = racingQueryRepository.findMemberWithTermRacingsInTeam(team.getId());

        Map<Long, Integer> previousResult = getPreviousRacingResult(team);

        List<TeamMemberResult> teamMemberResults = new ArrayList<>();
        memberRacingsMap.forEach((memberId, memberRacingList) -> {
            long count = memberRacingList.stream().filter(TeamRacingResultMemberDto::isWinner).count();
            int analysis = (int) ((double) count / memberRacingList.size() * 100);

            TeamRacingResultMemberDto data = memberRacingList.get(0);
            teamMemberResults.add(new TeamMemberResult(memberId, data.getNickName(), data.getMemberProfile(), analysis, previousResult.getOrDefault(memberId, -1), data.getIsTeamMember()));
        });

        teamMemberResults.sort(Comparator.comparingInt(TeamMemberResult::getAnalysis).reversed());

        int rank = 1;
        for (TeamMemberResult resultMember : teamMemberResults) {
            resultMember.setRank(rank++);
        }

        TeamRacingResult teamRacingResult = new TeamRacingResult(team.getId(), teamMemberResults);
        try {
            team.setTeamRacingResult(objectMapper.writeValueAsString(teamRacingResult));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Long, Integer> getPreviousRacingResult(Team team) {
        if(team.getTeamResult() != null && team.getTeamResult().getTeamRacingResult() != null){
            try {
                TeamRacingResult previousResult = objectMapper.readValue(team.getTeamResult().getTeamRacingResult(), TeamRacingResult.class);
                return previousResult.getMembers().stream()
                        .collect(Collectors.toMap(TeamMemberResult::getMemberId, teamMember -> teamMember.getRank()));
            } catch (JsonMappingException e) {
                throw new TeamException(CAN_NOT_PROCESS_JSON);
            } catch (JsonProcessingException e) {
                throw new TeamException(CAN_NOT_PROCESS_JSON);
            }
        }
        return new HashMap<>();
    }

    @Transactional
    public void updateLateTimeResultOfExitMember(Long memberId, Long teamId) {
        Team team = findTeamWithResult(teamId);
        if (team.getTeamResult() == null || team.getTeamResult().getLateTimeResult() == null) {
            return;
        }

        try {
            TeamLateTimeResult result = objectMapper.readValue(team.getTeamResult().getLateTimeResult(), TeamLateTimeResult.class);
            result.getMembers().forEach(resultMember -> {
                if (resultMember.getMemberId().equals(memberId)) {
                    resultMember.setTeamMember(false);
                }
            });

            team.setTeamLateResult(objectMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Member findMember(Long memberId){
        return memberRepository.findByIdAndStatus(memberId, ALIVE)
                .orElseThrow(() -> new MemberNotFoundException());
    }

    private Team findTeam(Long teamId){
        return teamQueryRepository.findByIdAndStatus(teamId, ALIVE)
                .orElseThrow(() -> new TeamException(NO_SUCH_TEAM));
    }

    private Team findTeamWithResult(Long teamId){
        return teamQueryRepository.findTeamWithResult(teamId)
                .orElseThrow(() -> new TeamException(NO_SUCH_TEAM));
    }

    private TeamMember findTeamMember(Long memberId, Long teamId){
        return teamQueryRepository.findAliveTeamMember(teamId, memberId)
                .orElseThrow(() -> new TeamException(INTERNAL_SERVER_ERROR));
    }

    private void checkExistTeam(Long teamId){
        if(!teamQueryRepository.existsById(teamId)){
            throw new TeamException(NO_SUCH_TEAM);
        }
    }

    private void checkTeamMember(Long memberId, Long teamId, boolean isMember){
        if(teamQueryRepository.existTeamMember(memberId, teamId) != isMember){
            if(isMember){
                throw new TeamException(NOT_IN_TEAM);
            }else {
                throw new TeamException(ALREADY_IN_TEAM);
            }
        }
    }

    private void checkHasRunSchedule(Long memberId, Long teamId) {
        if(scheduleQueryRepository.existRunScheduleOfMemberInTeam(memberId, teamId)){
            throw new TeamException(CAN_NOT_EXIT);
        }
    }
}
