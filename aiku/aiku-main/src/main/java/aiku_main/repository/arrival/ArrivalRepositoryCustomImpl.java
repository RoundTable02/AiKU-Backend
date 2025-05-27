package aiku_main.repository.arrival;

import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.Arrival;
import common.domain.QArrival;
import common.domain.schedule.QScheduleMember;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static common.domain.QArrival.arrival;
import static common.domain.schedule.QScheduleMember.scheduleMember;

@RequiredArgsConstructor
public class ArrivalRepositoryCustomImpl implements ArrivalRepositoryCustom{

    private final JPAQueryFactory query;

    @Override
    public List<Arrival> findArrivalsOfSchedule(Long scheduleId) {
        return query
                .selectFrom(arrival)
                .innerJoin(scheduleMember).on(scheduleMember.id.eq(arrival.scheduleMember.id))
                .where(scheduleMember.schedule.id.eq(scheduleId))
                .fetch();
    }
}
