package map.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.schedule.ScheduleMember;
import common.kafka_message.alarm.AlarmMemberInfo;
import lombok.RequiredArgsConstructor;
import map.dto.MemberProfileDto;
import map.dto.ScheduleMemberResDto;

import java.util.List;
import java.util.Optional;

import static common.domain.QArrival.arrival;
import static common.domain.Status.ALIVE;
import static common.domain.member.QMember.member;
import static common.domain.schedule.QSchedule.schedule;
import static common.domain.schedule.QScheduleMember.scheduleMember;

@RequiredArgsConstructor
public class ScheduleRepositoryCustomImpl implements ScheduleRepositoryCustom {

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

    @Override
    public Optional<Long> findScheduleMemberIdByMemberAndScheduleId(Long memberId, Long scheduleId) {
        Long scheduleMemberId = query.select(scheduleMember.id)
                .from(schedule)
                .leftJoin(schedule.scheduleMembers, scheduleMember)
                .leftJoin(scheduleMember.member, member)
                .where(schedule.id.eq(scheduleId), member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(scheduleMemberId);
    }

    @Override
    public List<ScheduleMemberResDto> getScheduleMembersInfo(Long scheduleId) {
        return query.select(Projections.constructor(ScheduleMemberResDto.class,
                        member.id, member.nickname,
                        Projections.constructor(MemberProfileDto.class,
                                member.profile.profileType, member.profile.profileImg, member.profile.profileCharacter, member.profile.profileBackground),
                        scheduleMember.arrivalTime))
                .from(scheduleMember)
                .innerJoin(member).on(member.id.eq(scheduleMember.member.id))
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        scheduleMember.status.eq(ALIVE))
                .fetch();
    }

    @Override
    public List<String> findAllFcmTokensInSchedule(Long scheduleId) {
        return query.select(member.firebaseToken)
                .from(schedule)
                .leftJoin(schedule.scheduleMembers, scheduleMember)
                .leftJoin(scheduleMember.member, member)
                .where(schedule.id.eq(scheduleId))
                .fetch();
    }

    @Override
    public List<ScheduleMember> findScheduleMembersNotInArrivalByScheduleId(Long scheduleId) {
        return query.select(scheduleMember)
                .from(schedule)
                .leftJoin(schedule.scheduleMembers, scheduleMember)
                .leftJoin(arrival).on(scheduleMember.id.eq(arrival.scheduleMember.id))
                .where(schedule.id.eq(scheduleId),
                        arrival.isNull())
                .fetch();
    }

}
