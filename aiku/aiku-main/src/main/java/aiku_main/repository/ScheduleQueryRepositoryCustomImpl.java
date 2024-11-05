package aiku_main.repository;

import aiku_main.application_event.domain.ScheduleArrivalMember;
import aiku_main.dto.*;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.ExecStatus;
import common.domain.QBetting;
import common.domain.schedule.QScheduleMember;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static common.domain.ExecStatus.WAIT;
import static common.domain.QBetting.betting;
import static common.domain.Status.ALIVE;
import static common.domain.member.QMember.member;
import static common.domain.schedule.QSchedule.schedule;
import static common.domain.schedule.QScheduleMember.scheduleMember;
import static common.domain.team.QTeam.team;

@RequiredArgsConstructor
public class ScheduleQueryRepositoryCustomImpl implements ScheduleQueryRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<ScheduleMember> findNotArriveScheduleMember(Long scheduleId) {
        return query.selectFrom(scheduleMember)
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.arrivalTime.isNull(),
                        scheduleMember.status.eq(ALIVE))
                .fetch();
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

    @Override
    public List<ScheduleMemberResDto> getScheduleMembersWithBettingInfo(Long memberId, Long scheduleId) {
        QScheduleMember subScheduleMember = new QScheduleMember("subScheduleMember");

        return query
                .select(Projections.constructor(ScheduleMemberResDto.class,
                        member.id, member.nickname,
                        Projections.constructor(MemberProfileResDto.class,
                                member.profile.profileType, member.profile.profileImg, member.profile.profileCharacter, member.profile.profileBackground),
                        member.point, betting.betee.id))
                .from(scheduleMember)
                .innerJoin(member).on(member.id.eq(scheduleMember.member.id))
                .leftJoin(betting).on(betting.betee.id.eq(scheduleMember.id),
                        betting.bettor.id.eq(
                                JPAExpressions.select(subScheduleMember.id)
                                        .from(subScheduleMember)
                                        .where(subScheduleMember.member.id.eq(memberId))
                        ))
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE))
                .orderBy(scheduleMember.createdAt.asc())
                .fetch();
    }

    @Override
    public List<TeamScheduleListEachResDto> getTeamSchedules(Long teamId, Long memberId, SearchDateCond dateCond, int page) {
        List<Long> scheduleIdList = query
                .select(schedule.id)
                .from(schedule)
                .where(schedule.team.id.eq(teamId),
                        schedule.status.eq(ALIVE),
                        scheduleTimeGoe(dateCond.getStartDate()),
                        scheduleTimeLoe(dateCond.getEndDate()))
                .offset(getOffset(page))
                .limit(11)
                .fetch();

        return query
                .select(Projections.constructor(TeamScheduleListEachResDto.class,
                        schedule.id, schedule.scheduleName,
                        Projections.constructor(LocationDto.class,
                                schedule.location.locationName, schedule.location.latitude, schedule.location.longitude),
                        schedule.scheduleTime, schedule.scheduleStatus,
                        Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})", scheduleMember.member.id)))
                .from(schedule)
                .innerJoin(scheduleMember).on(
                        scheduleMember.schedule.id.eq(schedule.id),
                        scheduleMember.status.eq(ALIVE))
                .where(schedule.id.in(scheduleIdList))
                .groupBy(schedule.id, schedule.scheduleName, schedule.location.locationName,
                        schedule.location.latitude, schedule.location.longitude,
                        schedule.scheduleTime, schedule.scheduleStatus)
                .orderBy(schedule.scheduleTime.desc())
                .fetch();
    }

    @Override
    public List<MemberScheduleListEachResDto> getMemberSchedules(Long memberId, SearchDateCond dateCond, int page) {
        List<Long> scheduleIdList = query
                .select(schedule.id)
                .from(scheduleMember)
                .innerJoin(schedule).on(schedule.id.eq(scheduleMember.schedule.id))
                .where(scheduleMember.member.id.eq(memberId),
                        scheduleMember.status.eq(ALIVE),
                        schedule.status.eq(ALIVE),
                        scheduleTimeGoe(dateCond.getStartDate()),
                        scheduleTimeLoe(dateCond.getEndDate()))
                .offset(getOffset(page))
                .limit(11)
                .fetch();

        return query
                .select(Projections.constructor(MemberScheduleListEachResDto.class,
                        team.id, team.teamName, schedule.id, schedule.scheduleName,
                        Projections.constructor(LocationDto.class,
                                schedule.location.locationName, schedule.location.latitude, schedule.location.longitude),
                        schedule.scheduleTime, schedule.scheduleStatus, scheduleMember.count().intValue()))
                .from(schedule)
                .innerJoin(team).on(schedule.team.id.eq(team.id))
                .leftJoin(scheduleMember).on(
                        scheduleMember.schedule.id.eq(schedule.id),
                        scheduleMember.status.eq(ALIVE))
                .where(schedule.id.in(scheduleIdList))
                .groupBy(schedule.id, schedule.scheduleName, schedule.location, schedule.scheduleTime, schedule.scheduleStatus)
                .orderBy(schedule.scheduleTime.desc())
                .fetch();
    }

    @Override
    public int countTeamScheduleByScheduleStatus(Long teamId, ExecStatus scheduleStatus, SearchDateCond dateCond) {
        return query
                .select(schedule.count())
                .from(schedule)
                .where(schedule.team.id.eq(teamId),
                        schedule.scheduleStatus.eq(scheduleStatus),
                        schedule.status.eq(ALIVE),
                        scheduleTimeGoe(dateCond.getStartDate()),
                        scheduleTimeLoe(dateCond.getEndDate()))
                .fetchFirst()
                .intValue();
    }

    @Override
    public int countMemberScheduleByScheduleStatus(Long memberId, ExecStatus scheduleStatus, SearchDateCond dateCond) {
        return query
                .select(scheduleMember.count())
                .from(scheduleMember)
                .innerJoin(schedule).on(
                        schedule.id.eq(scheduleMember.schedule.id))
                .where(scheduleMember.member.id.eq(memberId),
                        scheduleMember.status.eq(ALIVE),
                        schedule.scheduleStatus.eq(scheduleStatus),
                        schedule.status.eq(ALIVE),
                        scheduleTimeGoe(dateCond.getStartDate()),
                        scheduleTimeLoe(dateCond.getEndDate()))
                .fetchFirst()
                .intValue();
    }

    @Override
    public List<ScheduleArrivalMember> getScheduleArrivalResults(Long scheduleId) {
        return query
                .select(Projections.constructor(ScheduleArrivalMember.class,
                        member.id, member.nickname,
                        Projections.constructor(MemberProfileResDto.class,
                                member.profile.profileType, member.profile.profileImg, member.profile.profileCharacter, member.profile.profileBackground),
                        scheduleMember.arrivalTimeDiff))
                .from(scheduleMember)
                .join(member).on(member.id.eq(scheduleMember.member.id))
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE))
                .orderBy(scheduleMember.arrivalTime.asc(), scheduleMember.id.asc())
                .fetch();
    }

    private BooleanExpression scheduleTimeGoe(LocalDateTime startDate){
        return  (startDate != null) ? schedule.scheduleTime.goe(startDate) : null;
    }

    private BooleanExpression scheduleTimeLoe(LocalDateTime endDate){
        return  (endDate != null) ? schedule.scheduleTime.loe(endDate) : null;
    }

    private int getOffset(int page){
        return (page - 1) * 10;
    }
}
