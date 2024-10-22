package map.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.kafka_message.alarm.AlarmMemberInfo;
import lombok.RequiredArgsConstructor;
import map.dto.RacerResDto;
import map.dto.RacingResDto;

import java.util.List;

import static common.domain.QRacing.racing;
import static common.domain.member.QMember.member;
import static common.domain.schedule.QSchedule.schedule;
import static common.domain.schedule.QScheduleMember.scheduleMember;

@RequiredArgsConstructor
public class RacingQueryRepositoryImpl implements RacingQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public List<RacingResDto> getAllRunningRacingsInSchedule(Long scheduleId) {
        return query.select(Projections.constructor(RacingResDto.class,
                Projections.constructor(RacerResDto.class,
                        scheduleMember.member.id,
                        scheduleMember.member.nickname,
                        scheduleMember.member.profile
                ),
                Projections.constructor(RacerResDto.class,
                        scheduleMember.member.id,
                        scheduleMember.member.nickname,
                        scheduleMember.member.profile
                ),
                racing.createdAt))
                .from(racing)
                .join(scheduleMember).on(scheduleMember.id.eq(racing.firstRacer.id))
                .join(scheduleMember.member, member)
                .join(scheduleMember).on(scheduleMember.id.eq(racing.secondRacer.id))
                .join(scheduleMember.member, member)
                .where(scheduleMember.schedule.id.eq(scheduleId))
                .fetch();
    }

    @Override
    public boolean checkBothMemberHaveEnoughRacingPoint(Long racingId) {
        Tuple tuple = query.select(member.point.as("firstPoint"),
                        member.point.as("secondPoint"),
                        racing.pointAmount.as("pointAmount"))
                .from(racing)
                .join(scheduleMember).on(scheduleMember.id.eq(racing.firstRacer.id))
                .join(scheduleMember.member, member)
                .join(scheduleMember).on(scheduleMember.id.eq(racing.secondRacer.id))
                .join(scheduleMember.member, member)
                .where(racing.id.eq(racingId))
                .fetchOne();

        Integer firstPoint = tuple.get(0, Integer.class);
        Integer secondPoint = tuple.get(1, Integer.class);
        Integer pointAmount = tuple.get(2, Integer.class);

        return firstPoint >= pointAmount && secondPoint >= pointAmount;
    }

    @Override
    public List<AlarmMemberInfo> findMemberInfoByScheduleMemberId(Long racingId) {
        return query.select(Projections.constructor(AlarmMemberInfo.class,
                        member.id,
                        member.nickname,
                        member.profile,
                        member.firebaseToken
                ))
                .from(racing)
                .join(scheduleMember).on(scheduleMember.id.eq(racing.firstRacer.id))
                .join(scheduleMember.member, member)
                .join(scheduleMember).on(scheduleMember.id.eq(racing.secondRacer.id))
                .join(scheduleMember.member, member)
                .where(racing.id.eq(racingId))
                .fetch();
    }

    @Override
    public boolean checkMemberIsSecondRacerInRacing(Long memberId, Long racingId) {
        Long count = query.select(member.count())
                .from(racing)
                .join(scheduleMember).on(scheduleMember.id.eq(racing.secondRacer.id))
                .join(scheduleMember.member, member)
                .where(member.id.eq(memberId), racing.id.eq(racingId))
                .fetchOne();

        return count != null && count > 0;
    }
}
