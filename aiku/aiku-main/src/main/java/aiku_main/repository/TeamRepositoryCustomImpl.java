package aiku_main.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.team.QTeam;
import common.domain.team.Team;
import common.domain.team.TeamMember;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static common.domain.Status.ALIVE;
import static common.domain.team.QTeam.team;
import static common.domain.team.QTeamMember.teamMember;

@RequiredArgsConstructor
public class TeamRepositoryCustomImpl implements TeamRepositoryCustom{

    private final JPAQueryFactory query;

    @Override
    public Optional<Team> findTeamWithMember(Long teamId) {
        Team findTeam = query.selectFrom(team)
                .innerJoin(team.teamMembers, teamMember).fetchJoin()
                .where(team.id.eq(teamId),
                        team.status.eq(ALIVE),
                        teamMember.status.eq(ALIVE))
                .fetchOne();
        return Optional.ofNullable(findTeam);
    }

    @Override
    public boolean existTeamMember(Long memberId, Long teamId) {
        Long count = query.select(teamMember.count())
                .from(teamMember)
                .where(teamMember.member.id.eq(memberId),
                        teamMember.team.id.eq(teamId),
                        teamMember.status.eq(ALIVE))
                .fetchFirst();

        return count != null && count > 0;
    }

    @Override
    public Long countOfAliveTeamMember(Long teamId) {
        return query
                .select(teamMember.count())
                .from(teamMember)
                .where(teamMember.team.id.eq(teamId),
                        teamMember.status.eq(ALIVE))
                .fetchOne();
    }

    @Override
    public Optional<TeamMember> findAliveTeamMember(Long teamId, Long memberId) {
        TeamMember result = query.selectFrom(teamMember)
                .where(teamMember.team.id.eq(teamId),
                        teamMember.member.id.eq(memberId),
                        teamMember.status.eq(ALIVE))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
