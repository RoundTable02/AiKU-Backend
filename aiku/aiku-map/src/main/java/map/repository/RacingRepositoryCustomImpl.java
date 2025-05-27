package map.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.ExecStatus;
import common.domain.Status;
import common.domain.member.QMember;
import common.domain.schedule.QScheduleMember;
import common.domain.value_reference.ScheduleMemberValue;
import common.kafka_message.alarm.AlarmMemberInfo;
import lombok.RequiredArgsConstructor;
import map.dto.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static common.domain.member.QMember.member;
import static common.domain.racing.QRacing.racing;
import static common.domain.schedule.QScheduleMember.scheduleMember;

@RequiredArgsConstructor
public class RacingRepositoryCustomImpl implements RacingRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public void setWinnerAndTermRacingByScheduleMemberId(Long scheduleMemberId) {
        query.update(racing)
                .set(racing.raceStatus, ExecStatus.TERM)
                .set(racing.winner, new ScheduleMemberValue(scheduleMemberId))
                .where(racing.raceStatus.eq(ExecStatus.RUN),
                        racing.firstRacer.id.eq(scheduleMemberId)
                                .or(racing.secondRacer.id.eq(scheduleMemberId))
                )
                .execute();
    }

    @Override
    public void terminateRunningRacing(Long scheduleId) {
        query.update(racing)
                .set(racing.raceStatus, ExecStatus.TERM)
                .where(racing.firstRacer.id.in(
                        JPAExpressions.select(scheduleMember.id)
                                .from(scheduleMember)
                                .where(scheduleMember.schedule.id.eq(scheduleId))
                ).or(racing.secondRacer.id.in(
                        JPAExpressions.select(scheduleMember.id)
                                .from(scheduleMember)
                                .where(scheduleMember.schedule.id.eq(scheduleId))
                )))
                .execute();
    }

    @Override
    public void cancelRacing(Long racingId) {
        query.update(racing)
                .set(racing.status, Status.DELETE)
                .where(racing.id.eq(racingId))
                .execute();
    }

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


    @Override
    public List<RunningRacingDto> findRunningRacingsByScheduleMemberId(Long scheduleMemberId) {
        return query.select(Projections.constructor(RunningRacingDto.class,
                        racing.id, racing.firstRacer.id, racing.secondRacer.id, racing.pointAmount)
                )
                .from(racing)
                .where(racing.firstRacer.id.eq(scheduleMemberId)
                                .or(racing.secondRacer.id.eq(scheduleMemberId)),
                        racing.raceStatus.eq(ExecStatus.RUN)
                )
                .fetch();
    }

    @Override
    public List<TermRacingDto> findTermRacingIdsWithNoWinnerInSchedule(Long scheduleId) {
        return query.select(Projections.constructor(TermRacingDto.class,
                        racing.id, racing.firstRacer.id, racing.secondRacer.id, racing.pointAmount)
                )
                .from(racing)
                .join(scheduleMember).on(scheduleMember.id.eq(racing.firstRacer.id))
                .where(scheduleMember.schedule.id.eq(scheduleId),
                        racing.winner.isNull(),
                        racing.raceStatus.eq(ExecStatus.TERM)
                )
                .fetch();
    }

    @Override
    public List<String> findRacersFcmTokensInRacing(Long racingId) {
        QScheduleMember firstRacerMember = new QScheduleMember("firstRacerMember");
        QScheduleMember secondRacerMember = new QScheduleMember("secondRacerMember");

        QMember firstMember = new QMember("firstMember");
        QMember secondMember = new QMember("secondMember");

        Tuple tuple = query.select(firstMember.firebaseToken,
                        secondMember.firebaseToken)
                .from(racing)
                .join(firstRacerMember).on(firstRacerMember.id.eq(racing.firstRacer.id))
                .join(firstRacerMember.member, firstMember)
                .join(secondRacerMember).on(secondRacerMember.id.eq(racing.secondRacer.id))
                .join(secondRacerMember.member, secondMember)
                .where(racing.id.eq(racingId))
                .fetchOne();

        return List.of(tuple.get(0, String.class), tuple.get(1, String.class));
    }
}
