package aiku_main.service;

import aiku_main.application_event.domain.TeamLateTimeResult;
import aiku_main.application_event.domain.TeamResultMember;
import aiku_main.application_event.publisher.TeamEventPublisher;
import aiku_main.dto.*;
import aiku_main.repository.ScheduleRepository;
import aiku_main.repository.TeamReadRepository;
import aiku_main.repository.TeamRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.domain.member.Member;
import common.domain.Status;
import common.domain.schedule.Schedule;
import common.domain.team.Team;
import common.domain.team.TeamMember;
import common.exception.BaseExceptionImpl;
import common.exception.NoAuthorityException;
import common.response.status.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamReadRepository teamReadRepository;
    private final ScheduleRepository scheduleRepository;
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
        checkTeamMember(member.getId(), teamId, false);

        Team team = teamRepository.findById(teamId).orElseThrow();
        checkIsAlive(team);

        //서비스 로직
        team.addTeamMember(member);

        //TODO 푸시 알람

        return team.getId();
    }

    @Transactional
    public Long exitTeam(Member member, Long teamId) {
        //검증 로직
        checkTeamMember(member.getId(), teamId, true);

        Team team = teamRepository.findById(teamId).orElseThrow();
        checkIsAlive(team);

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
        checkTeamMember(member.getId(), teamId, true);

        //서비스 로직
        Team team = teamReadRepository.findTeamWithMember(teamId).orElseThrow();
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
        checkTeamMember(member.getId(), teamId, true);
        Team team = teamRepository.findById(teamId).orElseThrow();

        //서비스 로직
        return team.getTeamResult().getLateTimeResult();
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

    //== 편의 메서드 ==
    private void checkTeamMember(Long memberId, Long teamId, boolean isMember){
        if(teamRepository.existTeamMember(memberId, teamId) != isMember){
            if(isMember){
                throw new NoAuthorityException();
            }else {
                throw new BaseExceptionImpl(BaseErrorCode.ALREADY_IN_TEAM);
            }
        }
    }

    private void checkIsAlive(Team team){
        if(team.getStatus() == Status.DELETE){
            throw new NoSuchElementException();
        }
    }
}
