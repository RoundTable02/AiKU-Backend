package aiku_main.repository;

import aiku_main.dto.*;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.ExecStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static common.domain.QSchedule.schedule;
import static common.domain.QScheduleMember.scheduleMember;
import static common.domain.Status.ALIVE;
import static common.domain.member.QMember.member;

@RequiredArgsConstructor
@Repository
public class ScheduleReadRepositoryImpl implements ScheduleReadRepository{

    private final JPAQueryFactory query;

    @Override
    public List<ScheduleMemberResDto> getScheduleMembersWithMember(Long scheduleId) {
        return query
                .select(Projections.constructor(ScheduleMemberResDto.class,
                        member.id, member.nickname,
                        Projections.constructor(MemberProfileResDto.class,
                                member.profile.profileType, member.profile.profileImg, member.profile.profileCharacter, member.profile.profileBackground),
                        member.point))
                .from(scheduleMember)
                .innerJoin(scheduleMember.member, member)
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE))
                .orderBy(scheduleMember.createdAt.asc())
                .fetch();
    }

    @Override
    public List<TeamScheduleListEachResDto> getTeamScheduleList(Long teamId, Long memberId, SearchDateCond dateCond, int page, TotalCountDto totalCount) {
        totalCount.setTotalCount(
                query
                .select(schedule.count())
                .from(schedule)
                .where(schedule.team.id.eq(teamId),
                        schedule.status.eq(ALIVE),
                        scheduleTimeGoe(dateCond.getStartDate()),
                        scheduleTimeLoe(dateCond.getEndDate()))
                .fetchFirst());

        List<Long> scheduleIdList = query
                .select(schedule.id)
                .from(schedule)
                .where(schedule.team.id.eq(teamId),
                        schedule.status.eq(ALIVE),
                        scheduleTimeGoe(dateCond.getStartDate()),
                        scheduleTimeLoe(dateCond.getEndDate()))
                .offset(getOffset(page))
                .limit(10)
                .fetch();

        return query
                .select(Projections.constructor(TeamScheduleListEachResDto.class,
                        schedule.id, schedule.scheduleName,
                        Projections.constructor(LocationDto.class,
                                schedule.location.locationName, schedule.location.latitude, schedule.location.longitude),
                        schedule.scheduleTime, schedule.scheduleStatus,
                        Expressions.stringTemplate("STRING_AGG({0}, ',')", scheduleMember.member.id)))
                .from(schedule)
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
