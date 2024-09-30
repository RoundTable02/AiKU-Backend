package aiku_main.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.Betting;
import common.domain.ExecStatus;
import common.domain.member.QMember;
import common.domain.team.QTeam;
import common.domain.team.QTeamMember;
import common.domain.value_reference.ScheduleMemberValue;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static common.domain.ExecStatus.TERM;
import static common.domain.QBetting.betting;
import static common.domain.Status.ALIVE;
import static common.domain.member.QMember.member;
import static common.domain.schedule.QScheduleMember.scheduleMember;
import static common.domain.team.QTeam.team;
import static common.domain.team.QTeamMember.teamMember;

@RequiredArgsConstructor
public class BettingRepositoryCustomImpl implements BettingRepositoryCustom{

    private final JPAQueryFactory query;

    @Override
    public boolean existBettorInSchedule(ScheduleMemberValue bettor, Long scheduleId) {
        Long count = query
                .select(betting.count())
                .from(betting)
                .join(scheduleMember).on(scheduleMember.id.eq(bettor.getId()))
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        betting.bettor.id.eq(bettor.getId()),
                        betting.status.eq(ALIVE))
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public List<Betting> findBettingsInSchedule(Long scheduleId, ExecStatus bettingStatus) {
        return query.selectFrom(betting)
                .join(scheduleMember).on(scheduleMember.id.eq(betting.bettor.id))
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE),
                        betting.bettingStatus.eq(bettingStatus),
                        betting.status.eq(ALIVE))
                .fetch();
    }
}
