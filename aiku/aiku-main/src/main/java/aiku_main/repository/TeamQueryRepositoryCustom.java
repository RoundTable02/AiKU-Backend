package aiku_main.repository;

import aiku_main.dto.team.TeamMemberResDto;
import aiku_main.dto.team.TeamResDto;
import aiku_main.dto.team.TeamMemberResult;
import common.domain.team.Team;
import common.domain.team.TeamMember;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamQueryRepositoryCustom {

    Optional<Team> findTeamWithMember(Long teamId);
    Optional<Team> findTeamWithResult(Long teamId);

    List<TeamMember> findTeamMembersWithMemberInTeam(Long teamId);
    Optional<TeamMember> findTeamMember(Long teamId, Long memberId);
    Optional<TeamMember> findDeletedTeamMember(Long teamId, Long memberId);

    boolean existTeamMember(@Param("memberId") Long memberId, @Param("teamId") Long teamId);
    Long countOfTeamMember(Long teamId);

    List<TeamMemberResDto> getTeamMemberList(Long teamId);
    List<TeamResDto> getTeamList(Long memberId, int page);
    List<TeamMemberResult> getTeamLateTimeResult(Long teamId);
}
