package aiku_main.repository;

import common.domain.team.Team;
import common.domain.team.TeamMember;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepositoryCustom {

    Optional<Team> findTeamWithMember(Long teamId);

    boolean existTeamMember(@Param("memberId") Long memberId, @Param("teamId") Long teamId);
    Long countOfAliveTeamMember(Long teamId);
    Optional<TeamMember> findAliveTeamMember(Long teamId, Long memberId);
    Optional<TeamMember> findTeamMember(Long teamId, Long memberId);
}
