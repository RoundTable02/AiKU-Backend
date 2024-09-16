package aiku_main.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.Schedule;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    public boolean hasMemberRunScheduleInTeam(Long memberId, Long teamId) {
        Long count = query.select(schedule.count())
                .from(schedule)
                .innerJoin(scheduleMember).on(
                        scheduleMember.member.id.eq(memberId),
                        scheduleMember.schedule.id.eq(schedule.id),
                        scheduleMember.status.eq(ALIVE))
                .where(schedule.team.id.eq(teamId),
                        schedule.scheduleStatus.eq(RUN),
                        schedule.status.eq(ALIVE))
                .fetchOne();
        return count != null && count > 0;
    }


}
