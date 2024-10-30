package map.repository;

import common.kafka_message.alarm.AlarmMemberInfo;

import java.util.Optional;

public interface MemberQueryRepository {

    Optional<AlarmMemberInfo> findMemberInfo(Long memberId);

    boolean checkEnoughRacingPoint(Long memberId, Integer point);

}
