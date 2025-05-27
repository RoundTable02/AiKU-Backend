package aiku_main.repository.schedule;

import aiku_main.dto.schedule.result.arrival_time.ScheduleArrivalResult;
import aiku_main.dto.*;
import aiku_main.dto.schedule.*;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.ExecStatus;
import common.domain.schedule.*;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static common.domain.ExecStatus.RUN;
import static common.domain.ExecStatus.WAIT;
import static common.domain.Status.ALIVE;
import static common.domain.betting.QBetting.betting;
import static common.domain.member.QMember.member;
import static common.domain.schedule.QSchedule.schedule;
import static common.domain.schedule.QScheduleMember.scheduleMember;
import static common.domain.schedule.QScheduleResult.scheduleResult;
import static common.domain.team.QTeam.team;

@RequiredArgsConstructor
public class ScheduleRepositoryCustomImpl implements ScheduleRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public boolean isScheduleOwner(Long memberId, Long scheduleId) {
        Long count = query
                .select(scheduleMember.count())
                .from(scheduleMember)
                .where(
                        scheduleMember.member.id.eq(memberId),
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.isOwner.isTrue(),
                        scheduleMember.status.eq(ALIVE)
                )
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public boolean existScheduleMember(Long memberId, Long scheduleId) {
        Long count = query
                .select(scheduleMember.count())
                .from(scheduleMember)
                .where(
                        scheduleMember.member.id.eq(memberId),
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE)
                )
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public boolean existRunScheduleOfMemberInTeam(Long memberId, Long teamId) {
        Long count = query
                .select(schedule.count())
                .from(scheduleMember)
                .innerJoin(scheduleMember.member, member)
                .innerJoin(scheduleMember.schedule, schedule)
                .where(
                        member.id.eq(memberId),
                        schedule.team.id.eq(teamId),
                        schedule.scheduleStatus.eq(RUN),
                        schedule.status.eq(ALIVE),
                        scheduleMember.status.eq(ALIVE)
                )
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public Long countOfScheduleMembers(Long scheduleId) {
        return query
                .select(scheduleMember.count())
                .from(scheduleMember)
                .where(
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE)
                )
                .fetchFirst();
    }

    @Override
    public Optional<Schedule> findScheduleWithResult(Long scheduleId) {
        Schedule fetchedSchedule = query
                .selectFrom(schedule)
                .leftJoin(schedule.scheduleResult, scheduleResult)
                .where(schedule.id.eq(scheduleId))
                .fetchOne();

        return Optional.ofNullable(fetchedSchedule);
    }

    @Override
    public Optional<ScheduleMember> findScheduleMember(Long memberId, Long scheduleId) {
        ScheduleMember findScheduleMember = query
                .selectFrom(scheduleMember)
                .where(
                        scheduleMember.member.id.eq(memberId),
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE)
                )
                .fetchOne();

