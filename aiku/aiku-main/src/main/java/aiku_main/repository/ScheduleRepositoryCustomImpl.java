package aiku_main.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.Schedule;
import common.domain.ScheduleMember;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static common.domain.ExecStatus.RUN;
import static common.domain.QSchedule.schedule;
import static common.domain.QScheduleMember.scheduleMember;
import static common.domain.Status.ALIVE;

@RequiredArgsConstructor
public class ScheduleRepositoryCustomImpl implements ScheduleRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<Schedule> findMemberScheduleInTeamWithMember(Long memberId, Long teamId) {
        return query.selectFrom(schedule)
                .innerJoin(schedule.scheduleMembers, scheduleMember).fetchJoin()
                .where(schedule.team.id.eq(teamId),
                        scheduleMember.status.eq(ALIVE),
                        schedule.status.eq(ALIVE))
                .fetch();
    }

    @Override
    public boolean isScheduleOwner(Long memberId, Long scheduleId) {
        Long count = query.select(scheduleMember.count())
                .from(scheduleMember)
                .where(scheduleMember.member.id.eq(memberId),
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.isOwner.isTrue(),
                        scheduleMember.status.eq(ALIVE))
                .fetchOne();
        return count != null && count > 0;
    }

    @Override
    public boolean existScheduleMember(Long memberId, Long scheduleId) {
        Long count = query.select(scheduleMember.count())
                .from(scheduleMember)
                .where(scheduleMember.member.id.eq(memberId),
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE))
                .fetchOne();
        return count != null && count > 0;
    }

    @Override
    public Long countOfAliveScheduleMember(Long scheduleId) {
        return query.select(scheduleMember.count())
                .from(scheduleMember)
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE))
                .fetchFirst();
    }

    @Override
    public Optional<ScheduleMember> findAliveScheduleMember(Long memberId, Long scheduleId) {
        ScheduleMember findScheduleMember = query.selectFrom(scheduleMember)
                .where(scheduleMember.member.id.eq(memberId),
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE))
                .fetchOne();

        return Optional.ofNullable(findScheduleMember);
    }

    @Override
    public Optional<ScheduleMember> findNextScheduleOwner(Long scheduleId, Long prevOwnerScheduleMemberId) {
        ScheduleMember findScheduleMember = query.selectFrom(scheduleMember)
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE),
                        scheduleMember.id.ne(prevOwnerScheduleMemberId))
                .fetchFirst();

        return Optional.ofNullable(findScheduleMember);
    }
}
