package aiku_main.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static common.domain.ExecStatus.WAIT;
import static common.domain.Status.ALIVE;
import static common.domain.member.QMember.member;
import static common.domain.schedule.QSchedule.schedule;
import static common.domain.schedule.QScheduleMember.scheduleMember;

@RequiredArgsConstructor
public class ScheduleRepositoryCustomImpl implements ScheduleRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public Optional<Schedule> findScheduleWithNotArriveScheduleMember(Long scheduleId) {
        Schedule findSchedule = query.selectFrom(schedule)
                .join(schedule.scheduleMembers, scheduleMember).fetchJoin()
                .where(schedule.id.eq(scheduleId),
                        scheduleMember.arrivalTime.isNull(),
                        scheduleMember.status.eq(ALIVE))
                .fetchOne();

        return Optional.ofNullable(findSchedule);
    }

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
    public boolean existPaidScheduleMember(Long memberId, Long scheduleId) {
        Long count = query.select(scheduleMember.count())
                .from(scheduleMember)
                .where(scheduleMember.member.id.eq(memberId),
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.pointAmount.isNotNull(),
//                        scheduleMember.pointAmount.gt(0),
                        scheduleMember.status.eq(ALIVE))
                .fetchOne();
        return count != null && count > 0;
    }

    @Override
    public Long countOfScheduleMembers(Long scheduleId) {
        return query.select(scheduleMember.count())
                .from(scheduleMember)
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE))
                .fetchFirst();
    }

    @Override
    public Optional<ScheduleMember> findScheduleMember(Long memberId, Long scheduleId) {
        ScheduleMember findScheduleMember = query.selectFrom(scheduleMember)
                .where(scheduleMember.member.id.eq(memberId),
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE))
                .fetchOne();

        return Optional.ofNullable(findScheduleMember);
    }

    @Override
    public Optional<ScheduleMember> findScheduleMemberWithMemberById(Long scheduleMemberId) {
        ScheduleMember findScheduleMember = query.selectFrom(scheduleMember)
                .innerJoin(scheduleMember.member, member).fetchJoin()
                .where(scheduleMember.id.eq(scheduleMemberId))
                .fetchOne();

        return Optional.ofNullable(findScheduleMember);
    }

    @Override
    public int findPointAmountOfLatePaidScheduleMember(Long scheduleId) {
        return query.select(scheduleMember.pointAmount.sum().coalesce(0))
                .from(scheduleMember)
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.isPaid.isTrue(),
                        scheduleMember.arrivalTimeDiff.lt(0),
                        scheduleMember.status.eq(ALIVE))
                .fetchOne();
    }

    @Override
    public List<ScheduleMember> findPaidEarlyScheduleMemberWithMember(Long scheduleId) {
        return query.selectFrom(scheduleMember)
                .join(scheduleMember.member, member)
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.isPaid.isTrue(),
                        scheduleMember.arrivalTimeDiff.goe(0),
                        scheduleMember.status.eq(ALIVE))
                .fetch();
    }

    @Override
    public List<ScheduleMember> findPaidLateScheduleMemberWithMember(Long scheduleId) {
        return query.selectFrom(scheduleMember)
                .join(scheduleMember.member, member)
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.isPaid.isTrue(),
                        scheduleMember.arrivalTimeDiff.lt(0),
                        scheduleMember.status.eq(ALIVE))
                .fetch();
    }

    @Override
    public List<ScheduleMember> findWaitScheduleMemberWithScheduleInTeam(Long memberId, Long teamId) {
        return query.selectFrom(scheduleMember)
                .innerJoin(scheduleMember.schedule, schedule).fetchJoin()
                .where(scheduleMember.member.id.eq(memberId),
                        scheduleMember.status.eq(ALIVE),
                        schedule.team.id.eq(teamId),
                        schedule.scheduleStatus.eq(WAIT),
                        schedule.status.eq(ALIVE))
                .fetch();
    }

    @Override
    public List<ScheduleMember> findScheduleMembersWithMember(Long scheduleId) {
        return query.selectFrom(scheduleMember)
                .join(scheduleMember.member, member).fetchJoin()
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE))
                .fetch();
    }

    @Override
    public Optional<ScheduleMember> findNextScheduleOwnerWithMember(Long scheduleId, Long prevOwnerScheduleMemberId) {
        ScheduleMember findScheduleMember = query.selectFrom(scheduleMember)
                .join(scheduleMember.member, member).fetchJoin()
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE),
                        scheduleMember.id.ne(prevOwnerScheduleMemberId))
                .fetchFirst();

        return Optional.ofNullable(findScheduleMember);
    }
}
