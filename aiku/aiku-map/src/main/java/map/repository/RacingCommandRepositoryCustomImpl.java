package map.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.ExecStatus;
import common.domain.Status;
import common.domain.value_reference.ScheduleMemberValue;
import lombok.RequiredArgsConstructor;

import static common.domain.QRacing.racing;
import static common.domain.schedule.QScheduleMember.scheduleMember;

@RequiredArgsConstructor
public class RacingCommandRepositoryCustomImpl implements RacingCommandRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public void setWinnerAndTermRacingByScheduleMemberId(Long scheduleMemberId) {
        query.update(racing)
                .set(racing.raceStatus, ExecStatus.TERM)
                .set(racing.winner, new ScheduleMemberValue(scheduleMemberId))
                .where(racing.raceStatus.eq(ExecStatus.RUN),
                        racing.firstRacer.id.eq(scheduleMemberId)
                                .or(racing.secondRacer.id.eq(scheduleMemberId))
                )
                .execute();
    }

    @Override
    public void terminateRunningRacing(Long scheduleId) {
        query.update(racing)
                .set(racing.raceStatus, ExecStatus.TERM)
                .where(racing.firstRacer.id.in(
                        JPAExpressions.select(scheduleMember.id)
                                .from(scheduleMember)
                                .where(scheduleMember.schedule.id.eq(scheduleId))
                ).or(racing.secondRacer.id.in(
                        JPAExpressions.select(scheduleMember.id)
                                .from(scheduleMember)
                                .where(scheduleMember.schedule.id.eq(scheduleId))
                )))
                .execute();
    }

    @Override
    public void cancelRacing(Long racingId) {
        query.update(racing)
                .set(racing.status, Status.DELETE)
                .where(racing.id.eq(racingId))
                .execute();
    }
}
