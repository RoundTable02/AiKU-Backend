package aiku_main.service;

import aiku_main.application_event.domain.TeamBettingResult;
import aiku_main.application_event.domain.TeamLateTimeResult;
import aiku_main.application_event.domain.TeamResultMember;
import aiku_main.application_event.publisher.TeamEventPublisher;
import aiku_main.dto.*;
import aiku_main.exception.TeamException;
import aiku_main.repository.*;
import aiku_main.repository.dto.TeamBettingResultMemberDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.domain.member.Member;
import common.domain.Status;
import common.domain.schedule.Schedule;
import common.domain.team.Team;
import common.domain.team.TeamMember;
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
    private final TeamReadRepository teamReadRepository;
    private final ScheduleRepository scheduleRepository;
    private final BettingReadRepository bettingReadRepository;
    private final TeamEventPublisher teamEventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long addTeam(Member member, TeamAddDto teamDto){
        Team team = Team.create(member, teamDto.getGroupName());
        teamRepository.save(team);

        return team.getId();
    }

    @Transactional
    public Long enterTeam(Member member, Long teamId) {
        //검증 로직
        Team team = findTeamById(teamId);
        checkTeamMember(member.getId(), teamId, false);

        //서비스 로직
        team.addTeamMember(member);

        //TODO 푸시 알람

        return team.getId();
    }

    @Transactional
    public Long exitTeam(Member member, Long teamId) {
        //검증 로직
        Team team = findTeamById(teamId);
        checkTeamMember(member.getId(), teamId, true);

        //서비스 로직
        Long teamMemberCount = teamRepository.countOfAliveTeamMember(teamId);
        if (teamMemberCount <= 1){
            team.delete();
        }

        TeamMember teamMember = teamRepository.findAliveTeamMember(teamId, member.getId()).orElseThrow();
        team.removeTeamMember(teamMember);

        teamEventPublisher.publishTeamExitEvent(member, team);

        return team.getId();
    }

    //== 조회 서비스 ==
    public TeamDetailResDto getTeamDetail(Member member, Long teamId) {
        //검증 로직
        checkExistTeam(teamId);
        checkTeamMember(member.getId(), teamId, true);

        //서비스 로직
        Team team = teamRepository.findTeamWithMember(teamId).orElseThrow();
        TeamDetailResDto resultDto = new TeamDetailResDto(team);

        return resultDto;
    }

    public DataResDto<List<TeamEachListResDto>> getTeamList(Member member, int page) {
        //서비스 로직
        List<TeamEachListResDto> data = teamReadRepository.getTeamList(member.getId(), page);
        DataResDto<List<TeamEachListResDto>> resultDto = new DataResDto<>(page, data);

        return resultDto;
    }

    public String getTeamLateTimeResult(Member member, Long teamId){
        //검증 로직
        Team team = findTeamById(teamId);
        checkTeamMember(member.getId(), teamId, true);

        //서비스 로직
        return team.getTeamResult().getLateTimeResult();
    }

    public String getTeamBettingResult(Member member, Long teamId){
        //검증 로직
        Team team = findTeamById(teamId);
        checkTeamMember(member.getId(), teamId, true);

        //서비스 로직
        return team.getTeamResult().getTeamBettingResult();
    }

    //==이벤트 핸들러==
    @Transactional
    public void analyzeLateTimeResult(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        Team team = teamRepository.findById(schedule.getTeam().getId()).orElseThrow();

        List<TeamResultMember> lateTeamMemberRanking = teamReadRepository.getTeamLateTimeResult(team.getId());
        TeamLateTimeResult teamLateTimeResult = new TeamLateTimeResult(team.getId(), lateTeamMemberRanking);

        try {
            team.setTeamLateResult(objectMapper.writeValueAsString(teamLateTimeResult));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void analyzeBettingResult(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        Team team = teamRepository.findById(schedule.getTeam().getId()).orElseThrow();

        Map<Long, List<TeamBettingResultMemberDto>> memberBettingsMap = bettingReadRepository.findMemberTermBettingsInTeam(team.getId());

        List<TeamResultMember> teamResultMembers = new ArrayList<>();
        memberBettingsMap.forEach((memberId, memberBettingList) -> {
            long count = memberBettingList.stream().filter(TeamBettingResultMemberDto::isWinner).count();
            int analysis = (int) ((double)count/memberBettingList.size() * 100);

            TeamBettingResultMemberDto data = memberBettingList.get(0);
            teamResultMembers.add(new TeamResultMember(memberId, data.getNickName(), data.getMemberProfile(), analysis, data.getIsTeamMember()));
        });

        teamResultMembers.sort(Comparator.comparingInt(TeamResultMember::getAnalysis).reversed());

        TeamBettingResult teamBettingResult = new TeamBettingResult(team.getId(), teamResultMembers);
        try {
            team.setTeamBettingResult(objectMapper.writeValueAsString(teamBettingResult));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void updateLateTimeResultOfExitMember(Long memberId, Long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
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

    private Team findTeamById(Long teamId){
        Team team = teamRepository.findByIdAndStatus(teamId, ALIVE).orElse(null);
        if (team == null) {
            throw new TeamException(NO_SUCH_TEAM);
        }
        return team;
    }

    private void checkExistTeam(Long teamId){
        if(!teamRepository.existsById(teamId)){
            throw new TeamException(NO_SUCH_TEAM);
        }
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
}
