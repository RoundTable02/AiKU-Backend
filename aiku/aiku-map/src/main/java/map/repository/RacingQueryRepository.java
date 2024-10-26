package map.repository;

import common.kafka_message.alarm.AlarmMemberInfo;
import map.dto.RacingResDto;

import java.util.List;

public interface RacingQueryRepository {

    List<RacingResDto> getAllRunningRacingsInSchedule(Long scheduleId);

    boolean checkBothMemberHaveEnoughRacingPoint(Long racingId);

    List<AlarmMemberInfo> findMemberInfoByScheduleMemberId(Long racingId);

    boolean checkMemberIsSecondRacerInRacing(Long memberId, Long racingId);

    boolean existsByFirstMemberIdAndSecondMemberId(Long scheduleId, Long firstMemberId, Long secondMemberId);
}
