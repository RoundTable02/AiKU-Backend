package aiku_main.repository;

import aiku_main.application_event.domain.TeamResultMember;
import aiku_main.dto.TeamEachListResDto;
import common.domain.team.Team;
import common.domain.team.TeamMember;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamQueryRepositoryCustom {

    Optional<Team> findTeamWithMember(Long teamId);

    List<TeamMember> findTeamMembersWithMemberInTeam(Long teamId);
    Optional<TeamMember> findAliveTeamMember(Long teamId, Long memberId);
    Optional<TeamMember> findTeamMember(Long teamId, Long memberId);

    boolean existTeamMember(@Param("memberId") Long memberId, @Param("teamId") Long teamId);
    Long countOfAliveTeamMember(Long teamId);

    List<TeamEachListResDto> getTeamList(Long memberId, int page);
    List<TeamResultMember> getTeamLateTimeResult(Long teamId);
}
