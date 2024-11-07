package map.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.ExecStatus;
import common.domain.value_reference.ScheduleMemberValue;
import lombok.RequiredArgsConstructor;

import static common.domain.QRacing.racing;

@RequiredArgsConstructor
public class RacingCommandRepositoryCustomImpl implements RacingCommandRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public void terminateRacingsByScheduleMemberId(Long scheduleMemberId) {
        query.update(racing)
                .set(racing.raceStatus, ExecStatus.TERM)
                .set(racing.winner, new ScheduleMemberValue(scheduleMemberId))
                .where(racing.firstRacer.id.eq(scheduleMemberId)
                                .or(racing.secondRacer.id.eq(scheduleMemberId)),
                        racing.raceStatus.eq(ExecStatus.RUN)
                )
                .execute();
    }
}
