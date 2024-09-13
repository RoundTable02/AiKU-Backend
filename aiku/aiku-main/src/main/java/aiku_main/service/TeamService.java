package aiku_main.service;

import aiku_main.dto.TeamAddDto;
import aiku_main.dto.TeamDetailResDto;
import aiku_main.repository.TeamRepository;
import common.domain.Member;
import common.domain.Status;
import common.domain.Team;
import common.exception.BaseExceptionImpl;
import common.exception.NoAuthorityException;
import common.response.status.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TeamService {

    private final TeamRepository teamRepository;

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
        team.addTeamMember(member, false);

        //TODO 푸시 알람

        return team.getId();
    }

    @Transactional
    public Long exitTeam(Member member, Long teamId) {
        return null;
    }

    //== 조회 서비스 ==
    public TeamDetailResDto getTeamDetail(Member member, Long teamId) {
        //검증 로직
        checkTeamMember(member.getId(), teamId, true);

        //서비스 로직
        Team team = teamRepository.findTeamWithMember(teamId).orElseThrow();
        TeamDetailResDto resultDto = new TeamDetailResDto(team);

        return resultDto;
    }

    //== 편의 메서드 ==
    private void checkTeamMember(Long memberId, Long teamId, boolean isMember){
        if(teamRepository.existTeamMember(memberId, teamId) != isMember){
            if(isMember){
                throw new NoAuthorityException();
            }else {
                throw new BaseExceptionImpl(BaseErrorCode.AlreadyInTeam);
            }
        }
    }

    private void checkIsAlive(Team team){
        if(team.getStatus() == Status.DELETE){
            throw new NoSuchElementException();
        }
    }
}