        return Optional.ofNullable(findScheduleMember);
    }

    @Override
    public Optional<ScheduleMember> findScheduleMemberWithSchedule(Long memberId, Long scheduleId) {
        ScheduleMember findScheduleMember = query
                .selectFrom(scheduleMember)
                .innerJoin(scheduleMember.schedule, schedule).fetchJoin()
                .where(
                        scheduleMember.member.id.eq(memberId),
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE)
                )
                .fetchOne();

        return Optional.ofNullable(findScheduleMember);
    }

    @Override
    public int findLateScheduleMemberCount(Long scheduleId) {
        Long count = query
                .select(scheduleMember.count())
                .from(scheduleMember)
                .where(
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.arrivalTimeDiff.lt(0),
                        scheduleMember.status.eq(ALIVE)
                )
                .fetchOne();

        return count == null ? 0 : count.intValue();
    }

    @Override
    public List<ScheduleMember> findEarlyScheduleMemberWithMember(Long scheduleId) {
        return query
                .selectFrom(scheduleMember)
                .innerJoin(scheduleMember.member, member)
                .where(
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.arrivalTimeDiff.goe(0),
                        scheduleMember.status.eq(ALIVE)
                )
                .fetch();
    }

    @Override
    public List<ScheduleMember> findWaitScheduleMemberWithScheduleInTeam(Long memberId, Long teamId) {
        return query.selectFrom(scheduleMember)
                .innerJoin(scheduleMember.schedule, schedule).fetchJoin()
                .where(
                        scheduleMember.member.id.eq(memberId),
                        scheduleMember.status.eq(ALIVE),
                        schedule.team.id.eq(teamId),
                        schedule.scheduleStatus.eq(WAIT),
                        schedule.status.eq(ALIVE)
                )
                .fetch();
    }

    @Override
    public List<ScheduleMember> findScheduleMembersWithMember(Long scheduleId) {
        return query.selectFrom(scheduleMember)
                .innerJoin(scheduleMember.member, member).fetchJoin()
                .where(
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE)
                )
                .fetch();
    }

    @Override
    public Optional<Long> findScheduleMemberId(Long memberId, Long scheduleId) {
         Long id = query
                .select(scheduleMember.id)
                .from(scheduleMember)
                .where(
                        scheduleMember.member.id.eq(memberId),
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE)
                )
                .fetchOne();

        return Optional.ofNullable(id);
    }

    @Override
    public Optional<Long> findMemberIdOfScheduleMember(Long scheduleMemberId) {
        Long id = query
                .select(member.id)
                .from(scheduleMember)
                .innerJoin(scheduleMember.member, member)
                .where(scheduleMember.id.eq(scheduleMemberId))
                .fetchOne();

        return Optional.ofNullable(id);
    }

    @Override
    public Optional<ScheduleMember> findNextScheduleOwnerWithMember(Long scheduleId, Long prevOwnerScheduleMemberId) {
        ScheduleMember findScheduleMember = query
                .selectFrom(scheduleMember)
                .where(
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE),
                        scheduleMember.id.ne(prevOwnerScheduleMemberId)
                )
                .fetchFirst();

        return Optional.ofNullable(findScheduleMember);
    }

    @Override
    public Optional<ScheduleResult> findScheduleResult(Long scheduleId) {
        ScheduleResult fetchedScheduleResult = query
                .selectFrom(scheduleResult)
                .where(scheduleResult.schedule.id.eq(scheduleId))
                .fetchOne();

        return Optional.ofNullable(fetchedScheduleResult);
    }

    @Override
    public List<ScheduleMemberResDto> getScheduleMembersWithBettingInfo(Long memberId, Long scheduleId) {
        QScheduleMember subScheduleMember = new QScheduleMember("subScheduleMember");

        return query
                .select(Projections.constructor(
                        ScheduleMemberResDto.class,
                        member.id,
                        member.nickname,
                        Projections.constructor(
                                MemberProfileResDto.class,
                                member.profile.profileType,
                                member.profile.profileImg,
                                member.profile.profileCharacter,
                                member.profile.profileBackground
                        ),
                        scheduleMember.isOwner,
                        member.point,
                        betting.betee.id)
                )
                .from(scheduleMember)
                .innerJoin(scheduleMember.member, member)
                .leftJoin(betting).on(
                        betting.betee.id.eq(scheduleMember.id),
                        betting.bettor.id.eq(
                                JPAExpressions.select(subScheduleMember.id)
                                        .from(subScheduleMember)
                                        .where(subScheduleMember.member.id.eq(memberId))
                        )
                )
                .where(
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE)
                )
                .orderBy(scheduleMember.createdAt.asc())
                .fetch();
    }

    @Override
    public List<TeamScheduleListEachResDto> getTeamSchedules(Long teamId, Long memberId, SearchDateCond dateCond, int page) {
        List<Long> scheduleIdList = query
                .select(schedule.id)
                .from(schedule)
                .where(
                        schedule.team.id.eq(teamId),
                        schedule.status.eq(ALIVE),
                        scheduleTimeGoe(dateCond.getStartDate()),
                        scheduleTimeLoe(dateCond.getEndDate())
                )
                .offset(getOffset(page))
                .limit(11)
                .fetch();

        return query
                .select(Projections.constructor(
                        TeamScheduleListEachResDto.class,
                        schedule.id,
                        schedule.scheduleName,
                        Projections.constructor(
                                LocationDto.class,
                                schedule.location.locationName,
                                schedule.location.latitude,
                                schedule.location.longitude
                        ),
                        schedule.scheduleTime,
                        schedule.scheduleStatus,
                        Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})", scheduleMember.member.id)
                        )
                )
                .from(schedule)
                .leftJoin(scheduleMember).on(scheduleMember.schedule.id.eq(schedule.id))
                .where(
                        schedule.id.in(scheduleIdList),
                        scheduleMember.status.eq(ALIVE)
                )
                .groupBy(
                        schedule.id,
                        schedule.scheduleName,
                        schedule.location.locationName,
                        schedule.location.latitude,
                        schedule.location.longitude,
                        schedule.scheduleTime,
                        schedule.scheduleStatus
                )
                .orderBy(schedule.scheduleTime.desc())
                .fetch();
    }

    @Override
    public List<MemberScheduleListEachResDto> getMemberSchedules(Long memberId, SearchDateCond dateCond, int page) {
        List<Long> scheduleIdList = query
                .select(schedule.id)
                .from(scheduleMember)
                .innerJoin(scheduleMember.schedule, schedule)
                .where(
                        scheduleMember.member.id.eq(memberId),
                        scheduleMember.status.eq(ALIVE),
                        schedule.status.eq(ALIVE),
                        scheduleTimeGoe(dateCond.getStartDate()),
                        scheduleTimeLoe(dateCond.getEndDate())
                )
                .offset(getOffset(page))
                .limit(11)
                .fetch();

        return query
                .select(Projections.constructor(
                        MemberScheduleListEachResDto.class,
                        team.id,
                        team.teamName,
                        schedule.id,
                        schedule.scheduleName,
                        Projections.constructor(
                                LocationDto.class,
                                schedule.location.locationName,
                                schedule.location.latitude,
                                schedule.location.longitude
                        ),
                        schedule.scheduleTime,
                        schedule.scheduleStatus,
                        scheduleMember.count().intValue()
                        )
                )
                .from(schedule)
                .innerJoin(team).on(schedule.team.id.eq(team.id))
                .leftJoin(scheduleMember).on(scheduleMember.schedule.id.eq(schedule.id))
                .where(
                        schedule.id.in(scheduleIdList),
                        scheduleMember.status.eq(ALIVE)
                )
                .groupBy(
                        schedule.id,
                        schedule.scheduleName,
                        schedule.location,
                        schedule.scheduleTime,
                        schedule.scheduleStatus
                )
                .orderBy(schedule.scheduleTime.desc())
                .fetch();
    }

    @Override
    public int countTeamScheduleByScheduleStatus(Long teamId, ExecStatus scheduleStatus, SearchDateCond dateCond) {
        return query
                .select(schedule.count())
                .from(schedule)
                .where(
                        schedule.team.id.eq(teamId),
                        schedule.scheduleStatus.eq(scheduleStatus),
                        schedule.status.eq(ALIVE),
                        scheduleTimeGoe(dateCond.getStartDate()),
                        scheduleTimeLoe(dateCond.getEndDate())
                )
                .fetchFirst()
                .intValue();
    }

    @Override
    public int countMemberScheduleByScheduleStatus(Long memberId, ExecStatus scheduleStatus, SearchDateCond dateCond) {
        return query
                .select(scheduleMember.count())
                .from(scheduleMember)
                .innerJoin(schedule).on(schedule.id.eq(scheduleMember.schedule.id))
                .where(
                        scheduleMember.member.id.eq(memberId),
                        scheduleMember.status.eq(ALIVE),
                        schedule.scheduleStatus.eq(scheduleStatus),
                        schedule.status.eq(ALIVE),
                        scheduleTimeGoe(dateCond.getStartDate()),
                        scheduleTimeLoe(dateCond.getEndDate())
                )
                .fetchFirst()
                .intValue();
    }

    @Override
    public List<LocalDateTime> findScheduleDatesInMonth(Long memberId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        return query
                .select(schedule.scheduleTime)
                .from(scheduleMember)
                .innerJoin(scheduleMember.member, member)
                .innerJoin(scheduleMember.schedule, schedule)
                .where(
                        member.id.eq(memberId),
                        schedule.scheduleTime.between(startOfMonth, endOfMonth),
                        schedule.status.eq(ALIVE),
                        scheduleMember.status.eq(ALIVE)
                )
                .orderBy(schedule.scheduleTime.asc())
                .fetch();
    }

    @Override
    public List<String> findAlarmTokenListOfScheduleMembers(Long scheduleId, Long excludeMemberId) {
        return query
                .select(member.firebaseToken)
                .from(scheduleMember)
                .innerJoin(scheduleMember.member, member)
                .where(
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE),
                        memberNotEqual(excludeMemberId)
                )
                .fetch();
    }

    @Override
    public SchedulePreviewResDto getSchedulePreview(Long scheduleId) {
        return query
                .select(Projections.constructor(
                        SchedulePreviewResDto.class,
                        schedule.id,
                        schedule.scheduleName,
                        schedule.scheduleTime,
                        Projections.constructor(
                                LocationDto.class,
                                schedule.location.locationName,
                                schedule.location.latitude,
                                schedule.location.longitude
                        ),
                        Projections.constructor(
                                ScheduleOwnerResDto.class,
                                member.id,
                                member.nickname,
                                Projections.constructor(
                                        MemberProfileResDto.class,
                                        member.profile.profileType,
                                        member.profile.profileImg,
                                        member.profile.profileCharacter,
                                        member.profile.profileBackground
                                )
                        )
                ))
                .from(schedule)
                .leftJoin(schedule.scheduleMembers, scheduleMember)
                .leftJoin(scheduleMember.member, member)
                .where(
                        schedule.id.eq(scheduleId),
                        schedule.status.eq(ALIVE),
                        scheduleMember.isOwner.isTrue(),
                        scheduleMember.status.eq(ALIVE)
                )
                .fetchOne();
    }

    @Override
    public List<ScheduleArrivalResult> getScheduleArrivalResults(Long scheduleId) {
        return query
                .select(Projections.constructor(
                        ScheduleArrivalResult.class,
                        member.id,
                        member.nickname,
                        Projections.constructor(
                                MemberProfileResDto.class,
                                member.profile.profileType,
                                member.profile.profileImg,
                                member.profile.profileCharacter,
                                member.profile.profileBackground
                        ),
                        scheduleMember.arrivalTimeDiff
                        )
                )
                .from(scheduleMember)
                .innerJoin(scheduleMember.member, member)
                .where(
                        scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE)
                )
                .orderBy(
                        scheduleMember.arrivalTime.asc(),
                        scheduleMember.id.asc()
                )
                .fetch();
    }

    private BooleanExpression scheduleTimeGoe(LocalDateTime startDate){
        return  (startDate != null) ? schedule.scheduleTime.goe(startDate) : null;
    }

    private BooleanExpression scheduleTimeLoe(LocalDateTime endDate){
        return  (endDate != null) ? schedule.scheduleTime.loe(endDate) : null;
    }

    private BooleanExpression memberNotEqual(Long id){
        return id != null ? member.id.ne(id) : null;
    }

    private int getOffset(int page){
        return (page - 1) * 10;
    }
}
