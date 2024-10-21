package map.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.kafka_message.alarm.AlarmMemberInfo;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static common.domain.member.QMember.member;

@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public Optional<AlarmMemberInfo> findMemberInfo(Long memberId) {
        AlarmMemberInfo alarmMemberInfo = query.select(Projections.constructor(AlarmMemberInfo.class,
                        member.id,
                        member.nickname,
                        member.profile,
                        member.firebaseToken))
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(alarmMemberInfo);
    }
}
