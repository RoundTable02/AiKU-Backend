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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        QScheduleMember firstRacerMember = new QScheduleMember("firstRacerMember");  // 첫 번째 스케줄 멤버 별칭
        QScheduleMember secondRacerMember = new QScheduleMember("secondRacerMember"); // 두 번째 스케줄 멤버 별칭

        QMember firstMember = new QMember("firstMember"); // 첫 번째 멤버 별칭
        QMember secondMember = new QMember("secondMember"); // 두 번째 멤버 별칭

        Tuple tuple = query.select(firstMember.point.as("firstPoint"),
                        secondMember.point.as("secondPoint"),
                        racing.pointAmount.as("pointAmount"))
                .from(racing)
                .join(firstRacerMember).on(firstRacerMember.id.eq(racing.firstRacer.id)) // 첫 번째 레이서와 조인
                .join(firstRacerMember.member, firstMember) // 첫 번째 레이서의 멤버와 조인 (첫 번째 별칭 사용)
                .join(secondRacerMember).on(secondRacerMember.id.eq(racing.secondRacer.id)) // 두 번째 레이서와 조인
                .join(secondRacerMember.member, secondMember) // 두 번째 레이서의 멤버와 조인 (두 번째 별칭 사용)
                .where(racing.id.eq(racingId))
                .fetchOne();

        Integer firstPoint = tuple.get(0, Integer.class);
        Integer secondPoint = tuple.get(1, Integer.class);
        Integer pointAmount = tuple.get(2, Integer.class);

        return firstPoint >= pointAmount && secondPoint >= pointAmount;
    }

    @Override
    public List<AlarmMemberInfo> findMemberInfoByScheduleMemberId(Long racingId) {
        QScheduleMember firstRacerMember = new QScheduleMember("firstRacerMember");  // 첫 번째 스케줄 멤버 별칭
        QScheduleMember secondRacerMember = new QScheduleMember("secondRacerMember"); // 두 번째 스케줄 멤버 별칭

        QMember firstMember = new QMember("firstMember"); // 첫 번째 멤버 별칭
        QMember secondMember = new QMember("secondMember"); // 두 번째 멤버 별칭

        AlarmMemberInfo firstAlarmMemberInfo = query.select(Projections.constructor(AlarmMemberInfo.class,
                        firstMember.id,            // 첫 번째 레이서 멤버 ID
                        firstMember.nickname,      // 첫 번째 레이서 멤버 닉네임
                        firstMember.profile,       // 첫 번째 레이서 멤버 프로필
                        firstMember.firebaseToken  // 첫 번째 레이서 멤버 Firebase 토큰
                ))
                .from(racing)
                .join(firstRacerMember).on(firstRacerMember.id.eq(racing.firstRacer.id))  // 첫 번째 레이서와 조인
                .join(firstRacerMember.member, firstMember) // 첫 번째 레이서의 멤버와 조인
                .where(racing.id.eq(racingId))
                .fetchOne();

        AlarmMemberInfo secondAlarmMemberInfo = query.select(Projections.constructor(AlarmMemberInfo.class,
                        secondMember.id,           // 두 번째 레이서 멤버 ID
                        secondMember.nickname,     // 두 번째 레이서 멤버 닉네임
                        secondMember.profile,      // 두 번째 레이서 멤버 프로필
                        secondMember.firebaseToken // 두 번째 레이서 멤버 Firebase 토큰
                ))
                .from(racing)
                .join(secondRacerMember).on(secondRacerMember.id.eq(racing.secondRacer.id)) // 두 번째 레이서와 조인
                .join(secondRacerMember.member, secondMember) // 두 번째 레이서의 멤버와 조인
                .where(racing.id.eq(racingId))
                .fetchOne();

        if (Objects.isNull(firstAlarmMemberInfo) || Objects.isNull(secondAlarmMemberInfo)) {
            return Collections.emptyList();
        } else {
            return List.of(firstAlarmMemberInfo, secondAlarmMemberInfo);
        }
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

    @Override
    public boolean existsByFirstMemberIdAndSecondMemberId(Long scheduleId, Long firstMemberId, Long secondMemberId) {
        QScheduleMember firstRacerMember = new QScheduleMember("firstRacerMember");  // 첫 번째 스케줄 멤버 별칭
        QScheduleMember secondRacerMember = new QScheduleMember("secondRacerMember"); // 두 번째 스케줄 멤버 별칭

        QMember firstMember = new QMember("firstMember"); // 첫 번째 멤버 별칭
        QMember secondMember = new QMember("secondMember"); // 두 번째 멤버 별칭

        Long count = query.select(racing.count())
                .from(racing)
                .join(firstRacerMember).on(firstRacerMember.id.eq(racing.firstRacer.id)) // 첫 번째 레이서와 조인
                .join(firstRacerMember.member, firstMember) // 첫 번째 레이서의 멤버와 조인 (첫 번째 별칭 사용)
                .join(secondRacerMember).on(secondRacerMember.id.eq(racing.secondRacer.id)) // 두 번째 레이서와 조인
                .join(secondRacerMember.member, secondMember) // 두 번째 레이서의 멤버와 조인 (두 번째 별칭 사용)
                .where(
                        firstRacerMember.schedule.id.eq(scheduleId),
                        secondRacerMember.schedule.id.eq(scheduleId),
                        (firstMember.id.eq(firstMemberId).and(secondMember.id.eq(secondMemberId)))
                                .or(secondMember.id.eq(firstMemberId).and(firstMember.id.eq(secondMemberId)))
                )
                .fetchOne();

        return count != null && count > 0;
    }
}
