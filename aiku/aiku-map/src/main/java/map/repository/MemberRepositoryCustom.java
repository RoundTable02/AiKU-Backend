package map.repository;

import common.kafka_message.alarm.AlarmMemberInfo;

import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<AlarmMemberInfo> findMemberInfo(Long memberId);

    boolean checkEnoughRacingPoint(Long memberId, Integer point);

    Optional<AlarmMemberInfo> findMemberInfoByScheduleMemberId(Long scheduleMemberId);
}
