package map.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.member.QMember;
import common.domain.schedule.QScheduleMember;
import common.kafka_message.alarm.AlarmMemberInfo;
import lombok.RequiredArgsConstructor;
import map.dto.MemberProfileDto;
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
        QScheduleMember firstRacerMember = new QScheduleMember("firstRacerMember");  // 첫 번째 스케줄 멤버 별칭
        QScheduleMember secondRacerMember = new QScheduleMember("secondRacerMember"); // 두 번째 스케줄 멤버 별칭

        QMember firstMember = new QMember("firstMember"); // 첫 번째 멤버 별칭
        QMember secondMember = new QMember("secondMember"); // 두 번째 멤버 별칭

        return query.select(Projections.constructor(RacingResDto.class,
                        Projections.constructor(RacerResDto.class,
                                firstRacerMember.member.id,
                                firstRacerMember.member.nickname,
                                Projections.constructor(MemberProfileDto.class,
                                        firstRacerMember.member.profile.profileType,
                                        firstRacerMember.member.profile.profileImg,
                                        firstRacerMember.member.profile.profileCharacter,
                                        firstRacerMember.member.profile.profileBackground
                                )
                        ),
                        Projections.constructor(RacerResDto.class,
                                secondRacerMember.member.id,
                                secondRacerMember.member.nickname,
                                Projections.constructor(MemberProfileDto.class,
                                        secondRacerMember.member.profile.profileType,
                                        secondRacerMember.member.profile.profileImg,
                                        secondRacerMember.member.profile.profileCharacter,
                                        secondRacerMember.member.profile.profileBackground
                                )
                        ),
                        racing.createdAt))
                .from(racing)
                .join(firstRacerMember).on(firstRacerMember.id.eq(racing.firstRacer.id)) // 첫 번째 레이서와 조인
                .join(firstRacerMember.member, firstMember) // 첫 번째 레이서의 멤버와 조인 (첫 번째 별칭 사용)
                .join(secondRacerMember).on(secondRacerMember.id.eq(racing.secondRacer.id)) // 두 번째 레이서와 조인
                .join(secondRacerMember.member, secondMember) // 두 번째 레이서의 멤버와 조인 (두 번째 별칭 사용)
                .where(firstRacerMember.schedule.id.eq(scheduleId)) // 스케줄 ID 조건
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
