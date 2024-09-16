package aiku_main.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.QBetting;
import common.domain.QScheduleMember;
import common.domain.Status;
import common.domain.value_reference.ScheduleMemberValue;
import lombok.RequiredArgsConstructor;

import static common.domain.QBetting.betting;
import static common.domain.QScheduleMember.scheduleMember;
import static common.domain.Status.ALIVE;

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
}
