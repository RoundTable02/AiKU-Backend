package aiku_main.repository;

import aiku_main.dto.TeamEachListResDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static common.domain.ExecStatus.TERM;
import static common.domain.QSchedule.schedule;
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

    @Override
    public List<TeamEachListResDto> getTeamList(Long memberId, int page) {
        QSchedule subSchedule = new QSchedule("subSchedule");

        List<Long> teamIdList = query.select(teamMember.id)
                .from(teamMember)
                .where(teamMember.member.id.eq(memberId))
                .offset(getOffset(page))
                .limit(10)
                .fetch();

        return query.select(Projections.constructor(TeamEachListResDto.class,
                        team.id, team.teamName, teamMember.count(),
                        schedule.scheduleTime))
                .from(team)
                .leftJoin(team.teamMembers, teamMember).on(teamMember.team.id.eq(team.id))
                .leftJoin(schedule).on(schedule.team.id.eq(team.id))
                .where(team.id.in(teamIdList),
                        team.status.eq(ALIVE),
                        teamMember.status.eq(ALIVE),
                        schedule.scheduleTime.eq(
                                JPAExpressions.select(subSchedule.scheduleTime.max())
                                        .from(subSchedule)
                                        .where(subSchedule.team.id.eq(team.id),
                                                subSchedule.status.eq(ALIVE),
                                                subSchedule.scheduleStatus.eq(TERM))
                        ),
                        schedule.status.eq(ALIVE),
                        schedule.scheduleStatus.eq(TERM))
                .groupBy(team.id)
                .orderBy(schedule.scheduleTime.desc())
                .fetch();
    }

    private int getOffset(int page){
        return (page - 1) * 10;
    }
}
