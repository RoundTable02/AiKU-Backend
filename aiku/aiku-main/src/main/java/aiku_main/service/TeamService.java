package aiku_main.service;

import aiku_main.dto.TeamAddDto;
import aiku_main.repository.TeamRepository;
import common.domain.Member;
import common.domain.Team;
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
        Team team = Team.create(teamDto.getGroupName());
        teamRepository.save(team);

        return team.getId();
    }
}
