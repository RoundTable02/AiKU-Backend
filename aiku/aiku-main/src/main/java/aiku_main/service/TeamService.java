package aiku_main.service;

import aiku_main.dto.TeamAddDto;
import aiku_main.repository.TeamRepository;
import common.domain.Member;
import common.domain.Team;
import common.exception.BaseExceptionImpl;
import common.exception.NoAuthorityException;
import common.response.status.BaseErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Long enterTeam(Member member, Long teamId) {
        //검증 로직
        checkTeamMember(member.getId(), teamId, false);

        //서비스 로직
        Team team = teamRepository.findById(teamId).orElseThrow();
        team.addTeamMember(member, false);

        //TODO 푸시 알람

        return team.getId();
    }

    public Long exitTeam(Member member, Long teamId) {
        return null;
    }

    private void checkTeamMember(Long memberId, Long teamId, boolean isMember){
        if(teamRepository.existTeamMember(memberId, teamId) != isMember){
            if(isMember){
                throw new NoAuthorityException();
            }else {
                throw new BaseExceptionImpl(BaseErrorCode.AlreadyInTeam);
            }
        }
    }
}
