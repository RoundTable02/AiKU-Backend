package map.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.schedule.ScheduleMember;
import common.kafka_message.alarm.AlarmMemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static common.domain.Status.ALIVE;
import static common.domain.member.QMember.member;
import static common.domain.schedule.QSchedule.schedule;
import static common.domain.schedule.QScheduleMember.scheduleMember;

@RequiredArgsConstructor
public class ScheduleQueryRepositoryImpl implements ScheduleQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public List<AlarmMemberInfo> findScheduleMemberInfosByScheduleId(Long scheduleId) {
        return query.select(Projections.constructor(AlarmMemberInfo.class,
                        member.id,
                        member.nickname,
                        member.profile,
                        member.firebaseToken))
                .from(schedule)
                .leftJoin(schedule.scheduleMembers, scheduleMember)
                .leftJoin(scheduleMember.member, member)
                .where(schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE))
                .fetch();
    }

    @Override
    public boolean existMemberInSchedule(Long memberId, Long scheduleId) {
        Long count = query.select(member.count())
                .from(schedule)
                .leftJoin(schedule.scheduleMembers, scheduleMember)
                .leftJoin(scheduleMember.member, member)
                .where(schedule.id.eq(scheduleId),
                        member.id.eq(memberId),
                        scheduleMember.status.eq(ALIVE))
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public Optional<ScheduleMember> findScheduleMember(Long memberId, Long scheduleId) {
        ScheduleMember scheduleMemberRes = query.select(scheduleMember)
                .from(schedule)
                .leftJoin(schedule.scheduleMembers, scheduleMember)
                .leftJoin(scheduleMember.member, member)
                .where(schedule.id.eq(scheduleId),
                        member.id.eq(memberId),
                        scheduleMember.status.eq(ALIVE))
                .fetchOne();

        return Optional.ofNullable(scheduleMemberRes);
    }

}
