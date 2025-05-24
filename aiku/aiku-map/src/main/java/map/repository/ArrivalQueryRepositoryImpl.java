package map.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static common.domain.QArrival.arrival;
import static common.domain.schedule.QSchedule.schedule;
import static common.domain.schedule.QScheduleMember.scheduleMember;

@RequiredArgsConstructor
public class ArrivalQueryRepositoryImpl implements ArrivalQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public boolean isAllMembersInScheduleArrived(Long scheduleId) {
        Long memberCount = query.select(scheduleMember.count())
                .from(scheduleMember)
                .leftJoin(schedule).on(schedule.id.eq(scheduleId))
                .fetchOne();

        Long arrivedMemberCount = query.select(arrival.count())
                .from(arrival)
                .leftJoin(scheduleMember).on(scheduleMember.id.eq(arrival.scheduleMember.id))
                .leftJoin(schedule).on(schedule.id.eq(scheduleId))
                .fetchOne();

        if (arrivedMemberCount == null) {
            return false; // No arrivals found, so not all members have arrived
        }

        return arrivedMemberCount.equals(memberCount);
    }
}
