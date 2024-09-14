package aiku_main.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.QTeam;
import common.domain.QTeamMember;
import common.domain.Status;
import common.domain.Team;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static common.domain.QTeam.team;
import static common.domain.QTeamMember.teamMember;
import static common.domain.Status.ALIVE;

@RequiredArgsConstructor
public class TeamReadRepositoryImpl implements TeamReadRepositoryCustom{

    private final JPAQueryFactory query;

    @Override
    public Optional<Team> findTeamWithMember(Long teamId) {
        return query.selectFrom(team)
                .leftJoin(team.teamMembers, teamMember).fetchJoin()
                .where(team.id.eq(teamId),
                        team.status.eq(ALIVE),
                        teamMember.status.eq(ALIVE))
                .stream().findFirst();
    }
}
